package Beans;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Objects;

public class CardPane extends Pane {
    private final Card card = new Card();
    public final static String path = "/pk/";
    public static double LENGTH = 60;
    public final ImageView imageView = new ImageView();

    public CardPane(String url) {
        imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path + url + ".jpg"))));
        init();
        setInfor(url);
        getChildren().add(imageView);
        Action();
    }

    public String getSize() {
        return card.size;
    }

    public String getType() {
        return card.type;
    }

    public void setInfor(String infor) {
        card.type = infor.substring(0, 1);
        card.size = infor.substring(1);
    }

    public void init() {
        imageView.fitWidthProperty().bind(this.widthProperty());
        imageView.fitHeightProperty().bind(this.heightProperty());
    }

    public void Action() {
        setOnMouseClicked(e -> {
            if (this.getLayoutY() == 0) {
                this.setLayoutY(-30);
            } else {
                this.setLayoutY(0);
            }
        });
    }
}
