package Utils;

/**
 * @description: 日志输出
 * @author: HWinZnieJ
 * @create: 2023-09-04 16:38
 **/

public class Logger {
    private static final String RED = "\u001b[31m";
    private static final String GREEN = "\u001b[32m";
    private static final String BLUE = "\u001b[34m";
    private static final String YELLOW = "\u001b[33m";
    private static final String END = "\u001b[0m";

    public static void info(String msg) {
        System.out.println(BLUE + "[INFO] " + msg + END);
    }

    public static void warning(String msg) {
        System.out.println(YELLOW + "[WARNING] " + msg + END);
    }

    public static void error(String msg) {
        System.out.println(RED + "[ERROR] " + msg + END);
    }

    public static void success(String msg) {
        System.out.println(GREEN + "[SUCCESS] " + msg + END);
    }
}
