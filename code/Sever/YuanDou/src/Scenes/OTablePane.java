package Scenes;

import GameRun.Main;
import Beans.*;
import Utils.Utils;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.image.*;

import javax.swing.Timer;
import java.io.IOException;
import java.util.*;

public class OTablePane extends Pane {
    private long startTime;
    private boolean winner = false;
    final int iA = 0, iB = 1, iC = 2;
    //界面容器
    public Pane Slot = new Pane(), Choose = new Pane(), Game_inFor = new Pane(), Chat = new Pane(), Panel = new Pane(), BottomCard = new Pane();
    public Pane User = new Pane(), Display = new Pane(), alarm = new Pane();
    //组件
    private final ImageView alarmGround = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/alarm.png"))));
    private final Label limitTime = new Label();
    public Button Go = new Button("Go"), Skip = new Button("Skip");
    public Button Quit = new Button("Quit"), Ai = new Button("Ai"), Vol = new Button("Vol"), Set = new Button("Set");
    public ComboBox<Object> Chat_trash = new ComboBox<>();
    public TextArea Chat_record = new TextArea();
    //信息
    private int QQWis;
    private int allChooseTime = 0;
    private int ChooseNum = 0;
    private int curr;//出牌轮转次数，当前限制时间
    private int cotNull = 0;//跳过次数
    private int Ready = 0;//准备人数
    private static int UID;//机器ID
    public ImageView BackGround = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/OTablePane.png"))));
    public ArrayList<CardPane> cardPanes = new ArrayList<>();
    public Timer timer = null;
    //
    ArrayList<CardPane> cache = new ArrayList<>(); //缓存上家牌
    public Label cardNum1 = new Label(), cardNum2 = new Label(), cardNum3 = new Label(), Name1 = new Label(), Name2 = new Label(), Name3 = new Label();
    Button QQW = new Button(), Round = new Button();
    public ImageView cardNumBackGround1 = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/cardNumBackGround.png")))), cardNumBackGround2 = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/cardNumBackGround.png")))), cardNumBackGround3 = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/cardNumBackGround.png"))));
    //
    public ImageView p1 = new ImageView(), p2 = new ImageView(), p3 = new ImageView();
    ArrayList<User> players;

    public OTablePane(ArrayList<User> players, int uid) throws IOException {
        this.players = players;
        UID = uid;
        Platform.runLater(() -> {
            Init_Node();
            Init_Pane();
            Action();
        });
    }

//按钮事件
    public void Action() {
        //按键事件
        Quit.setOnAction(e -> {
            Utils.log("Client:Exit");
            Main.setScene(new TablePickerPane());
        });
        Go.setOnAction(e -> {
            int i = 0;
            int len = Slot.getChildren().size();
            StringBuilder card = new StringBuilder();
            StringBuilder type = new StringBuilder();
            for (int j = 0; j < cardPanes.size(); j++) {
                if (Slot.getChildren().get(j).getLayoutY() < 0) {
                    card.append(cardPanes.get(j).getSize()).append(" ");
                    type.append(cardPanes.get(j).getType()).append(cardPanes.get(j).getSize()).append(" ");
                }
            }

            if (legal(card.toString())) {
                do {
                    if (Slot.getChildren().get(i).getLayoutY() < 0) {
                        cardPanes.remove(i);
                        Slot.getChildren().remove(i);
                        i = 0;
                        len--;
                    } else {
                        i++;
                    }
                    if (i == len) break;
                } while (Slot.getChildren().size() > 0);
                Refresh(false);
                if (UID == 0) {//A/B/C:Lay
                    Utils.log("A:" + "Lay:" + type);
                    if (Slot.getChildren().size() == 0) {
                        Utils.log("A:" + "Done");
                    }
                } else if (UID == 1) {
                    Utils.log("B:" + "Lay:" + type);
                    if (Slot.getChildren().size() == 0) {
                        Utils.log("B:" + "Done");
                    }
                } else {
                    Utils.log("C:" + "Lay:" + type);
                    if (Slot.getChildren().size() == 0) {
                        Utils.log("C:" + "Done");
                    }
                }
                Platform.runLater(() -> {
                    ChooseNum++;
                    timer.stop();
                    setAllChooseTime();
                    Choose.getChildren().clear();
                    alarm.getChildren().clear();
                });
            }
        });
        Skip.setOnAction(e -> {
            if (!cache.isEmpty()) {
                if (UID == 0) {//A/B/C:Skip
                    Utils.log("A:" + "Skip");
                } else if (UID == 1) {
                    Utils.log("B:" + "Skip");
                } else {
                    Utils.log("C:" + "Skip");
                }
            }
            Platform.runLater(() -> {
                ChooseNum++;
                timer.stop();
                setAllChooseTime();
                Choose.getChildren().clear();
                alarm.getChildren().clear();
            });
        });
        Chat_trash.setOnAction(e -> {
            String msg = "Message:Chat:";
            if (Chat_trash.getSelectionModel().getSelectedIndex() != -1) {//选了
                int selectedIndex = Chat_trash.getSelectionModel().getSelectedIndex();
                Utils.log(msg + players.get(UID).getName() + ":" + Chat_trash.getItems().get(selectedIndex));
            } else if (Chat_trash.getValue() != null) {//其他文本
                Utils.log(msg + players.get(UID).getName() + ":" + Chat_trash.getValue());
            }
            Chat_record.setOpacity(1);
            Platform.runLater(() -> {//清除文本
                Chat_trash.getSelectionModel().clearSelection();
                Chat_trash.getEditor().clear();
                Chat_trash.setValue(null);
                Chat_trash.setPromptText("Press Enter to Send");
            });
        });
        Chat_record.setOnMouseClicked(e -> {
            Chat_record.setOpacity(1);
            p3.setOpacity(0.5);
        });
        BackGround.setOnMouseClicked(e -> {
            Chat_record.setOpacity(0.5);
            p3.setOpacity(1);
        });
    }
