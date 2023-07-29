package Scenes;

import Beans.User;
import GameRun.Main;
import Utils.Utils;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;

import java.util.ArrayList;
import java.util.Objects;


public class TablePickerPane extends VBox {
    private static TableItem selected = null;
    private final HBox userInfo = new HBox();
    private final FlowPane flowPane = new FlowPane();
    private final Button btCreate;

    class TableItem extends StackPane {
        Label label;
        Rectangle rectangle;
        boolean playing;

        TableItem(int num, boolean playing) {
            this.setAlignment(Pos.CENTER);
            this.playing = playing;
            String state = playing ? "已开局" : "等待玩家";
            label = new Label(num + "/3\n" + state);
            rectangle = new Rectangle(200, 100);
            rectangle.setFill(Color.TRANSPARENT); // 设置填充颜色为透明
            rectangle.setStroke(Color.BLACK); // 设置边框颜色为黑色
            rectangle.setStrokeWidth(1); // 设置边框宽度为1px
            rectangle.setArcWidth(10); // 设置圆角横向半径为10
            rectangle.setArcHeight(10);
            this.getChildren().addAll(label, rectangle);
            this.setOnMouseClicked(e -> {
                if (playing) return;
                if (selected != null) selected.setNotSelected();
                setSelected();
                if (e.getClickCount() == 2) {
                    joinTable(this);
                }
            });
        }

        public void setNotSelected() {
            selected = null;
            this.setStyle("-fx-background-color: #00000000");
        }

        public void setSelected() {
            selected = this;
            this.setStyle("-fx-background-color: #00AAAA80;-fx-background-radius: 10");
        }
    }


    public TablePickerPane() {
        setStyle("-fx-background-image: url('/Image/Table.jpg'); " + "-fx-background-size: cover; ");
        btCreate = new Button("新建台桌");
        btCreate.setOnAction(e -> createTable());
        Button btnRefresh = new Button("刷新");
        btnRefresh.setOnAction(e -> refresh());
        HBox buttons = new HBox(btCreate, btnRefresh);
        buttons.setSpacing(20);
        buttons.setPadding(new Insets(5));
        userInfo.setAlignment(Pos.CENTER_LEFT);
        buttons.setAlignment(Pos.CENTER_LEFT);
        // 创建一个占位区域
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox upper = new HBox(userInfo, spacer, buttons);
        if (Main.currentUser == null) notLoggedIn();
        else showUserInfo();

        // 设置右对齐的按钮居右对齐
        upper.setAlignment(Pos.CENTER_RIGHT);
        this.getChildren().addAll(upper, flowPane);
        flowPane.setPadding(new Insets(0, 15, 0, 15));
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        userInfo.setPadding(new Insets(5));
        userInfo.setSpacing(15);
        userInfo.setPrefHeight(50);
        refresh();
    }

    private void showUserInfo() {
        btCreate.setDisable(false);
        userInfo.getChildren().clear();
        User user = Main.currentUser;
        user.updateOStone();
        sendInfo();
        Button button = new Button("登出");
        button.setOnAction(e -> notLoggedIn());
        Button btCharge = new Button("充值");
        btCharge.setOnAction(e -> {
            ChargePane chargePane = new ChargePane();
            chargePane.initModality(Modality.APPLICATION_MODAL);
            chargePane.showAndWait();
        });
        userInfo.getChildren().addAll(new Label("用户：" + user.getName()), new Label("原石：" + user.getoStone()), button, btCharge);
    }

    private void notLoggedIn() {
        btCreate.setDisable(true);
        userInfo.getChildren().clear();
        Main.currentUser = null;
        Button btLongin = new Button("登录");
        btLongin.setOnAction(e -> {
            LoginStage loginStage = new LoginStage();
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.showAndWait();
            User user = Main.currentUser;
            if (user != null) {
                showUserInfo();
            }
        });
        userInfo.getChildren().add(btLongin);
    }

    private void refresh() {
        Utils.log("Client:Get Tables");
        if (Main.currentUser != null) showUserInfo();
    }

    public void createTable() {
        Utils.log("Client:Create Table");
        refresh();
    }

    public void joinTable(TableItem item) {
        User user = Main.currentUser;
        if (user == null) {
            Alert alert = new javafx.scene.control.Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("请登录");
            alert.setContentText("不登陆的话无法进入牌桌");
            // 显示弹窗
            alert.showAndWait();
            return;
        }
        sendInfo();
        int idx = flowPane.getChildren().indexOf(item);
        Utils.log("Client:Join Table " + idx);
        Main.setScene(new WaitPane());
    }

    private void sendInfo() {
        User user = Main.currentUser;
        Utils.log("Client:Info:" + user.getName() + "," + user.getoStone() + "," + user.getImage());
    }

    ArrayList<TableItem> tableItems = new ArrayList<>();

    public void messageHandler(String message) {
        if (message.startsWith("System:Player ")) {
            Main.mark = message.substring("System:Player ".length());
            System.out.println(Main.mark);
        } else if (message.startsWith("System:Tables{")) {
            message = message.substring("System:Tables{".length());
            String[] strings = message.split("}");
//            flowPane.getChildren().clear();
            tableItems.clear();
            for (String s : strings) {
                if (s.length() <= 1) break;
                s = s.substring(1);
                String[] tmp = s.split(",");
                tableItems.add(new TableItem(Integer.parseInt(tmp[0]), Boolean.parseBoolean(tmp[1])));
            }
        }
        Platform.runLater(() -> {
            flowPane.getChildren().clear();
            tableItems.forEach(e -> flowPane.getChildren().add(e));
        });
    }


}
