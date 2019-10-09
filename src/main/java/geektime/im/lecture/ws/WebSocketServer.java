package geektime.im.lecture.ws;

import geektime.im.lecture.utils.EnhancedThreadFactory;
import geektime.im.lecture.ws.handler.CloseIdleChannelHandler;
import geektime.im.lecture.ws.handler.WebsocketRouterHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class WebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    @Autowired
    private ServerConfig serverConfig;

    private ServerBootstrap bootstrap;
    private ChannelFuture channelFuture;


    private EventExecutorGroup eventExecutorGroup;

    @Autowired
    private WebsocketRouterHandler websocketRouterHandler;

    @Autowired
    private CloseIdleChannelHandler closeIdleChannelHandler;

    @PostConstruct
    public void start() throws InterruptedException {
        if (serverConfig.port == 0) {
            log.info("WebSocket Server not config.");
            return;
        }

        log.info("WebSocket Server is starting");
        eventExecutorGroup =
                new DefaultEventExecutorGroup(serverConfig.userThreads, new EnhancedThreadFactory("WebSocketBizThreadPool"));

        ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                //先添加websocket相关的编解码器和协议处理器
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpObjectAggregator(65536));
                pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                pipeline.addLast(new WebSocketServerProtocolHandler("/", null, true));
                //再添加服务端业务消息的总处理器
                pipeline.addLast(websocketRouterHandler);
                //服务端添加一个idle处理器，如果一段时间socket中没有消息传输，服务端会强制断开
                pipeline.addLast(new IdleStateHandler(0, 0, serverConfig.getAllIdleSecond()));
                pipeline.addLast(closeIdleChannelHandler);

            }

        };

        bootstrap = newServerBootstrap();
        bootstrap.childHandler(initializer);

        channelFuture = bootstrap.bind(serverConfig.port).sync();

        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        log.info("WebSocket Server start succ on:" + serverConfig.port);

        new Thread() {

            @Override
            public void run() {
                try {
                    channelFuture.channel().closeFuture().sync();
                } catch (Exception e) {
                    log.error("WebSocket Server start failed!", e);
                }
            }

        }.start();
    }


    class ShutdownThread extends Thread {
        @Override
        public void run() {
            close();
        }
    }

    public void close() {
        if (bootstrap == null) {
            log.info("WebSocket server is not running!");
            return;
        }

        log.info("WebSocket server is stopping");
        if (channelFuture != null) {
            channelFuture.channel().close().awaitUninterruptibly(10, TimeUnit.SECONDS);
            channelFuture = null;
        }
        if (bootstrap != null && bootstrap.config().group() != null) {
            bootstrap.config().group().shutdownGracefully();
        }
        if (bootstrap != null && bootstrap.config().childGroup() != null) {
            bootstrap.config().childGroup().shutdownGracefully();
        }
        bootstrap = null;

        log.info("WebSocket server stopped");
    }

    /**
     * 如果系统本身支持epoll同时用户自己的配置也允许epoll，会优先使用EpollEventLoopGroup
     * @return
     */
    public ServerBootstrap newServerBootstrap() {
        if (Epoll.isAvailable() && serverConfig.useEpoll) {
            EventLoopGroup bossGroup =
                    new EpollEventLoopGroup(serverConfig.bossThreads, new DefaultThreadFactory("WebSocketBossGroup", true));
            EventLoopGroup workerGroup =
                    new EpollEventLoopGroup(serverConfig.workerThreads, new DefaultThreadFactory("WebSocketWorkerGroup", true));
            return new ServerBootstrap().group(bossGroup, workerGroup).channel(EpollServerSocketChannel.class);
        }

        return newNioServerBootstrap(serverConfig.bossThreads, serverConfig.workerThreads);
    }

    private ServerBootstrap newNioServerBootstrap(int bossThreads, int workerThreads) {
        EventLoopGroup bossGroup;
        EventLoopGroup workerGroup;
        if (bossThreads >= 0 && workerThreads >= 0) {
            bossGroup = new NioEventLoopGroup(bossThreads);
            workerGroup = new NioEventLoopGroup(workerThreads);
        } else {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
        }

        return new ServerBootstrap().group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
    }
}
