package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

public class Table {
    //    final static int DELAY = 100;
    String[] cards = {"H14", "H15", "H3", "H4", "H5", "H6", "H7", "H8", "H9", "H10", "H11", "H12", "H13", "F14", "F15", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12", "F13", "S14", "S15", "S3", "S4", "S5", "S6", "S7", "S8", "S9", "S10", "S11", "S12", "S13", "P14", "P15", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10", "P11", "P12", "P13", "J16", "J17"};
    private int readyPlayerNumber = 0;
    ArrayList<ClientHandler> clients = new ArrayList<>();
    boolean started = false;
    int currentUser;

    public void broadcastMessage(String message) {
//        System.out.println(message);
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public String usersInfo() {
        StringBuilder stringBuilder = new StringBuilder("{");
        for (ClientHandler client : clients) {
            stringBuilder.append(client.toString());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public void addClient(ClientHandler client) {
        if (clients.size() > 2) {
            client.sendMessage("System:No Space!");
        } else {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            char mark = (char) ('A' + clients.size());
            clients.add(client);
            client.table = this;
            client.sendMessage("System:Player:" + mark);
            try {
                Thread.sleep(150);
            } catch (Exception ignore) {
            }
            syncUserInfo();
            sendIdentities();
        }
    }

    void removeClient(ClientHandler client) {
        clients.remove(client);
        client.table = null;
        syncUserInfo();
//        if (clients.isEmpty()) Server.tables.remove(this);
    }

    void syncUserInfo() {
        broadcastMessage("System:Come:" + usersInfo());
    }

    private void sendIdentities() {
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).sendMessage("System:Player:" + (char) ('A' + i));
        }
    }

    public String toString() {
        return "{" + clients.size() + "," + started + "}";
    }

    public void ready() {
        readyPlayerNumber++;
        if (readyPlayerNumber == 3) {
            broadcastMessage("System:Begin");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sendCards();
        }
    }

    private void washCards() {
        for (int i = 0; i < 54; i++) {
            int idx = (int) (Math.random() * 54);
            String tmp = cards[i];
            cards[i] = cards[idx];
            cards[idx] = tmp;
        }
    }

//    public void delay() {
//        try {
//            Thread.sleep(DELAY);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void sendCards() {
        started = true;
        resetReady();
        washCards();
        int QQW = (int) (3 * Math.random());
        currentUser = QQW;
        for (int i = 0; i < 3; i++) {
            StringJoiner stringJoiner = new StringJoiner(",", "System:Give:" + (char) ('A' + i) + ':', "");
            ArrayList<String> cardList = new ArrayList<>(Arrays.asList(cards).subList(i * 17, 17 + i * 17));
            if (i == QQW) {
                cardList.add(cards[51]);
                cardList.add(cards[52]);
                cardList.add(cards[53]);
            }
            cardList.sort((s1, s2) -> {
                if (s1.length() != s2.length()) return Integer.compare(s1.length(), s2.length());
                s1 = s1.substring(1);
                s2 = s2.substring(1);
                return s1.compareTo(s2);
            });
            cardList.forEach(stringJoiner::add);
            broadcastMessage(stringJoiner.toString());
        }
        broadcastMessage("System:QQW:" + (char) (QQW + 'A'));
        broadcastMessage("System:QQWCard:" + cards[51] + ',' + cards[52] + ',' + cards[53]);
        go();
    }


    public void unReady() {
        readyPlayerNumber--;
    }

    public static void main(String[] args) {
        Table table = new Table();
        table.sendCards();
    }

    public void nextRound() {
        currentUser = (currentUser + 1) % 3;
        go();
    }

    private void go() {
        broadcastMessage("System:" + "Round:" + (char) (currentUser + 'A'));
    }

    public void resetReady() {
        started = false;
        readyPlayerNumber = 0;
    }
}
