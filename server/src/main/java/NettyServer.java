import codec.NettyKryoDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import serialize.KryoSerializer;
import utils.RpcRequest;
import utils.RpcResponse;

/**
 * @Projectname: sfrpc
 * @Filename: NettyServer
 * @Author: SpringForest
 * @Data:2023/2/3 15:57
 * @Description:
 */
@Slf4j
public class NettyServer {
    private int port;

    private NettyServer(int port) {
        this.port = port;
    }

    private void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup wokerGroup = new NioEventLoopGroup();
        KryoSerializer kryoSerializer = new KryoSerializer();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, wokerGroup)
                    .channel(NioServerSocketChannel.class)
                    //TCP默认开启Nagle算法，该算法的作用是尽可能的发送大数据块，减少网络传输
                    //TCP_NODELAY的作用就是控制是否启用Nagle算法
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    //是否开启TCP底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //表示系统用于临时存放已经完成三次握手的请求的队列的最大长度
                    //如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            channel.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcRequest.class));
                            channel.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                            channel.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            //绑定端口并等待绑定成功
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            //等待监听端口关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务器启动失败：", e);
        } finally {
            bossGroup.shutdownGracefully();
            wokerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyServer(889).run();
    }
}
