package Beans;

import Utils.UserService;

import java.util.ArrayList;
import java.util.Arrays;

public class User {
    private String name;
    private int id;
    private long oStone;
    private String passwd;
    private String image;
    private Boolean isD = false;    //地主判断位
    public ArrayList<String> myCards = new ArrayList<>();     //手牌的权重序列

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getoStone() {
        return oStone;
    }

    public void setoStone(long oStone) {
        this.oStone = oStone;
    }

    public void updateOStone(long oStone) {
        this.oStone = oStone;
        UserService.setOStone(this.id, oStone);
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setD(Boolean d) {
        isD = d;
    }

    public boolean getD() {
        return isD;
    }

    public void updateOStone() {
        oStone = UserService.getOStone(id);
    }

    public void setMyCards(String cards) {
        String[] cd = cards.split(",");
        myCards.addAll(Arrays.asList(cd));
    }

    public String getNum() {
        return String.valueOf(myCards.size());
    }

    public void removeCard(String card) {
        String[] remove = card.split(" ");
        for (String s : remove) {
            myCards.remove(s);
        }
    }

}
