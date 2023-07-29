package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringJoiner;

class ClientHandler implements Runnable {
    Table table = null;
    String name = null;
    String image = null;
    boolean ready = false;
    long oStone = 0;
    private final Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] buffer = new byte[1024];
                int length = inputStream.read(buffer);
                if (length > 0) {
                    String orgMessage = new String(buffer, 0, length);
                    String[] messages = orgMessage.split("\\|");
                    for (String message : messages) {
                        if (message.length() < 3) continue;
                        System.out.println("Received message: " + message);
                        if ("Client:Create Table".equals(message)) {
                            Table table = new Table();
                            Server.tables.add(table);
                        } else if ("Client:Get Tables".equals(message)) {
                            StringBuilder stringBuilder = new StringBuilder("System:Tables{");
                            for (Table t : Server.tables) {
                                stringBuilder.append(t.toString());
                            }
                            stringBuilder.append('}');
                            sendMessage(stringBuilder.toString());
                        } else if (message.startsWith("Client:Join Table")) {
                            int idx = Integer.parseInt(message.substring("Client:Join Table ".length()));
                            Server.tables.get(idx).addClient(this);
                        } else if (message.startsWith("Client:Exit")) {
                            if (table == null) return;
                            table.removeClient(this);
                        } else if (message.startsWith("Client:Info:")) {
                            message = message.substring("Client:Info:".length());
                            String[] strings = message.split(",");
                            name = strings[0];
                            oStone = Long.parseLong(strings[1]);
                            image = strings[2];
                            ready = false;
                        } else if (table == null) continue;
                        else {//同步的消息
                            table.broadcastMessage(message);
                            if (message.contains("Ready")) {
                                ready = true;
                                table.ready();
                            } else if (message.contains("Unready")) {
                                ready = false;
                                table.unReady();
                            } else if (message.contains("Lay") || message.contains("Skip")) {
                                table.nextRound();
                            } else if (message.contains("Done")) {
                                table.resetReady();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            outputStream.write((message + '|').getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(",", "{", "}").add(name).add(String.valueOf(oStone)).add(String.valueOf(ready)).add(image).toString();
    }
}
