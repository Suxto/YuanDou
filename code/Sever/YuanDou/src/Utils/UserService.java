package Utils;

import Beans.User;

public class UserService {
    public static boolean usableName(String name) {
        String sql = "SELECT COUNT(*) FROM Users WHERE name=?";
        return JDBC.getCount(sql, name) == 0;
    }

    public static void addUser(String name, String passwd) {
        if (!usableName(name)) return;
        String sql = "INSERT INTO Users(name,passwd) VALUES(?,?)";
        JDBC.update(sql, name, passwd);
    }

    public static User login(String name, String passwd) {
        String sql = "SELECT * FROM Users WHERE name=? AND passwd=?";
        return JDBC.getOne(User.class, sql, name, passwd);
    }

    public static void setOStone(int id, long num) {
        String sql = "UPDATE Users SET oStone=? WHERE id=?";
        JDBC.update(sql, num, id);
    }

    public static long getOStone(int id) {
        String sql = "SELECT oStone FROM Users WHERE id=?";
        return JDBC.getCount(sql, id);
    }

    public static void main(String[] args) {
//        addUser("1234", "1213232");
//        setOStone(4,10000);
//        System.out.println(login("1234", "1213232"));
//        System.out.println(login("1234","1213232"));
    }
}
