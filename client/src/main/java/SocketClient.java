import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @Projectname: rpc
 * @Filename: SocketClient
 * @Author: SpringForest
 * @Data:2023/2/2 20:53
 * @Description:
 */
public class SocketClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);


    public Object send(String message, String host, int port){
        //创建Socket对象并绑定服务器地址和端口号
        try {
            logger.info("客户端运行成功，正在发送信息");
            Socket socket = new Socket(host,port);

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            //通过输出流向服务器发送信息
            outputStream.writeObject(message);
            outputStream.flush();
            logger.info("信息发送完毕");
            //通过输入流获取服务器的相应信息
            return inputStream.readObject();
        } catch (UnknownHostException e) {
            logger.error("服务器地址错误");
        } catch (IOException | ClassNotFoundException e) {
            logger.error("occur exception", e);
        }
        return null;
    }

    public static void main(String[] args) {
        SocketClient socketClient = new SocketClient();
        String message = (String) socketClient.send("测试信息","127.0.0.1",123);
        System.out.println("服务器返回信息：" + message);
    }
}


