package serialize;

/**
 * @Projectname: sfrpc
 * @Filename: Serialize
 * @Author: SpringForest
 * @Data:2023/2/3 12:07
 * @Description:
 */

public interface Serializer {
    /**
     * 序列化
     *
     * @param obj 要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes 序列化后的字节数组
     * @param clazz 目标类
     * @param <T>   泛型
     * @return 反序列化的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
