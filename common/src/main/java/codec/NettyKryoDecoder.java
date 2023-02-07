package codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serialize.Serializer;

import java.util.List;

/**
 * @Projectname: sfrpc
 * @Filename: NetyKryoDecoder
 * @Author: SpringForest
 * @Data:2023/2/3 15:12
 * @Description: 自定义的解码器
 */
@AllArgsConstructor
@Slf4j
public class NettyKryoDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(NettyKryoEncoder.class);
    private Serializer serializer;
    private Class<?> genericClass;

    /*
     * Netty传输的消息长度，对象序列化后对应的字节数组的大小，存储在ByteBuf头部
     * */
    private static final int BODY_LENGTH = 4;

    /*
     * 解码ByteBuf对象
     *
     * @param ctx 解码器关联的ChanelHandlerContext对象
     * @param in 入站的数据，即ByteBuf对象
     * @param out 解码后的数据对象
     * */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        logger.error("调用解码器");
        //byteBuf中写入消息的消息长度信息本身（int类型数据）已经占用了4个字节，所以byteBuf长度要大于4才说明有数据
        if (in.readableBytes() >= BODY_LENGTH) {
            //标记当前readIndex的位置
            in.markReaderIndex();
            //读取消息长度
            int length = in.readInt();
            //判断不合理的情况
            if (length < 0 || in.readableBytes() < 0) {
                logger.error("消息为空");
                return;
            }
            //如果可读字节小于消息长度说明消息不完整，重置readIndex
            if (in.readableBytes() < length) {
                in.resetReaderIndex();
                logger.error("消息不不完整");
                return;
            }
            //进行序列化
            byte[] body = new byte[length];
            in.readBytes(body);
            //将bytes数组转换为需要的对象
            Object obj = serializer.deserialize(body, genericClass);
            out.add(obj);
            logger.info("解码成功");
        }
    }
}
