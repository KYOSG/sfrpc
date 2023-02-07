package serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import utils.RpcRequest;
import utils.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Projectname: sfrpc
 * @Filename: KryoSerializer
 * @Author: SpringForest
 * @Data:2023/2/3 11:02
 * @Description:
 */
@Slf4j
public class KryoSerializer implements Serializer {
    /*
     * 因为Kryo不是线程安全的，所以使用ThreadLocal存储Kryo对象
     * 使每个线程都有自己的Kryo,Input和Output对象
     * */
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        /*
         * 是否关闭注册行为，默认为打开，关闭后会有序列化问题
         * 这会赋予该 Class 一个从 0 开始的编号，但 Kryo 使用注册行为最大的问题在于，
         * 其不保证同一个 Class 每一次注册的号码相同，这与注册的顺序有关，也就意味着在不同的机器、
         * 同一个机器重启前后都有可能拥有不同的编号，这会导致序列化产生问题，所以在分布式项目中，一般关闭注册行为。
         * */
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);//是否关闭循环引用，关闭可以提高性能，但是一般设置为false
        return kryo;
    });


    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            //Object -> byte:将对象序列化为byte数组
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            log.error("序列化失败");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            //byte -> Object从byte数组中反序列化出对象
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(o);
        } catch (Exception e) {
            log.error("反序列化失败");
            e.printStackTrace();
        }
        return null;
    }
}
