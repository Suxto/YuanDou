package Scenes;

import Beans.User;
import GameRun.Main;
import Utils.UserService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Objects;

public class LoginStage extends Stage {

    private final GridPane loginPane;
    private final GridPane registerPane;
    private final VBox mainPane;
    private final Label lErrorLabel = new Label();
    private final Label rErrorLabel = new Label();

    public LoginStage() {
        setTitle("登录");
        setResizable(false);
        // 创建登录界面的布局
        loginPane = createLoginPane();

        // 创建注册界面的布局
        registerPane = createRegisterPane();

        // 创建主布局，初始显示登录界面
        mainPane = new VBox(10);
        mainPane.setAlignment(Pos.CENTER);
        mainPane.setPadding(new Insets(20));
        mainPane.getChildren().add(createTitleLabel("登录")); // 添加标题标签

        // 添加登录界面
        mainPane.getChildren().add(loginPane);

        Scene scene = new Scene(mainPane, 300, 200);
        setScene(scene);
        //设置登录背景
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/Login.png")));
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        mainPane.setBackground(background);
    }

    private GridPane createLoginPane() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        // 添加登录表单控件
        Label usernameLabel = new Label("用户名:");
        usernameLabel.setTextFill(Color.GOLD);
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("密码:");
        passwordLabel.setTextFill(Color.GOLD);
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("登录");
        Button switchToRegisterButton = new Button("注册");
        Stage that = this;
        // 设置按钮点击事件
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (loginCheck(username, password)) {
                clearError();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setHeaderText("登录成功");
                alert.setContentText("欢迎来到⚪Battle！");
                alert.showAndWait();
                that.close();
            } else {
                showError("用户名或密码错误");
            }
        });

        // 设置切换到注册界面的按钮点击事件
        switchToRegisterButton.setOnAction(event -> {
            mainPane.getChildren().clear();
            mainPane.getChildren().add(createTitleLabel("注册")); // 添加标题标签
            mainPane.getChildren().add(registerPane);
            clearError();
        });

        return getGridPane(gridPane, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, switchToRegisterButton, lErrorLabel);
    }

    private GridPane getGridPane(GridPane gridPane, Label usernameLabel, TextField usernameField, Label passwordLabel, PasswordField passwordField, Button loginButton, Button switchToRegisterButton, Label label) {
        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);

        HBox hbox = new HBox(10, loginButton, switchToRegisterButton);
        hbox.setAlignment(Pos.CENTER);
        gridPane.add(hbox, 0, 3, 2, 1);

        label.setTextFill(Color.RED);
        gridPane.add(label, 0, 2, 2, 1);

        return gridPane;
    }

    private GridPane createRegisterPane() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // 添加注册表单控件
        Label usernameLabel = new Label("用户名:");
        usernameLabel.setTextFill(Color.GOLD);
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("密码:");
        passwordLabel.setTextFill(Color.GOLD);
        PasswordField passwordField = new PasswordField();
        Button registerButton = new Button("注册");
        Button switchToLoginButton = new Button("返回登录");

        // 设置按钮点击事件
        registerButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (isValidUsername(username)) {
                if (isValidPassword(password)) {
                    UserService.addUser(username, password);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("提示");
                    alert.setHeaderText("注册成功");
                    alert.setContentText("欢迎来到⚪Battle！");
                    // 显示弹窗
                    alert.showAndWait();
                    switchToLoginButton.fire();
                } else {
                    showError("密码不合法");
                }
            } else {
                showError("用户名不合法或已被占用");
            }
        });

        // 设置切换到登录界面的按钮点击事件
        switchToLoginButton.setOnAction(event -> {
            mainPane.getChildren().clear();
            mainPane.getChildren().add(createTitleLabel("登录")); // 添加标题标签
            mainPane.getChildren().add(loginPane);
            clearError();
        });

        return getGridPane(gridPane, usernameLabel, usernameField, passwordLabel, passwordField, registerButton, switchToLoginButton, rErrorLabel);
    }

    private boolean loginCheck(String name, String passwd) {
        User user = UserService.login(name, passwd);
        if (user == null) return false;
        Main.currentUser = user;
        return true;
    }

    private Label createTitleLabel(String text) {
        Label titleLabel = new Label(text);
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        titleLabel.setTextFill(Color.WHITE);
        return titleLabel;
    }

    private boolean isValidUsername(String username) {
        // 根据需求添加合法性验证逻辑
        return username.matches("[a-zA-Z0-9]+") && UserService.usableName(username);
    }

    private boolean isValidPassword(String password) {
        // 根据需求添加合法性验证逻辑
        return password.length() >= 6;
    }

    private void showError(String message) {
        rErrorLabel.setText(message);
        lErrorLabel.setText(message);
    }

    private void clearError() {
        rErrorLabel.setText("");
        lErrorLabel.setText("");
    }

}
