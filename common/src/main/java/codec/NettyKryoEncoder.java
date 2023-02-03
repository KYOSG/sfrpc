package codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import serialize.Serializer;

/**
 * @Projectname: sfrpc
 * @Filename: NettyKryoEncoder
 * @Author: SpringForest
 * @Data:2023/2/3 14:32
 * @Description: 自定义的编码器，将对象转换为字节流，使用ByteBuf当作容器
 */
@AllArgsConstructor
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {
    private final Serializer serializer;
    private final Class<?> genericClass;

    /*
     * 将对象转换为字节码然后写入到ByteBuf中
     * */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        //检查是否有此Class对应的对象实例
        if (genericClass.isInstance(o)) {
            //将对象序列化为byte
            byte[] body = serializer.serialize(o);
            //读取消息的长度
            int length = body.length;
            //写入消息对应的字节数组长度
            byteBuf.writeInt(length);
            //将字节数组写入ByteBuf中
            byteBuf.writeBytes(body);
        }
    }


}
