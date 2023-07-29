package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
   static final ArrayList<Table> tables = new ArrayList<>();
//    private static final ArrayList<ClientHandler> hallClients = new ArrayList<>();

    public static void main(String[] args) {
        int serverPort = 8081;  // 服务器监听端口

        try {
            // 创建ServerSocket对象，绑定端口
            ServerSocket serverSocket = new ServerSocket(serverPort);
            System.out.println("Server is running and listening on port " + serverPort);

            while (true) {
                // 等待客户端连接
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // 创建新的客户端处理器
                ClientHandler clientHandler = new ClientHandler(clientSocket);

                // 启动客户端处理器线程
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

