package Scenes;

import Beans.User;
import GameRun.Main;
import Utils.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Objects;

public class ChargePane extends Stage {


    public ChargePane() {
        setTitle("充值");
        setResizable(false);
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/Charge.jpg"))));
        imageView.setFitWidth(300);
        imageView.setFitHeight(420);
        Button button = new Button("完成");
        Stage that = this;
        button.setOnAction(e -> {
            that.close();
        });
        VBox mainPane = new VBox(imageView, button);
        mainPane.setSpacing(15);
        mainPane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(mainPane, 300, 500);
        setScene(scene);
    }
}