//解释服务器指令
    public void messageHandler(String msg) throws IOException {
        String[] message = msg.split(":");
        if (message[0].equals("System")) {
            switch (message[1]) {
                case "Give": {
                    switch (message[2]) {
                        case "A": {
                            players.get(iA).setMyCards(message[3]);
                            Ready++;
                            break;
                        }
                        case "B": {
                            players.get(iB).setMyCards(message[3]);
                            Ready++;
                            break;
                        }
                        case "C": {
                            players.get(iC).setMyCards(message[3]);
                            Ready++;
                            break;
                        }
                    }
                    if (Ready == 3) {
                        Re_Card(players.get(UID));
                        Action();
                        Refresh(false);
                        Platform.runLater(() -> {
                            int temp = UID;
                            cardNum1.setText(players.get(UID).getNum());
                            p1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/UserBattle/" + players.get(UID).getImage()))));
                            Name1.setText(players.get(UID).getName());
                            cardNum3.setText(players.get((temp + 1) % 3).getNum());
                            p3.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/UserBattle/" + players.get((temp + 1) % 3).getImage()))));
                            Name3.setText(players.get((temp + 1) % 3).getName());
                            cardNum2.setText(players.get((temp + 2) % 3).getNum());
                            p2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/UserBattle/" + players.get((temp + 2) % 3).getImage()))));
                            Name2.setText(players.get((temp + 2) % 3).getName());
                        });

                    }
                    break;
                }
                case "QQW": {
                    switch (message[2]) {
                        case "A": {
                            players.get(iA).setD(true);
                            QQWis = iA;
                            break;
                        }
                        case "B": {
                            players.get(iB).setD(true);
                            QQWis = iB;
                            break;
                        }
                        case "C": {
                            players.get(iC).setD(true);
                            QQWis = iC;
                            break;
                        }
                    }
                    Platform.runLater(() -> {
                        QQW.setText("QQW:" + players.get(QQWis).getName());
                    });
                    startTime = System.currentTimeMillis();
                    break;
                }
                case "QQWCard": {
                    Platform.runLater(() -> {
                        LayoutQQWCard(message[2]);
                    });
                    break;
                }
                case "Round": {
                    switch (message[2]) {
                        case "A": {
                            Platform.runLater(() -> {
                                if (UID == 0) {
                                    Choose.getChildren().addAll(Go, Skip);
                                    Timer();
                                }
                                Round.setText("Round:" + players.get(0).getName());
                            });
                            break;
                        }
                        case "B": {
                            Platform.runLater(() -> {
                                if (UID == 1) {
                                    Choose.getChildren().addAll(Go, Skip);
                                    Timer();
                                }
                                Round.setText("Round:" + players.get(1).getName());
                            });
                            break;
                        }
                        case "C": {
                            Platform.runLater(() -> {
                                if (UID == 2) {
                                    Choose.getChildren().addAll(Go, Skip);
                                    Timer();
                                }
                                Round.setText("Round:" + players.get(2).getName());
                            });
                            break;
                        }
                    }
                    break;
                }
            }
        } else if (message[0].equals("Message")) {
            switch (message[1]) {
                case "Chat": {
                    Platform.runLater(() -> {
                        Chat_record.appendText(message[2] + ":" + message[3] + "\n");
                    });
                    break;
                }
                case "Pre": {
                    break;
                }
            }
        } else {
            switch (message[0]) {
                case "A": {
                    switch (message[1]) {
                        case "Lay": {
                            Platform.runLater(() -> {
                                DisplayCard(message[2]);
                            });
                            Refresh(true);
                            Platform.runLater(() -> {
                                cotNull = 0;
                                FreshCardNum(0, message[2]);
                            });
                            break;
                        }
                        case "Skip": {
                            cotNull++;          //空过判断数++
                            if (cotNull == 2) {     //当有连续两个空过时,清空上家出的牌preCards 并将空过判断数置0
                                Platform.runLater(() -> {
                                    Display.getChildren().clear();
                                    cache.clear();
                                    cotNull = 0;
                                });
                            }
                            break;
                        }
                        case "Done": {
                            if (UID == 0 || (QQWis != 0 && QQWis != UID)) winner = true;
                            Main.setScene(new CheckOutPane(players.get(UID), players.get(UID).getoStone(), TotalTime(), winner, allChooseTime / ChooseNum));
                        }
                    }
                    break;
                }
                case "B": {
                    switch (message[1]) {
                        case "Lay": {
                            Platform.runLater(() -> {
                                DisplayCard(message[2]);
                            });
                            Refresh(true);
                            Platform.runLater(() -> {
                                cotNull = 0;
                                FreshCardNum(1, message[2]);
                            });
                            break;
                        }
                        case "Skip": {
                            cotNull++;          //空过判断数++
                            if (cotNull == 2) {     //当有连续两个空过时,清空上家出的牌preCards 并将空过判断数置0
                                Platform.runLater(() -> {
                                    Display.getChildren().clear();
                                    cache.clear();
                                    cotNull = 0;
                                });
                            }
                            break;
                        }
                        case "Done": {
                            if (UID == 1 || (QQWis != 1 && QQWis != UID)) winner = true;
                            Main.setScene(new CheckOutPane(players.get(UID), players.get(UID).getoStone(), TotalTime(), winner, allChooseTime / ChooseNum));
                        }
                    }
                    break;
                }
                case "C": {
                    switch (message[1]) {
                        case "Lay": {
                            Platform.runLater(() -> {
                                DisplayCard(message[2]);
                            });
                            Refresh(true);
                            Platform.runLater(() -> {
                                cotNull = 0;
                                FreshCardNum(2, message[2]);
                            });
                            break;
                        }
                        case "Skip": {
                            cotNull++;          //空过判断数++
                            if (cotNull == 2) {     //当有连续两个空过时,清空上家出的牌preCards 并将空过判断数置0
                                Platform.runLater(() -> {
                                    Display.getChildren().clear();
                                    cache.clear();
                                    cotNull = 0;
                                });
                            }
                            break;
                        }
                        case "Done": {
                            if (UID == 2 || (QQWis != 2 && QQWis != UID)) winner = true;
                            Main.setScene(new CheckOutPane(players.get(UID), players.get(UID).getoStone(), TotalTime(), winner, allChooseTime / ChooseNum));
                        }
                    }
                    break;
                }
            }
        }
    }
