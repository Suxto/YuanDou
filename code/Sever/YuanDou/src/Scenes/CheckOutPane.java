package Scenes;


import Beans.User;
import GameRun.Main;
import Utils.Utils;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Objects;


public class CheckOutPane extends Pane {
    private final boolean Win;
    User Player;
    private long nowStone;
    private final long msTime;
    private final int avgTime;
    ImageView Victory_Defeat = new ImageView(), BackGround = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/final.jpg"))));
    Image Victory = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/victory.png"))),
            Defeat = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/defeat.png")));
    TextArea Chat_record = new TextArea();
    ComboBox<Object> Chat_trash = new ComboBox<>();
    Button Back = new Button("Back"), Another = new Button("Another"), Exit = new Button("Exit");
    Label stone = new Label("Added OStone "), AvgTime = new Label("AVG Time: "), TotalTime = new Label("Total Time: ");
    Label Value_stone = new Label(), Value_AvgTime = new Label(), Value_TotalTime = new Label();

    public CheckOutPane(User user, long nowStone, long msTime, boolean win, int avg) {
        this.avgTime = avg;
        this.nowStone = nowStone;
        this.msTime = msTime;
        this.Win = win;
        this.Player = user;
        Platform.runLater(() -> {
            getInformation();
            Init();
            Action();
        });
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
          user
         */
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/UserBattle/" + Player.getImage()))));
        imageView.fitWidthProperty().bind(this.widthProperty().multiply(367.0 / 1280));
        imageView.fitHeightProperty().bind(this.heightProperty().multiply(441.0 / 720));
        imageView.layoutXProperty().bind(this.widthProperty().multiply(73.0 / 1280));
        imageView.layoutYProperty().bind(this.heightProperty().multiply(117.0 / 720));
        getChildren().add(imageView);
        /*
          Victory or Defeat
         */
        Victory_Defeat.fitWidthProperty().bind(this.widthProperty().multiply(313.0 / 1280));
        Victory_Defeat.fitHeightProperty().bind(this.heightProperty().multiply(143.0 / 720));
        Victory_Defeat.layoutXProperty().bind(this.widthProperty().multiply(513.0 / 1280));
        Victory_Defeat.layoutYProperty().bind(this.heightProperty().multiply(53.0 / 720));
        getChildren().add(Victory_Defeat);
        /*
          Information
         */
        stone.layoutXProperty().bind(this.widthProperty().multiply(471.0 / 1280));
        stone.layoutYProperty().bind(this.heightProperty().multiply(230.0 / 720));
        stone.prefWidthProperty().bind(this.widthProperty().multiply(313.0 / 1280));
        stone.prefHeightProperty().bind(this.heightProperty().multiply(61.0 / 720));
        stone.setFont(Font.font("SimHei", FontWeight.BOLD, 44));
        stone.setTextFill(Color.WHITE);
        AvgTime.layoutXProperty().bind(this.widthProperty().multiply(471.0 / 1280));
        AvgTime.layoutYProperty().bind(this.heightProperty().multiply(340.0 / 720));
        AvgTime.prefWidthProperty().bind(this.widthProperty().multiply(313.0 / 1280));
        AvgTime.prefHeightProperty().bind(this.heightProperty().multiply(61.0 / 720));
        AvgTime.setFont(Font.font("SimHei", FontWeight.BOLD, 44));
        AvgTime.setTextFill(Color.WHITE);
        TotalTime.layoutXProperty().bind(this.widthProperty().multiply(471.0 / 1280));
        TotalTime.layoutYProperty().bind(this.heightProperty().multiply(440.0 / 720));
        TotalTime.prefWidthProperty().bind(this.widthProperty().multiply(313.0 / 1280));
        TotalTime.prefHeightProperty().bind(this.heightProperty().multiply(61.0 / 720));
        TotalTime.setFont(Font.font("SimHei", FontWeight.BOLD, 44));
        TotalTime.setTextFill(Color.WHITE);
        getChildren().addAll(stone, AvgTime, TotalTime);
        /*
          Value
         */
        Value_stone.layoutXProperty().bind(this.widthProperty().multiply(730.0 / 1280));
        Value_stone.layoutYProperty().bind(this.heightProperty().multiply(230.0 / 720));
        Value_stone.prefWidthProperty().bind(this.widthProperty().multiply(313.0 / 1280));
        Value_stone.prefHeightProperty().bind(this.heightProperty().multiply(61.0 / 720));
        Value_stone.setFont(Font.font("SimHei", FontWeight.BOLD, 44));
        Value_stone.setTextFill(Color.YELLOW);
        Value_AvgTime.layoutXProperty().bind(this.widthProperty().multiply(730.0 / 1280));
        Value_AvgTime.layoutYProperty().bind(this.heightProperty().multiply(340.0 / 720));
        Value_AvgTime.prefWidthProperty().bind(this.widthProperty().multiply(313.0 / 1280));
        Value_AvgTime.prefHeightProperty().bind(this.heightProperty().multiply(61.0 / 720));
        Value_AvgTime.setFont(Font.font("SimHei", FontWeight.BOLD, 44));
        Value_AvgTime.setTextFill(Color.YELLOW);
        Value_TotalTime.layoutXProperty().bind(this.widthProperty().multiply(730.0 / 1280));
        Value_TotalTime.layoutYProperty().bind(this.heightProperty().multiply(440.0 / 720));
        Value_TotalTime.prefWidthProperty().bind(this.widthProperty().multiply(313.0 / 1280));
        Value_TotalTime.prefHeightProperty().bind(this.heightProperty().multiply(61.0 / 720));
        Value_TotalTime.setFont(Font.font("SimHei", FontWeight.BOLD, 44));
        Value_TotalTime.setTextFill(Color.YELLOW);
        getChildren().addAll(Value_stone, Value_AvgTime, Value_TotalTime);
        /*
          Button
         */
        Back.layoutXProperty().bind(this.widthProperty().multiply(62.0 / 1280));
        Back.layoutYProperty().bind(this.heightProperty().multiply(602.0 / 720));
        Back.prefWidthProperty().bind(this.widthProperty().multiply(198.0 / 1280));
        Back.prefHeightProperty().bind(this.heightProperty().multiply(58.0 / 720));
        Back.setFont(Font.font(20));

        Another.layoutXProperty().bind(this.widthProperty().multiply(300.0 / 1280));
        Another.layoutYProperty().bind(this.heightProperty().multiply(602.0 / 720));
        Another.prefWidthProperty().bind(this.widthProperty().multiply(198.0 / 1280));
        Another.prefHeightProperty().bind(this.heightProperty().multiply(58.0 / 720));
        Another.setFont(Font.font(20));

        Exit.layoutXProperty().bind(this.widthProperty().multiply(538.0 / 1280));
        Exit.layoutYProperty().bind(this.heightProperty().multiply(602.0 / 720));
        Exit.prefWidthProperty().bind(this.widthProperty().multiply(198.0 / 1280));
        Exit.prefHeightProperty().bind(this.heightProperty().multiply(58.0 / 720));
        Exit.setFont(Font.font(20));

        getChildren().addAll(Back, Another, Exit);
        /*
          Chat
         */
        Chat_trash.layoutXProperty().bind(this.widthProperty().multiply(955.0 / 1280));
        Chat_trash.layoutYProperty().bind(this.heightProperty().multiply(501.0 / 720));
        Chat_trash.prefWidthProperty().bind(this.widthProperty().multiply(216.0 / 1280));
        Chat_trash.prefHeightProperty().bind(this.heightProperty().multiply(36.0 / 720));

        Chat_record.layoutXProperty().bind(this.widthProperty().multiply(955.0 / 1280));
        Chat_record.layoutYProperty().bind(this.heightProperty().multiply(220.0 / 720));
        Chat_record.prefWidthProperty().bind(this.widthProperty().multiply(216.0 / 1280));
        Chat_record.prefHeightProperty().bind(this.heightProperty().multiply(281.0 / 720));

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
//        Platform.runLater(() -> {
        Chat_trash.getItems().addAll("气死我了", "der~ der~", "狒狒");
        getChildren().addAll(Chat_trash, Chat_record);
//        });
        /*
          Size
         */
        this.widthProperty().addListener((observable, oldValue, newValue) -> {
            double fontSize = this.getWidth() * (20.0 / 1280);
            stone.setFont(Font.font(fontSize));
            AvgTime.setFont(Font.font(fontSize));
            TotalTime.setFont(Font.font(fontSize));
            Back.setFont(Font.font(fontSize));
            Exit.setFont(Font.font(fontSize));
            Another.setFont(Font.font(fontSize));
            Chat_record.setFont(Font.font(fontSize));
            Chat_trash.setStyle("-fx-font-size: " + fontSize + "px;");
        });
    }
//更新信息
    public void getInformation() {
        if (Win) {
            if (Player.getD()) {
                Value_stone.setText("+200");
                nowStone += 200;
            } else {
                Value_stone.setText("+100");
                nowStone += 100;
            }
            Victory_Defeat.setImage(Victory);
        } else {
            if (Player.getD()) {
                Value_stone.setText("-200");
                nowStone -= 200;
            } else {
                Value_stone.setText("-100");
                nowStone -= 100;
            }
            Victory_Defeat.setImage(Defeat);
        }
        Main.currentUser.updateOStone(nowStone);
        long s = (msTime / 1000) % 60;
        long m = (msTime / 1000) / 60 % 60;
        String totalTime = m + "min " + s + "s";
        Value_TotalTime.setText(totalTime);
        Value_AvgTime.setText(avgTime + " s/次");
    }
//解释服务器命令
    public void messageHandler(String msg) {
        String[] message = msg.split(":");
        if (message[0].equals("Message")) {
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
        }
    }
//按钮事件
    public void Action() {
        Back.setOnAction(e -> {
            Utils.log("Client:Exit");
            Main.setScene(new TablePickerPane());
        });
        Another.setOnAction(e -> {
            Main.setScene(new WaitPane());
        });
        Exit.setOnAction(e -> {
            Utils.log("Client:Exit");
            Runtime.getRuntime().exit(0);
        });
        Chat_trash.setOnAction(e -> {
            String msg = "Message:Chat:";
            if (Chat_trash.getSelectionModel().getSelectedIndex() != -1) {//选了
                int selectedIndex = Chat_trash.getSelectionModel().getSelectedIndex();
                Utils.log(msg + Player.getName() + ":" + Chat_trash.getItems().get(selectedIndex));
            } else if (Chat_trash.getValue() != null) {//其他文本
                Utils.log(msg + Player.getName() + ":" + Chat_trash.getValue());
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
        });
        BackGround.setOnMouseClicked(e -> {
            Chat_record.setOpacity(0.5);
        });
    }
}
