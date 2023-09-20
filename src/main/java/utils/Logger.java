package utils;

/**
 * @description: 日志输出
 * @author: HWinZnieJ
 * @create: 2023-09-04 16:38
 **/

public class Logger {
    private static final String BOLD = "\033[1m";
    private static final String RED = "\u001b[31m";
    private static final String GREEN = "\u001b[32m";
    private static final String BLUE = "\u001b[34m";
    private static final String YELLOW = "\u001b[33m";
    private static final String END = "\u001b[0m";

    /**
     * 输出INFO级别的日志信息
     * @param msg 要输出的字符串
     */
    public static void info(String msg) {
        System.out.println(BLUE + BOLD + "[INFO] " + END + BLUE + msg + END);
    }

    /**
     * 输出WARNING级别的日志信息
     * @param msg 要输出的字符串
     */
    public static void warning(String msg) {
        System.out.println(YELLOW + BOLD + "[WARNING] " + END + YELLOW + msg + END);
    }

    /**
     * 输出ERROR级别的日志信息
     * @param msg 要输出的字符串
     */
    public static void error(String msg) {
        System.out.println(RED + BOLD + "[ERROR] " + END + RED + msg + END);
    }

    /**
     * 输出SUCCESS级别的日志信息
     * @param msg 要输出的字符串
     */
    public static void success(String msg) {
        System.out.println(GREEN + BOLD + "[SUCCESS] " + END + GREEN + msg + END);
    }
}