//记录出牌总时间
    public void setAllChooseTime() {
        int temp = Integer.parseInt(limitTime.getText());
        allChooseTime += (30-temp);
    }
//展示所出牌
    public void DisplayCard(String type) {
        String[] newType = type.split(" ");
        cache.clear();
        Display.getChildren().clear();
        for (String s : newType) {
            CardPane c = new CardPane(s);
            c.prefWidthProperty().bind(Display.heightProperty().multiply(63.0 / 88));
            c.prefHeightProperty().bind(Display.heightProperty());
            cache.add(c);
        }
        for (CardPane cardPane : cache) {
            Display.getChildren().add(cardPane);
        }
        Refresh(true);
    }
//得到上家的牌
    public String getPreType() {
        StringBuilder preType = new StringBuilder();
        for (CardPane cardPane : cache) preType.append(cardPane.getSize()).append(" ");
        return preType.toString();
    }
//判定是否可以出牌
    public boolean legal(String go) {
        String preType = typeJudge(getPreType());//上家底牌类型
        String type = typeJudge(go);//出牌的底牌类型
        ArrayList<Integer> nowCards = new ArrayList<>();
        String[] nCard = go.split(" ");
        for (String s : nCard) {
            nowCards.add(Integer.parseInt(s));
        }

        if (type.equals("ERROR")) {       //验证出的牌是否符合排列规则
            return false;
        }
        if (!cache.isEmpty()) {   //上家若出过牌
            if ("王炸".equals(preType)) {//王炸最大
                return false;
            }
            if (!type.equals("炸弹") && !type.equals("王炸")) {
                if (!type.equals(preType) || nowCards.size() != cache.size()) {     //继续验证出的牌是否与上家出的牌型一样
                    return false;
                }
            }
            //再根据牌型判断大小(通常都可以用第一个字符来比较)
            if (!rankJudge(type, nowCards)) {//是否比上家大
                return false;
            }
        }
        return true;
    }
