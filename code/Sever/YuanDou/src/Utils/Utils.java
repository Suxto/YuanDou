package Utils;

import java.util.Scanner;

public class Utils {
    final static ConnectHelper connectHelper = new ConnectHelper();

    public static void disconnect() {
        connectHelper.stop();
    }

    public static String get() {
        return new Scanner(System.in).nextLine();
    }

    public static void log(String msg) {
//        System.out.println(msg);
        connectHelper.sendMsg(msg+'|');
    }
}
