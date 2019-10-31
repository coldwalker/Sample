package geektime.im.lecture.ws.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import geektime.im.lecture.service.MessageService;
import geektime.im.lecture.utils.EnhancedThreadFactory;
import geektime.im.lecture.vo.MessageVO;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 服务端处理所有接收消息的handler，这里只是示例，没有拆分太细，建议实际项目中按消息类型拆分到不同的handler中。
 */
@ChannelHandler.Sharable
@Component
public class WebsocketRouterHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private static final ConcurrentHashMap<Long, Channel> userChannel = new ConcurrentHashMap<>(15000);
    private static final ConcurrentHashMap<Channel, Long> channelUser = new ConcurrentHashMap<>(15000);
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(50, new EnhancedThreadFactory("ackCheckingThreadPool"));
    private static final Logger logger = LoggerFactory.getLogger(WebsocketRouterHandler.class);
    private static final AttributeKey<AtomicLong> TID_GENERATOR = AttributeKey.valueOf("tid_generator");
    private static final AttributeKey<ConcurrentHashMap> NON_ACKED_MAP = AttributeKey.valueOf("non_acked_map");

    @Autowired
    private MessageService messageService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            String msg = ((TextWebSocketFrame) frame).text();
            JSONObject msgJson = JSONObject.parseObject(msg);
            int type = msgJson.getIntValue("type");
            JSONObject data = msgJson.getJSONObject("data");
            switch (type) {
                case 0://心跳
                    long uid = data.getLong("uid");
                    long timeout = data.getLong("timeout");
                    logger.info("[heartbeat]: uid = {} , current timeout is {} ms, channel = {}", uid, timeout, ctx.channel());
                    ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":0,\"timeout\":" + timeout + "}"));
                    break;
                case 1://上线消息
                    long loginUid = data.getLong("uid");
                    userChannel.put(loginUid, ctx.channel());
                    channelUser.put(ctx.channel(), loginUid);
                    ctx.channel().attr(TID_GENERATOR).set(new AtomicLong(0));
                    ctx.channel().attr(NON_ACKED_MAP).set(new ConcurrentHashMap<Long, JSONObject>());
                    logger.info("[user bind]: uid = {} , channel = {}", loginUid, ctx.channel());
                    ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":1,\"status\":\"success\"}"));
                    break;
                case 2: //查询消息
                    long ownerUid = data.getLong("ownerUid");
                    long otherUid = data.getLong("otherUid");
                    List<MessageVO> messageVO = messageService.queryConversationMsg(ownerUid, otherUid);
                    String msgs = "";
                    if (messageVO != null) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type", 2);
                        jsonObject.put("data", JSONArray.toJSON(messageVO));
                        msgs = jsonObject.toJSONString();
                    }
                    ctx.writeAndFlush(new TextWebSocketFrame(msgs));
                    break;

                case 3: //发消息
                    long senderUid = data.getLong("senderUid");
                    long recipientUid = data.getLong("recipientUid");
                    String content = data.getString("content");
                    int msgType     = data.getIntValue("msgType");
                    MessageVO messageContent = messageService.sendNewMsg(senderUid, recipientUid, content, msgType);
                    if (messageContent != null) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type", 3);
                        jsonObject.put("data", JSONObject.toJSON(messageContent));
                        ctx.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(jsonObject)));
                    }
                    break;

                case 5: //查总未读
                    long unreadOwnerUid = data.getLong("uid");
                    long totalUnread = messageService.queryTotalUnread(unreadOwnerUid);
                    ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":5,\"data\":{\"unread\":" + totalUnread + "}}"));
                    break;

                case 6: //处理ack
                    long tid = data.getLong("tid");
                    ConcurrentHashMap<Long, JSONObject> nonAckedMap = ctx.channel().attr(NON_ACKED_MAP).get();
                    nonAckedMap.remove(tid);
                    break;
            }

        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("[channelActive]:remote address is {} ", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("[channelClosed]:remote address is {} ", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("process error. uid is {},  channel info {}", channelUser.get(ctx.channel()), ctx.channel(), cause);
        ctx.channel().close();
    }

    public void pushMsg(long recipientUid, JSONObject message) {
        Channel channel = userChannel.get(recipientUid);
        if (channel != null && channel.isActive() && channel.isWritable()) {
            AtomicLong generator = channel.attr(TID_GENERATOR).get();
            long tid = generator.incrementAndGet();
            message.put("tid", tid);
            channel.writeAndFlush(new TextWebSocketFrame(message.toJSONString())).addListener(future -> {
                if (future.isCancelled()) {
                    logger.warn("future has been cancelled. {}, channel: {}", message, channel);
                } else if (future.isSuccess()) {
                    addMsgToAckBuffer(channel, message);
                    logger.warn("future has been successfully pushed. {}, channel: {}", message, channel);
                } else {
                    logger.error("message write fail, {}, channel: {}", message, channel, future.cause());
                }
            });
        }
    }

    /**
     * 清除用户和socket映射的相关信息
     *
     * @param channel
     */
    public void cleanUserChannel(Channel channel) {
        long uid = channelUser.remove(channel);
        userChannel.remove(uid);
        logger.info("[cleanChannel]:remove uid & channel info from gateway, uid is {}, channel is {}", uid, channel);
    }

    /**
     * 将推送的消息加入待ack列表
     *
     * @param channel
     * @param msgJson
     */
    public void addMsgToAckBuffer(Channel channel, JSONObject msgJson) {
        channel.attr(NON_ACKED_MAP).get().put(msgJson.getLong("tid"), msgJson);
        executorService.schedule(() -> {
            if (channel.isActive()) {
                checkAndResend(channel, msgJson);
            }
        }, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * 检查并重推
     *
     * @param channel
     * @param msgJson
     */
    private void checkAndResend(Channel channel, JSONObject msgJson) {
        long tid = msgJson.getLong("tid");
        int tryTimes = 2;//重推2次
        while (tryTimes > 0) {
            if (channel.attr(NON_ACKED_MAP).get().containsKey(tid) && tryTimes > 0) {
                channel.writeAndFlush(new TextWebSocketFrame(msgJson.toJSONString()));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            tryTimes--;
        }
    }
}
