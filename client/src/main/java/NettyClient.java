import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serialize.KryoSerializer;

/**
 * @Projectname: rpc
 * @Filename: NettyClient
 * @Author: SpringForest
 * @Data:2023/2/3 10:20
 * @Description:
 */

public class NettyClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final String host;
    private final int port;
    private static final Bootstrap b;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //初始化相关资源
    static {
        /*
         * 在Netty中，Channel为网络操作抽象类，EventLoop负责处理注册到其上的Channel处理I/O操作，两者配合参与I/O操作；
         * EventLoopGroup是一个EventLoop的分组，它可以获取到一个或者多个EventLoop对象，因此它提供了迭代出EventLoop对象的方法。
         * */
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        //Netty使用的是kryo序列化协议
        KryoSerializer kryoSerializer new KryoSerializer();

        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 设置连接超时时间，超过该时间还没有连接上视为连接失败
                // 如果5秒内没有发送数据给服务端就发送一次心跳请求
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        /*
                         * 自定义序列化编解码器
                         * */
                        ch.pipeline().addLast(new Nett)
                    }
                })
    }
}