//判定出牌合不合法
    private boolean rankJudge(String type, ArrayList<Integer> nowCards) {
        // 1.单                      判断单张的权重大小 再根据preCards里的权重通过牌库hash找到对应的牌面 如果牌面相同 不允许出牌
        if (type.equals("单")) {
            return nowCards.get(0) > Integer.parseInt(cache.get(0).getSize());
        }
        // 2.对                      判断nowCards中第一位的权重大小即可 如果牌面相同 不允许出牌
        if (type.equals("对")) {
            return nowCards.get(0) > Integer.parseInt(cache.get(0).getSize());
        }
        // 3.三带                判断nowCards中第一位的权重大小即可 如果牌面相同 不允许出牌
        if (type.equals("三带")) {
            return nowCards.get(0) > Integer.parseInt(cache.get(0).getSize());
        }
        // 4.三带一                三张相同牌中的任意一张牌面按照权重比较大小
        if (type.equals("三带一")) {
            return nowCards.get(1) > Integer.parseInt(cache.get(1).getSize());
        }
        // 三带二                三张相同牌中的任意一张牌面按照权重比较大小
        if (type.equals("三带二")) {
            return nowCards.get(2) > Integer.parseInt(cache.get(2).getSize());
        }
        // 5.飞机                 任意一个机身的牌面权重 > 先出的任意一个机身的牌面权重
        if (type.equals("飞机无翅膀")) {
            return nowCards.get(0) > Integer.parseInt(cache.get(0).getSize());
        }
        if (type.equals("飞机单翅膀")) {
            return nowCards.get(1) > Integer.parseInt(cache.get(1).getSize());
        }
        if (type.equals("飞机对翅膀")) {
            return nowCards.get(2) > Integer.parseInt(cache.get(2).getSize());
        }
        // 6.连对(最小为三对)      最小的两张相同牌牌面权重 > 先出的最小的两张相同牌牌面权重 && length相等
        if (type.equals("连对")) {
            return nowCards.get(0) > Integer.parseInt(cache.get(0).getSize()) && nowCards.size() == cache.size();
        }
        // 7.顺子 (最小是5张)      最小的牌面权重 > 先出的最小牌面权重 && length相等
        if (type.equals("顺子")) {
            return nowCards.get(0) > Integer.parseInt(cache.get(0).getSize()) && nowCards.size() == cache.size();
        }
        // 8.四带二                四张相同牌中的任意一张牌面按照权重比较大小
        if (type.equals("四带对")) {
            if (Objects.equals(nowCards.get(0), nowCards.get(2))) {
                if (cache.get(0).getSize().equals(cache.get(2).getSize())) {
                    return nowCards.get(0) > Integer.parseInt(cache.get(0).getSize());
                }
                if (cache.get(3).getSize().equals(cache.get(5).getSize())) {
                    return nowCards.get(0) > Integer.parseInt(cache.get(3).getSize());
                } else {
                    return nowCards.get(0) > Integer.parseInt(cache.get(5).getSize());
                }
            }
            if (Objects.equals(nowCards.get(3), nowCards.get(5))) {
                if (cache.get(0).getSize().equals(cache.get(2).getSize())) {
                    return nowCards.get(3) > Integer.parseInt(cache.get(0).getSize());
                }
                if (cache.get(3).getSize().equals(cache.get(5).getSize())) {
                    return nowCards.get(3) > Integer.parseInt(cache.get(3).getSize());
                } else {
                    return nowCards.get(3) > Integer.parseInt(cache.get(5).getSize());
                }
            } else {
                if (cache.get(0).getSize().equals(cache.get(2).getSize())) {
                    return nowCards.get(5) > Integer.parseInt(cache.get(0).getSize());
                }
                if (cache.get(3).getSize().equals(cache.get(5).getSize())) {
                    return nowCards.get(5) > Integer.parseInt(cache.get(3).getSize());
                } else {
                    return nowCards.get(5) > Integer.parseInt(cache.get(5).getSize());
                }
            }
        }
        if (type.equals("四带单")) {
            return nowCards.get(2) > Integer.parseInt(cache.get(2).getSize());
        }
        // 9.炸弹                 大于其他所有类型 && 四张相同牌中的任一牌面按照权重比较大小 && 大小王大于所有牌
        if (type.equals("炸弹")) {
            if (getPreType().equals("炸弹")) {     //如果上家出的也是炸弹
                if (nowCards.get(0) <= Integer.parseInt(cache.get(0).getSize())) {
                    return false;
                }
            } else if (!getPreType().equals("王炸")) {  //如果上家出的不是王炸
                return true;
            }

        }

        return type.equals("王炸");
    }
