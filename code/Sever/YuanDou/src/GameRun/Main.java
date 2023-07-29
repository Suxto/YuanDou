package GameRun;

import Beans.User;
import Scenes.*;
import Utils.Utils;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {
    public static Pane currentPane;
    public static User currentUser = null;
    public static Stage stage;
    public static Scene scene = new Scene(new Pane());
    public static String mark;
    public static double mainWidth = 0;
    public static double mainHeight = 0;


    @Override
    public void start(Stage primaryStage) {
        System.setProperty("file.encoding", "UTF-8");
        stage = primaryStage;
        currentPane = new TablePickerPane();
        getInit();
        setScene(currentPane);
        stage.setTitle("⚪Battle");
        stage.show();
        stage.setOnCloseRequest(e -> Utils.disconnect());
    }


    // 获取主屏幕属性初始化界面
    public void getInit() {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        mainWidth = bounds.getWidth();
        mainHeight = bounds.getHeight();
        stage.setX(mainWidth / 5);
        stage.setY(mainHeight / 6);
        stage.setWidth(1600);
        stage.setHeight(900);
        stage.setScene(scene);
    }

    //切换页面
    public static void setScene(Pane pane) {
        currentPane = pane;
        scene.setRoot(currentPane);
    }

    public static void main(String[] args) {
        launch(args);
    }
}