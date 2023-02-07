import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import utils.RpcRequest;
import utils.RpcResponse;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Projectname: sfrpc
 * @Filename: NettyServerHandler
 * @Author: SpringForest
 * @Data:2023/2/3 15:57
 * @Description: 接受客户端发送的消息并返回结果
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    //AtomicInteger是基于CAS(Compare and swap)原理实现非阻塞的原子操作，用于线程安全地加减整型数据
    //定义初始值为1
    private static final AtomicInteger atomicInteger = new AtomicInteger(1);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            RpcRequest request = (RpcRequest) msg;
            log.info("服务器接收到信息：[{}],时间：[{}]", request.toString(), atomicInteger.getAndIncrement());
            RpcResponse response = RpcResponse.builder().message("服务器已收到信息").build();
            ChannelFuture channelFuture = ctx.writeAndFlush(response);
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务器异常", cause);
        ctx.close();
    }
}