//判定牌的类型
    public static String typeJudge(String next) {
        String linkCards1 = "34567890JQKA";// 顺子
        String linkCards2 = "3344556677889900JJQQKKAA";// 连对
        String linkCards3 = "333444555666777888999000JJJQQQKKKAAA";// 飞机


        String[] split = next.split(" ");//字符串分割
        next = next.replace(" ", "");   //去掉字符串里的空格
        next = next.replace("10", "0"); //将10替换为0
        next = next.replace("11", "J"); //将10替换为J
        next = next.replace("12", "Q"); //将10替换为Q
        next = next.replace("13", "K"); //将10替换为K
        next = next.replace("14", "A"); //将10替换为A
        next = next.replace("15", "2"); //将10替换为2
        next = next.replace("17", "D"); //D代表大王
        next = next.replace("16", "X"); //X代表小王

        if (next.contains("D") && next.contains("X") && next.length() != 2) {   //王炸
            return "ERROR";
        }
        if (linkCards1.contains(next) && split.length >= 5) {    //顺子
            return "顺子";
        }
        if (linkCards2.contains(next) && split.length > 5) {    //连对
            if ((split[0].equals(split[1])) && (split[split.length - 1].equals(split[split.length - 2])) && (split.length % 2 == 0)) {
                return "连对";
            }
        }
        if (split.length == 1) { //单
            return "单";
        } else if (split.length == 2) {
            if ((split[0] + split[1]).contains("17") && (split[0] + split[1]).contains("16")) {   //王炸
                return "王炸";
            }
            if (split[0].equals(split[1])) {
                return "对";
            }
        } else if (split.length == 3) {
            if (split[0].equals(split[1]) && split[1].equals(split[2])) {
                return "三带";
            }

        } else if (split.length == 4) {
            if (split[0].equals(split[1]) && split[1].equals(split[2]) && split[2].equals(split[3])) {
                return "炸弹";
            }
            if (split[0].equals(split[1]) && split[1].equals(split[2]) || split[1].equals(split[2]) && split[2].equals(split[3])) {    //三带一
                return "三带一";
            }
        } else if (split.length == 5) {
            if (split[0].equals(split[1]) && split[1].equals(split[2]) || split[1].equals(split[2]) && split[2].equals(split[3]) || split[2].equals(split[3]) && split[3].equals(split[4])) {    //三带二
                return "三带二";
            }
        } else if (split.length == 6) {    //飞机
            if (split[0].equals(split[1]) && split[1].equals(split[2]) && split[2].equals(split[3]) || split[1].equals(split[2]) && split[2].equals(split[3]) && split[3].equals(split[4]) || split[2].equals(split[3]) && split[3].equals(split[4]) && split[4].equals(split[5])) {
                return "四带单";
            }
            if (next.length() % 3 == 0 && linkCards3.contains(next))             //飞机无翅膀
                return "三顺";
        } else if (split.length == 7) {
            if (split[0].equals(split[1]) && split[1].equals(split[2]) && split[3].equals(split[4]) && split[4].equals(split[5]) || split[0].equals(split[1]) && split[1].equals(split[2]) && split[4].equals(split[5]) && split[5].equals(split[6]) || split[1].equals(split[2]) && split[2].equals(split[3]) && split[4].equals(split[5]) && split[5].equals(split[6])) {
                return "飞机单翅膀";
            }

        } else if (split.length == 8) {
            if (split[0].equals(split[1]) && split[1].equals(split[2]) && split[3].equals(split[4]) && split[4].equals(split[5]) && split[6].equals(split[7]) || split[0].equals(split[1]) && split[1].equals(split[2]) && split[3].equals(split[4]) && split[5].equals(split[6]) && split[6].equals(split[7]) || split[0].equals(split[1]) && split[2].equals(split[3]) && split[3].equals(split[4]) && split[5].equals(split[6]) && split[6].equals(split[7])) {
                return "飞机带翅膀";
            } else if (split[0].equals(split[1]) && split[1].equals(split[2]) && split[2].equals(split[3]) && split[4].equals(split[5]) && split[6].equals(split[7]) || split[0].equals(split[1]) && split[2].equals(split[3]) && split[3].equals(split[4]) && split[4].equals(split[5]) && split[6].equals(split[7]) || split[0].equals(split[1]) && split[2].equals(split[3]) && split[4].equals(split[5]) && split[5].equals(split[6]) && split[6].equals(split[7])) {
                return "四带对";
            }
        }
        return "ERROR";
    }
//初始化界面
    public void Init_Pane() {
        /*
        BackGround
         */
        BackGround.fitWidthProperty().bind(this.widthProperty());
        BackGround.fitHeightProperty().bind(this.heightProperty());
        getChildren().add(BackGround);
        /*
          Slot
         */
        Slot.layoutXProperty().bind(this.widthProperty().multiply(236.0 / 1280));
        Slot.layoutYProperty().bind(this.heightProperty().multiply(489.0 / 720));
        Slot.prefWidthProperty().bind(this.widthProperty().multiply(1098.0 / 1280));
        Slot.prefHeightProperty().bind(this.heightProperty().multiply(213.0 / 720));
        getChildren().add(Slot);
        /*
          Display
         */
        Display.layoutXProperty().bind(this.widthProperty().multiply(648.0 / 2560));
        Display.layoutYProperty().bind(this.heightProperty().multiply(396.0 / 1440));
        Display.prefWidthProperty().bind(this.widthProperty().multiply(1265.0 / 2560));
        Display.prefHeightProperty().bind(this.heightProperty().multiply(324.0 / 1440));
        getChildren().add(Display);
        /*
          Account
         */
        User.layoutYProperty().bind(this.heightProperty().multiply(370.0 / 720));
        User.prefWidthProperty().bind(this.widthProperty().multiply(236.0 / 1280));
        User.prefHeightProperty().bind(this.heightProperty().multiply(330.0 / 720));
        getChildren().add(User);
        /*
          Choose
         */
        Choose.layoutXProperty().bind(this.widthProperty().multiply(487.0 / 1280));
        Choose.layoutYProperty().bind(this.heightProperty().multiply(390.0 / 720));
        Choose.prefWidthProperty().bind(this.widthProperty().multiply(360.0 / 1280));
        Choose.prefHeightProperty().bind(this.heightProperty().multiply(55.0 / 720));
        getChildren().add(Choose);
        /*
          Game_inFor
         */
        Game_inFor.layoutXProperty().bind(this.widthProperty().multiply(517.0 / 1280));
        Game_inFor.layoutYProperty().bind(this.heightProperty().multiply(72.0 / 720));
        Game_inFor.prefWidthProperty().bind(this.widthProperty().multiply(300.0 / 1280));
        Game_inFor.prefHeightProperty().bind(this.heightProperty().multiply(50.0 / 720));
        getChildren().add(Game_inFor);
        /*
          Chat
         */
        Chat.layoutXProperty().bind(this.widthProperty().multiply(980.0 / 1280));
        Chat.prefWidthProperty().bind(this.widthProperty().multiply(300.0 / 1280));
        Chat.prefHeightProperty().bind(this.heightProperty().multiply(240.0 / 720));
        getChildren().add(Chat);
        /*
          Panel
         */
        Panel.layoutXProperty().bind(this.widthProperty().multiply(419.0 / 1280));
        Panel.prefWidthProperty().bind(this.widthProperty().multiply(500.0 / 1280));
        Panel.prefHeightProperty().bind(this.heightProperty().multiply(72.0 / 720));
        getChildren().add(Panel);
        /*
          QQWCard
         */
        BottomCard.layoutXProperty().bind(Panel.widthProperty().multiply(170.8 / 500));
        BottomCard.prefWidthProperty().bind(Panel.widthProperty().multiply(154.5 / 500));
        BottomCard.prefHeightProperty().bind(Panel.heightProperty());
        Panel.getChildren().add(BottomCard);
        /*
         * Alarm
         */
        alarm.layoutXProperty().bind(this.widthProperty().multiply(1275.0 / 2560));
        alarm.layoutYProperty().bind(this.heightProperty().multiply(733.0 / 1440));
        alarm.prefWidthProperty().bind(this.widthProperty().multiply(129.0 / 2560));
        alarm.prefHeightProperty().bind(this.heightProperty().multiply(121.0 / 1440));
        getChildren().add(alarm);
    }
