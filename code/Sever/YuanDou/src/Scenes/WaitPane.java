package Scenes;

import Beans.User;
import GameRun.Main;
import Utils.Utils;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class WaitPane extends Pane {
    Button Ready = new Button("Ready"), Setting = new Button("Setting"), Quit = new Button("Quit");//按钮：准备，设置，退出
    ComboBox Chat_trash = new ComboBox<>();//对话框
    TextArea Chat_record = new TextArea();//对话记录
    ImageView BackGround = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/WaitPane.png"))));//背景图
    Image ReadyTrue = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/True.png"))), Ready_No = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/False.png")));//是否准备
    ImageView user1Ready = new ImageView(), user2Ready = new ImageView(), user3Ready = new ImageView();
    ImageView user1P = new ImageView(), user2P = new ImageView(), user3P = new ImageView();
    ArrayList<User> Players = new ArrayList<>();//用户
    //    int PlayerNum = 0;//准备人数
    public int UID;//本机识别号

    public WaitPane() {
        Platform.runLater(() -> {
            Init();
            Action();
        });

    }
//更新是否准备
    public void changeReady(int locate, boolean ok) {
        switch (locate) {
            case 0:
                if (ok) user1Ready.setImage(ReadyTrue);
                else user1Ready.setImage(Ready_No);
                break;
            case 1: {
                if (ok) user2Ready.setImage(ReadyTrue);
                else user2Ready.setImage(Ready_No);
                break;
            }
            case 2: {
                if (ok) user3Ready.setImage(ReadyTrue);
                else user3Ready.setImage(Ready_No);
                break;
            }
        }
    }
//刷新房间的玩家
    public void Come(int locate, String view) {
        String loca = "/UserWait/";
        Image userImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(loca + view)));//用户图
        switch (locate) {
            case 0: {
                user1P.setImage(userImage);
                user1Ready.setImage(Ready_No);
                break;
            }
            case 1: {
                user2P.setImage(userImage);
                user2Ready.setImage(Ready_No);
                break;
            }
            case 2: {
                user3P.setImage(userImage);
                user3Ready.setImage(Ready_No);
                break;
            }
        }
    }
