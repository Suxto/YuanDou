package Beans;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class Puke {
    static int pukeLeave = 0, pukeNum = 17;
    static String[] mem;
    static String format = ".jpg";
    static Image[] image;
    static ImageView[] imageViews;

    Puke(String[][] pu) {
        init(pu);
    }

    public void init(String[][] pu) {
        mem = new String[pukeNum];
        image = new Image[pukeNum];
        imageViews = new ImageView[pukeNum];
        while (pukeLeave < pukeNum) {
            int rand = (int) (Math.random() * 54);
            if (pu[rand][1].equals("true")) {
                mem[pukeLeave] = pu[rand][0];
                pu[rand][1] = "false";
                pukeLeave++;
            }
        }
        sort();
        for (int i = 0; i < pukeNum; i++) {
            double x = 19 + i * (1334.0 - 19 - 19) / pukeNum;
            String path = "./Image/puke/" + mem[i] + format;
            image[i] = new Image(path);
            imageViews[i] = new ImageView(image[i]);
        }
    }

    public void load(Pane PukeGroup, double mainWidth) {
        double mainHeight = mainWidth * 9 / 16;
        for (int i = 0; i < pukeNum; i++) {
            double x = 19 + i * (1334.0 - 19 * 7) / pukeNum;
            String path = "./Image/puke/" + mem[i] + format;
            image[i] = new Image(path);
            imageViews[i] = new ImageView(image[i]);
            imageViews[i].setFitWidth(mainWidth * 159 / 1334);
            imageViews[i].setFitHeight(mainHeight * 213 / 750);
            imageViews[i].setLayoutX(mainWidth * x / 1334);
            imageViews[i].fitWidthProperty().bind(PukeGroup.widthProperty().multiply(159.0 / 1334));
            imageViews[i].fitHeightProperty().bind(PukeGroup.heightProperty());
            imageViews[i].layoutXProperty().bind(PukeGroup.widthProperty().multiply(x / 1334));
            PukeGroup.getChildren().add(imageViews[i]);
            int finalI = i;
            imageViews[i].setOnMouseClicked(e -> {
                if (imageViews[finalI].getLayoutY() == 0)
                    imageViews[finalI].setLayoutY(-60);
                else
                    imageViews[finalI].setLayoutY(0);
            });
        }
    }
    public static void sort() {
        for (int i = 0; i < pukeNum; i++) {
            for (int j = i + 1; j < pukeNum; j++) {
                if (Integer.parseInt(mem[i].substring(1)) < Integer.parseInt(mem[j].substring(1))) {
                    String temp = mem[i];
                    mem[i] = mem[j];
                    mem[j] = temp;
                }
            }
        }
    }
}
