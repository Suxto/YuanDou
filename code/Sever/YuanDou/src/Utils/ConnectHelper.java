package Utils;

import GameRun.Main;
import Scenes.CheckOutPane;
import Scenes.OTablePane;
import Scenes.TablePickerPane;
import Scenes.WaitPane;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectHelper {
    final static String serverHost = "47.120.6.121";  // 服务器主机名或IP地址
    final static int serverPort = 8081;  // 服务器端口
    private InputStream inputStream;
    private OutputStream outputStream;
    private Socket socket;
    private Thread receiveThread;
//    boolean onGoing = true;

    public ConnectHelper() {
        try {
            // 创建Socket对象，连接服务器
            socket = new Socket(serverHost, serverPort);
            // 获取输入输出流
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            // 启动接收服务器消息的线程
            receiveThread = new Thread(() -> {
                try {
                    while (!Thread.interrupted() && !socket.isClosed()) {
                        byte[] buffer = new byte[1024];
                        int length = inputStream.read(buffer);
                        if (length > 0) {
                            String message = new String(buffer, 0, length);
                            String[] messages = message.split("\\|");
                            for (String msg : messages) {
                                if (msg.length() < 3) continue;
                                if (Main.currentPane instanceof Scenes.TablePickerPane) {
                                    ((TablePickerPane) (Main.currentPane)).messageHandler(msg);
                                } else if (Main.currentPane instanceof Scenes.WaitPane) {
                                    ((WaitPane) Main.currentPane).messageHandler(msg);
                                } else if (Main.currentPane instanceof Scenes.CheckOutPane) {
                                    //TODO MsgHandler
                                    ((CheckOutPane) (Main.currentPane)).messageHandler(msg);
                                } else {
                                    ((OTablePane) (Main.currentPane)).messageHandler(msg);
                                }
                            }

                            //System.out.println("Received message from server: " + message);
                        }
                    }

                } catch (IOException ignore) {
//                    e.printStackTrace();
                }
            });
            receiveThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String message) {
        try {
            outputStream.write(message.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        receiveThread.interrupt();
        try {
            socket.close();
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
