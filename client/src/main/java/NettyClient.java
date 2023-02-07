import codec.NettyKryoDecoder;
import codec.NettyKryoEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serialize.KryoSerializer;
import utils.RpcRequest;
import utils.RpcResponse;

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
        KryoSerializer kryoSerializer = new KryoSerializer();

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
                        //将数据序列化为字节
                        ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                        //将字节反序列化为对象
                        ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcResponse.class));
                        ch.pipeline().addLast(new NettyClientHandler());

                    }
                });
    }

    /*
     * 发送消息到服务端
     * @param 消息体
     * @return 服务端返回数据
     * */

    public RpcResponse sendMessage(RpcRequest request) {
        try {
            ChannelFuture channelFuture = b.connect(host, port).sync();
            logger.info("客户端连接成功{}", host + ":" + port);
            Channel futureChannel = channelFuture.channel();
            logger.info("发送消息");
            if (futureChannel != null) {
                futureChannel.writeAndFlush(request).addListener(future -> {
                    if (future.isSuccess()) {
                        logger.info("客户端发送消息：[{}]", request.toString());
                    } else {
                        logger.error("客户端消息发送失败");
                    }
                });
                //阻塞等待，知道Channel关闭
                futureChannel.closeFuture().sync();
                //将服务端返回的数据取出
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                return futureChannel.attr(key).get();
            }
        } catch (Exception e) {
            logger.error("客户端启动失败");
        }
        return null;
    }

    public static void main(String[] args) throws InterruptedException {
        RpcRequest request = RpcRequest.builder()
                .interfaceName("测试接口")
                .methodName("测试方法").build();

        NettyClient nettyClient = new NettyClient("127.0.0.1", 889);

        for (int i = 0; i < 4; i++) {
            RpcResponse response = nettyClient.sendMessage(request);
            System.out.println(response.toString());
            Thread.sleep(1000);
        }
        request.setMethodName("最后一次");
        RpcResponse response = nettyClient.sendMessage(request);
        System.out.println(response.toString());
    }
}