//展示地址底牌
    public void LayoutQQWCard(String msg) {
        String[] card = msg.split(",");
        for (int i = 0; i < card.length; i++) {
            double x = i * 51.5;
            ImageView temp = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/pk/" + card[i] + ".jpg"))));
            temp.layoutXProperty().bind(BottomCard.widthProperty().multiply(x / 154.5));
            temp.fitWidthProperty().bind(BottomCard.widthProperty().multiply(51.5 / 154.5));
            temp.fitHeightProperty().bind(BottomCard.heightProperty());
            BottomCard.getChildren().add(temp);
        }
    }
//展示己方牌区
    public void Re_Card(User user) {
        for (int i = 0; i < user.myCards.size(); i++) {
            CardPane c = new CardPane(user.myCards.get(i));
            cardPanes.add(c);
            cardPanes.get(i).prefWidthProperty().bind(Slot.heightProperty().multiply(63.0 / 88));
            cardPanes.get(i).prefHeightProperty().bind(Slot.heightProperty());
        }
        Platform.runLater(() -> {
            for (CardPane cardPane : cardPanes) {
                Slot.getChildren().add(cardPane);
            }
        });
    }
//初始化按钮
    public void Init_Node() {
        /*
          Account
         */
        //player1
        p1.fitWidthProperty().bind(User.widthProperty());
        p1.fitHeightProperty().bind(User.heightProperty().multiply(300.0 / 330));
        Label account = new Label(players.get(UID).getoStone() + "");
        account.layoutXProperty().bind(User.widthProperty().divide(20));
        account.layoutYProperty().bind(User.heightProperty().multiply(300.0 / 330));
        account.setFont(Font.font(44));
        account.setStyle("-fx-background-color: #DCDCDC;-fx-opacity: 0.7;");
        cardNumBackGround1.layoutXProperty().bind(User.widthProperty().multiply(180.0 / 236));
        cardNumBackGround1.fitHeightProperty().bind(User.heightProperty().multiply(50.0 / 330));
        cardNumBackGround1.fitWidthProperty().bind(cardNumBackGround1.fitHeightProperty().multiply(63.0 / 88));
        cardNum1.layoutXProperty().bind(User.widthProperty().multiply(185.0 / 236));
        cardNum1.layoutYProperty().bind(User.heightProperty().multiply(10.0 / 330));
        cardNum1.setFont(Font.font(24));
        Name1.layoutXProperty().bind(User.widthProperty().multiply(30.0 / 236));
        Name1.layoutYProperty().bind(User.heightProperty().multiply(-10.0 / 330));
        Name1.setFont(Font.font(24));
        Name1.setTextFill(Color.GOLD);
        Platform.runLater(() -> {
            User.getChildren().addAll(p1, account, cardNumBackGround1, cardNum1, Name1);
        });
        //player2
        p2.fitWidthProperty().bind(this.widthProperty().multiply(188.8 / 1280));
        p2.fitHeightProperty().bind(this.heightProperty().multiply(240.0 / 720));
        p2.layoutXProperty().bind(this.widthProperty().multiply(0));
        p2.layoutYProperty().bind(this.heightProperty().multiply(85.0 / 720));
        cardNumBackGround2.layoutXProperty().bind(p2.fitWidthProperty().multiply(180.0 / 236));
        cardNumBackGround2.layoutYProperty().bind(this.heightProperty().multiply(85.0 / 720));
        cardNumBackGround2.fitHeightProperty().bind(p2.fitHeightProperty().multiply(50.0 / 330));
        cardNumBackGround2.fitWidthProperty().bind(cardNumBackGround2.fitHeightProperty().multiply(63.0 / 88));
        cardNum2.layoutXProperty().bind(cardNumBackGround2.layoutXProperty());
        cardNum2.layoutYProperty().bind(cardNumBackGround2.layoutYProperty().add(8));
        cardNum2.setFont(Font.font(24));
        Name2.layoutXProperty().bind(this.widthProperty().multiply(30.0 / 1280));
        Name2.layoutYProperty().bind(this.heightProperty().multiply(75.0 / 720));
        Name2.setFont(Font.font(24));
        Name2.setTextFill(Color.GOLD);
        Platform.runLater(() -> {
            getChildren().addAll(p2, cardNumBackGround2, cardNum2, Name2);
        });
        //player3
        p3.fitWidthProperty().bind(this.widthProperty().multiply(188.8 / 1280));
        p3.fitHeightProperty().bind(this.heightProperty().multiply(240.0 / 720));
        p3.layoutXProperty().bind(this.widthProperty().multiply(1090.0 / 1280));
        p3.layoutYProperty().bind(this.heightProperty().multiply(85.0 / 720));
        cardNumBackGround3.layoutXProperty().bind(this.widthProperty().multiply(2468.8 / 2560));
        cardNumBackGround3.layoutYProperty().bind(this.heightProperty().multiply(85.0 / 720));
        cardNumBackGround3.fitHeightProperty().bind(p3.fitHeightProperty().multiply(50.0 / 330));
        cardNumBackGround3.fitWidthProperty().bind(cardNumBackGround3.fitHeightProperty().multiply(63.0 / 88));
        cardNum3.layoutXProperty().bind(cardNumBackGround3.layoutXProperty());
        cardNum3.layoutYProperty().bind(cardNumBackGround3.layoutYProperty().add(8));
        cardNum3.setFont(Font.font(24));
        Name3.layoutXProperty().bind(this.widthProperty().multiply(1180.0 / 1280));
        Name3.layoutYProperty().bind(this.heightProperty().multiply(335.0 / 720));
        Name3.setFont(Font.font(24));
        Name3.setTextFill(Color.GOLD);
        Platform.runLater(() -> {
            getChildren().addAll(p3, cardNumBackGround3, cardNum3, Name3);
        });
        /*
          Choose
         */
        Go.prefWidthProperty().bind(Choose.widthProperty().multiply(120.0 / 360));
        Go.prefHeightProperty().bind(Choose.heightProperty());
        Skip.layoutXProperty().bind(Choose.widthProperty().multiply(240.0 / 360));
        Skip.prefWidthProperty().bind(Choose.widthProperty().multiply(120.0 / 360));
        Skip.prefHeightProperty().bind(Choose.heightProperty());
        Go.setFont(Font.font(20));
        Skip.setFont(Font.font(20));
        /*
          Alarm
         */
        alarmGround.fitWidthProperty().bind(alarm.widthProperty());
        alarmGround.fitHeightProperty().bind(alarm.heightProperty());
        limitTime.layoutXProperty().bind(alarm.widthProperty().divide(3));
        limitTime.layoutYProperty().bind(alarm.heightProperty().divide(3.5));
        limitTime.setFont(Font.font(36));
        /*
          Chat
         */
        Chat_trash.prefWidthProperty().bind(Chat.widthProperty());
        Chat_trash.prefHeightProperty().bind(Chat.heightProperty().multiply(40.0 / 240));
        Chat_record.layoutYProperty().bind(Chat.heightProperty().multiply(40.0 / 240));
        Chat_record.prefWidthProperty().bind(Chat.widthProperty());
        Chat_record.prefHeightProperty().bind(Chat.heightProperty().multiply(200.0 / 240));
        //
        Chat_trash.setEditable(true);
        Chat_trash.setOpacity(0.5);
        Chat_trash.setStyle("-fx-font-size: 20");
        //
        Chat_record.setEditable(false);
        Chat_record.setOpacity(0.5);
        Chat_record.setFont(Font.font(20));
        Chat_record.setWrapText(true);
        //
        Platform.runLater(() -> {
            Chat_trash.getItems().addAll("气死我了", "der~ der~", "狒狒");
            Chat.getChildren().addAll(Chat_trash, Chat_record);
        });
        /*
          Panel
         */
        Quit.prefWidthProperty().bind(Panel.widthProperty().multiply(85.4 / 500));
        Quit.prefHeightProperty().bind(Panel.heightProperty());
        Ai.layoutXProperty().bind(Panel.widthProperty().multiply(85.4 / 500));
        Ai.prefWidthProperty().bind(Panel.widthProperty().multiply(85.4 / 500));
        Ai.prefHeightProperty().bind(Panel.heightProperty());
        Vol.layoutXProperty().bind(Panel.widthProperty().multiply(325.3 / 500));
        Vol.prefWidthProperty().bind(Panel.widthProperty().multiply(85.4 / 500));
        Vol.prefHeightProperty().bind(Panel.heightProperty());
        Set.layoutXProperty().bind(Panel.widthProperty().multiply(410.7 / 500));
        Set.prefWidthProperty().bind(Panel.widthProperty().multiply(85.4 / 500));
        Set.prefHeightProperty().bind(Panel.heightProperty());
        Quit.setFont(Font.font(20));
        Ai.setFont(Font.font(20));
        Vol.setFont(Font.font(20));
        Set.setFont(Font.font(20));

        QQW.layoutXProperty().bind(this.widthProperty().multiply(484.4 / 1280));
        QQW.layoutYProperty().bind(this.heightProperty().multiply(72.0 / 720));
        QQW.prefWidthProperty().bind(this.widthProperty().multiply(150.0 / 1280));
        QQW.prefHeightProperty().bind(this.heightProperty().multiply(30.0 / 720));
        QQW.setStyle("-fx-background-color: Gold");
        QQW.setTextFill(Color.RED);
        Round.layoutXProperty().bind(this.widthProperty().multiply(699.7 / 1280));
        Round.layoutYProperty().bind(this.heightProperty().multiply(72.0 / 720));
        Round.prefWidthProperty().bind(this.widthProperty().multiply(150.0 / 1280));
        Round.prefHeightProperty().bind(this.heightProperty().multiply(30.0 / 720));
        Round.setStyle("-fx-background-color: gray");
        Round.setTextFill(Color.BLUE);
        Platform.runLater(() -> {
            Panel.getChildren().addAll(Quit, Ai, Vol, Set);
            getChildren().addAll(QQW, Round);
        });
        /*
        Size
         */
        this.widthProperty().addListener((observable, oldValue, newValue) -> {
            double fontSize = this.getWidth() * (20.0 / 1280);
            Go.setFont(Font.font(fontSize));
            Skip.setFont(Font.font(fontSize));
            Quit.setFont(Font.font(fontSize));
            Ai.setFont(Font.font(fontSize));
            Vol.setFont(Font.font(fontSize));
            Set.setFont(Font.font(fontSize));
            limitTime.setFont(Font.font(fontSize));
            cardNum1.setFont(Font.font(fontSize));
            cardNum2.setFont(Font.font(fontSize));
            cardNum3.setFont(Font.font(fontSize));
            Chat_record.setFont(Font.font(fontSize));
            Name1.setFont(Font.font(fontSize));
            Name2.setFont(Font.font(fontSize));
            Name3.setFont(Font.font(fontSize));
            QQW.setFont(Font.font(fontSize / 1.5));
            Round.setFont(Font.font(fontSize / 1.5));
//            alarm.DFont(fontSize);
            Chat_trash.setStyle("-fx-font-size: " + fontSize + "px;");
        });
    }