//解释服务器指令
    public void messageHandler(String msg) {
        String[] message = msg.split(":");
        if (message[0].equals("System")) {
            switch (message[1]) {
                case "Come": {
                    Platform.runLater(() -> {
                        analyze(message[2]);
                    });
                    break;
                }
                case "Begin": {
                    try {
                        Main.setScene(new OTablePane(Players, UID));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case "Ready": {
                    Platform.runLater(() -> {
                        Chat_record.appendText("System：对局马上开始\n");
                    });
                    break;
                }
                case "Player": {
                    Platform.runLater(() -> {
                        if (message[2].equals("A")) {
                            UID = 0;
                        } else if (message[2].equals("B")) {
                            UID = 1;
                        } else {
                            UID = 2;
                        }
                    });
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
                        case "Ready": {
                            Platform.runLater(() -> {
                                changeReady(0, true);
                            });
                            break;
                        }
                        case "Unready": {
                            Platform.runLater(() -> {
                                changeReady(0, false);
                            });
                            break;
                        }
                    }
                    break;
                }
                case "B": {
                    switch (message[1]) {
                        case "Ready": {
                            Platform.runLater(() -> {
                                changeReady(1, true);
                            });
                            break;
                        }
                        case "Unready": {
                            Platform.runLater(() -> {
                                changeReady(1, false);
                            });
                            break;
                        }
                    }
                    break;
                }
                case "C": {
                    switch (message[1]) {
                        case "Ready": {
                            Platform.runLater(() -> {
                                changeReady(2, true);
                            });
                            break;
                        }
                        case "Unready": {
                            Platform.runLater(() -> {
                                changeReady(2, false);
                            });
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }
//更新房间内玩家信息
    private void analyze(String msg) {
        user1P.setImage(null);
        user2P.setImage(null);
        user3P.setImage(null);
        user1Ready.setImage(null);
        user2Ready.setImage(null);
        user3Ready.setImage(null);
        ArrayList<User> temporary = new ArrayList<>();
        String[] userIn = msg.split("}");
        for (int i = 0; i < userIn.length; i++) {
            userIn[i] = userIn[i].replace("{{", "");
            userIn[i] = userIn[i].replace("{", "");
            userIn[i] = userIn[i].replace("}", "");
        }//分用户
        for (int i = 0; i < userIn.length; i++) {
            String[] temp1 = userIn[i].split(",");
            User temp2 = new User();
            temp2.setName(temp1[0]);
            temp2.setoStone(Long.parseLong(temp1[1]));
            temp2.setImage(temp1[3] + ".png");
            temporary.add(temp2);
            if (Boolean.parseBoolean(temp1[2]) && i == UID) {
                Ready.setText("Cancel Ready");
            }
            Come(i, temp2.getImage());
            changeReady(i, Boolean.parseBoolean(temp1[2]));
        }
        Players = temporary;
    }
//初始化界面
    public void Init() {
        /*
          BackGround
         */
        BackGround.fitWidthProperty().bind(this.widthProperty());
        BackGround.fitHeightProperty().bind(this.heightProperty());
        getChildren().add(BackGround);
        /*
          Button
         */
        Ready.layoutXProperty().bind(this.widthProperty().multiply(562.0 / 1280));
        Ready.layoutYProperty().bind(this.heightProperty().multiply(629.0 / 720));
        Ready.prefWidthProperty().bind(this.widthProperty().multiply(156.0 / 1280));
        Ready.prefHeightProperty().bind(this.heightProperty().multiply(77.0 / 720));
        Setting.layoutXProperty().bind(this.widthProperty().multiply(780.0 / 1280));
        Setting.layoutYProperty().bind(this.heightProperty().multiply(629.0 / 720));
        Setting.prefWidthProperty().bind(this.widthProperty().multiply(156.0 / 1280));
        Setting.prefHeightProperty().bind(this.heightProperty().multiply(77.0 / 720));
        Quit.layoutXProperty().bind(this.widthProperty().multiply(998.0 / 1280));
        Quit.layoutYProperty().bind(this.heightProperty().multiply(629.0 / 720));
        Quit.prefWidthProperty().bind(this.widthProperty().multiply(156.0 / 1280));
        Quit.prefHeightProperty().bind(this.heightProperty().multiply(77.0 / 720));
        Ready.setFont(Font.font(20));
        Setting.setFont(Font.font(20));
        Quit.setFont(Font.font(20));

        getChildren().addAll(Ready, Quit);
        /*
          Chat
         */
        user1Ready.layoutXProperty().bind(this.widthProperty().multiply(478.0 / 2560));
        user1Ready.layoutYProperty().bind(this.heightProperty().multiply(84.0 / 1440));
        user1Ready.fitWidthProperty().bind(this.widthProperty().multiply(158.0 / 2560));
        user1Ready.fitHeightProperty().bind(this.heightProperty().multiply(158.0 / 1440));
        user2Ready.layoutXProperty().bind(this.widthProperty().multiply(1201.0 / 2560));
        user2Ready.layoutYProperty().bind(this.heightProperty().multiply(84.0 / 1440));
        user2Ready.fitWidthProperty().bind(this.widthProperty().multiply(158.0 / 2560));
        user2Ready.fitHeightProperty().bind(this.heightProperty().multiply(158.0 / 1440));
        user3Ready.layoutXProperty().bind(this.widthProperty().multiply(1924.0 / 2560));
        user3Ready.layoutYProperty().bind(this.heightProperty().multiply(84.0 / 1440));
        user3Ready.fitWidthProperty().bind(this.widthProperty().multiply(158.0 / 2560));
        user3Ready.fitHeightProperty().bind(this.heightProperty().multiply(158.0 / 1440));
        user1P.layoutXProperty().bind(this.widthProperty().multiply(406.0 / 2560));
        user1P.layoutYProperty().bind(this.heightProperty().multiply(270.0 / 1440));
        user1P.fitWidthProperty().bind(this.widthProperty().multiply(303.0 / 2560));
        user1P.fitHeightProperty().bind(this.heightProperty().multiply(898.0 / 1440));
        user2P.layoutXProperty().bind(this.widthProperty().multiply(1129.0 / 2560));
        user2P.layoutYProperty().bind(this.heightProperty().multiply(270.0 / 1440));
        user2P.fitWidthProperty().bind(this.widthProperty().multiply(303.0 / 2560));
        user2P.fitHeightProperty().bind(this.heightProperty().multiply(898.0 / 1440));
        user3P.layoutXProperty().bind(this.widthProperty().multiply(1852.0 / 2560));
        user3P.layoutYProperty().bind(this.heightProperty().multiply(270.0 / 1440));
        user3P.fitWidthProperty().bind(this.widthProperty().multiply(303.0 / 2560));
        user3P.fitHeightProperty().bind(this.heightProperty().multiply(898.0 / 1440));
        this.getChildren().addAll(user1P, user2P, user3P);
        this.getChildren().addAll(user1Ready, user2Ready, user3Ready);
        Chat_record.layoutXProperty().bind(this.widthProperty().multiply(14.0 / 1280));
        Chat_record.layoutYProperty().bind(this.heightProperty().multiply(416.0 / 720));
        Chat_record.prefWidthProperty().bind(this.widthProperty().multiply(300.0 / 1280));
        Chat_record.prefHeightProperty().bind(this.heightProperty().multiply(250.0 / 720));
        Chat_trash.layoutXProperty().bind(this.widthProperty().multiply(14.0 / 1280));
        Chat_trash.layoutYProperty().bind(this.heightProperty().multiply(666.0 / 720));
        Chat_trash.prefWidthProperty().bind(this.widthProperty().multiply(300.0 / 1280));
        Chat_trash.prefHeightProperty().bind(this.heightProperty().multiply(40.0 / 720));
        Chat_trash.setEditable(true);
        Chat_trash.setOpacity(0.5);
        Chat_trash.setStyle("-fx-font-size: 20");
        Chat_record.setEditable(false);
        Chat_record.setOpacity(0.5);
        Chat_record.setFont(Font.font(20));
        //____//快捷
        Chat_trash.getItems().addAll("气死我了", "der~ der~", "狒狒");
        this.getChildren().addAll(Chat_trash, Chat_record);
        /*
        User
         */

        /*
        Size
         */
        Ready.widthProperty().addListener((observable, oldValue, newValue) -> {
            double fontSize = this.getWidth() * (20.0 / 1280);
            Ready.setFont(Font.font(fontSize));
            Setting.setFont(Font.font(fontSize));
            Quit.setFont(Font.font(fontSize));
            Chat_record.setFont(Font.font(fontSize));
            Chat_trash.setStyle("-fx-font-size: " + fontSize + "px;");
        });
    }
//按钮事件
    public void Action() {
        //按键事件
        Quit.setOnAction(e -> {
            Utils.log("Client:Exit");
            Main.setScene(new TablePickerPane());
        });
        Chat_trash.setOnAction(e -> {
            String msg = "Message:Chat:";
            if (Chat_trash.getSelectionModel().getSelectedIndex() != -1) {//选了
                int selectedIndex = Chat_trash.getSelectionModel().getSelectedIndex();
                Utils.log(msg + Players.get(UID).getName() + ":" + Chat_trash.getItems().get(selectedIndex));
            } else if (Chat_trash.getValue() != null) {//其他文本
                Utils.log(msg + Players.get(UID).getName() + ":" + Chat_trash.getValue());
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
            Platform.runLater(() -> {
                Chat_record.setOpacity(1);
            });
        });
        this.setOnMouseClicked(e -> {
            Platform.runLater(() -> {
                Chat_record.setOpacity(0.5);
            });
        });
        Setting.setOnAction(e -> {
//            new Setting(this);
        });
        Ready.setOnAction(e -> {
            Platform.runLater(() -> {
                if (Ready.getText().equals("Ready")) {
                    Ready.setText("Cancel Ready");
                    if (UID == 0) {//A/B/C:Lay
                        Utils.log("A:" + "Ready");
                    } else if (UID == 1) {
                        Utils.log("B:" + "Ready");
                    } else {
                        Utils.log("C:" + "Ready");
                    }
                } else {
                    Ready.setText("Ready");
                    if (UID == 0) {//A/B/C:Lay
                        Utils.log("A:" + "Unready");
                    } else if (UID == 1) {
                        Utils.log("B:" + "Unready");
                    } else {
                        Utils.log("C:" + "Unready");
                    }
                }
            });
        });
    }
}
