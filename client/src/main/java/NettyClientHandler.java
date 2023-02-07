import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.RpcResponse;

/**
 * @Projectname: sfrpc
 * @Filename: NettyClientHandler
 * @Author: SpringForest
 * @Data:2023/2/3 15:49
 * @Description:
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            RpcResponse response = (RpcResponse) msg;
            logger.info("客户端接收到信息:" + response.toString());
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            //将服务端的返回结果保存到AttributeMap上，AttributeMap可以看作是一个Channel的共享数据
            //AttributeMap的key是AttributeKey，value是Attribute
            ctx.channel().attr(key).set(new RpcResponse());
            ctx.channel().close();
        } finally {
            //释放对象防止内存泄漏
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("客户端异常：", cause);
        ctx.close();
    }
}