//刷新牌区
    public void Refresh(boolean up) {
        Platform.runLater(() -> {
            if (up) {
                int len = Display.getChildren().size();
                double inch = Math.min((1265.0) / (len), CardPane.LENGTH);
                double x = 0;
                for (int i = 0; i < len; i++) {
                    Display.getChildren().get(i).layoutXProperty().bind(Display.widthProperty().multiply(x / 1265));
                    x += inch;
                }
            } else {
                int len = Slot.getChildren().size();
                double inch = Math.min((1098.0 - 159 - 19) / (len), CardPane.LENGTH);
                double x = 0;
                for (int i = 0; i < len; i++) {
                    Slot.getChildren().get(i).layoutXProperty().bind(Slot.widthProperty().multiply(x / 1098));
                    x += inch;
                }
            }
        });
    }
//计时
    public void Timer() {
        alarm.getChildren().addAll(alarmGround, limitTime);
        limitTime.setText("30");

        timer = new Timer(1000, e -> {
            curr = Integer.parseInt(limitTime.getText());
            Platform.runLater(() -> {
                if (curr == 0) {
                    timer.stop();
                    LimitTimeNull();
                    return;
                }
                limitTime.setText(--curr + "");
            });
        });
        timer.start();
    }
//全部对局时间
    public long TotalTime() {
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
//超时为出牌应对
    public void LimitTimeNull() {
        if (!Slot.getChildren().isEmpty()) {
            if (cache.isEmpty()) {
                String card = cardPanes.get(0).getSize();
                String type = cardPanes.get(0).getType() + cardPanes.get(0).getSize();
                if (legal(card)) {
                    cardPanes.remove(0);
                    Slot.getChildren().remove(0);
                    Refresh(false);
                    if (UID == 0) {//A/B/C:Lay
                        Utils.log("A:" + "Lay:" + type);
                        if (Slot.getChildren().size() == 0) {
                            Utils.log("A:" + "Done");
                        }
                    } else if (UID == 1) {
                        Utils.log("B:" + "Lay:" + type);
                        if (Slot.getChildren().size() == 0) {
                            Utils.log("B:" + "Done");
                        }
                    } else {
                        Utils.log("C:" + "Lay:" + type);
                        if (Slot.getChildren().size() == 0) {
                            Utils.log("C:" + "Done");
                        }
                    }
                }
            } else {
                if (UID == 0) {//A/B/C:Skip
                    Utils.log("A:" + "Skip");
                } else if (UID == 1) {
                    Utils.log("B:" + "Skip");
                } else {
                    Utils.log("C:" + "Skip");
                }
            }
            Platform.runLater(() -> {
                ChooseNum++;
                setAllChooseTime();
                Choose.getChildren().clear();
                alarm.getChildren().clear();
            });
        }
    }
//刷新牌数
    public void FreshCardNum(int i, String card) {
        players.get(i).removeCard(card);
        int temp = UID;
        int count = 0;
        for (int j = 0; j < 3; j++) {
            if (temp == i) break;
            else {
                temp = (temp + 1) % 3;
                count++;
            }
        }
        if (count == 0) cardNum1.setText(players.get(i).getNum());
        else if (count == 1) cardNum3.setText(players.get(i).getNum());
        else cardNum2.setText(players.get(i).getNum());
    }
}
