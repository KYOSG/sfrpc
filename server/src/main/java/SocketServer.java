

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Projectname: rpc
 * @Filename: SocketServer
 * @Author: SpringForest
 * @Data:2023/2/2 20:53
 * @Description:
 */
public class SocketServer {
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);
    public void start(int port){
        //创建SocketServer对象并绑定接口
        try (ServerSocket server = new ServerSocket(port)){
            Socket socket;
            logger.info("服务端运行成功，正在监听");
            //使用accept方法监听客户端请求
            while((socket = server.accept())!=null){
                logger.info("链接建立");
                try {
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    //3. 通过输入流读取客户端发送的信息
                    //Message message = (String) inputStream.readObject();
                    String message = (String) inputStream.readObject();
                    logger.info("接收到客户端信息：" + message);
                    //设置新消息内容
                    //message.setMessage("新消息");
                    //通过输出流向客户端发送信息
                    outputStream.writeObject("服务器接收到信息");
                    outputStream.flush();
                } catch (ClassNotFoundException e) {
                    logger.error("occur Exception: ",e);
                }
            }

        }catch (IOException e){
            logger.error("Occur IOException: ",e);
        }
    }

    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer();
        socketServer.start(123);
    }
}
