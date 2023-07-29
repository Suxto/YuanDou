package Scenes;

import GameRun.Main;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;

public class Setting extends Pane {
    Button Apply = new Button("Apply"), Cancel = new Button("Cancel");
    private final ComboBox<Object> screen = new ComboBox<>();

    public Setting(Pane pane) {
        init(pane);
        Node();
        Action(pane);
    }

    public void init(Pane pane) {
        listen();
        setTranslateZ(0);
        this.setStyle("-fx-background-color: gray");
        setOpacity(0.7);
        for (int i = 0; i < pane.getChildren().size(); i++)
            pane.getChildren().get(i).setDisable(true);
        pane.getChildren().add(this);

    }

    public void Node( ) {
        screen.getItems().addAll("1920x1080", "1280x720", "FULL");
        screen.setOnAction(e -> {
            if (screen.getValue().equals("1920x1080")) {
                Main.stage.setFullScreen(false);
                Main.stage.setX((Main.mainWidth - 1920) / 2);
                Main.stage.setY((Main.mainHeight - 1080) / 2);
                Main.stage.setHeight(1080);
                Main.stage.setWidth(1920);
            } else if (screen.getValue().equals("1280x720")) {
                Main.stage.setFullScreen(false);
                Main.stage.setX((Main.mainWidth - 1280) / 2);
                Main.stage.setY((Main.mainHeight - 720) / 2);
                Main.stage.setHeight(720);
                Main.stage.setWidth(1280);
            } else {
                Main.stage.setFullScreen(true);
            }
            listen();
        });
        getChildren().add(Apply);
        screen.setLayoutY(200);
        getChildren().add(screen);
    }

    public void Action(Pane pane) {
        Apply.setOnAction(e -> {
            pane.getChildren().remove(this);
            for (int i = 0; i < pane.getChildren().size(); i++)
                pane.getChildren().get(i).setDisable(false);
        });
    }

    public void listen() {
        /* this.layoutXProperty().bind(pane.widthProperty().multiply(440/1280.0));
        this.layoutYProperty().bind(pane.heightProperty().multiply(60/720.0));
        this.prefWidthProperty().bind(pane.widthProperty().multiply(400 / 1280));
        this.prefHeightProperty().bind(pane.heightProperty().multiply(600 / 720));*/
        double w = Main.stage.getWidth() * 400 / 1280, h = Main.stage.getHeight() * 600 / 720,
                x = Main.stage.getWidth() * 440 / 1280, y = Main.stage.getHeight() * 60 / 720;
        this.setPrefWidth(w);
        this.setPrefHeight(h);
        this.setLayoutX(x);
        this.setLayoutY(y);
    }
}
