package geektime.im.lecture.redis;

import com.alibaba.fastjson.JSONObject;
import geektime.im.lecture.service.impl.UserServiceImpl;
import geektime.im.lecture.ws.handler.WebsocketRouterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class NewMessageListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private WebsocketRouterHandler websocketRouterHandler;

    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    private static final RedisSerializer<String> valueSerializer = new GenericToStringSerializer(Object.class);


    @Override
    public void onMessage(Message message, byte[] pattern) {
        String topic = stringRedisSerializer.deserialize(message.getChannel());
        String jsonMsg = valueSerializer.deserialize(message.getBody());
        logger.info("Message Received --> pattern: {}，topic:{}，message: {}", new String(pattern), topic, jsonMsg);
        JSONObject msgJson = JSONObject.parseObject(jsonMsg);
        long otherUid = msgJson.getLong("otherUid");
        JSONObject pushJson = new JSONObject();
        pushJson.put("type", 4);
        pushJson.put("data", msgJson);

        websocketRouterHandler.pushMsg(otherUid, pushJson);

    }
}
