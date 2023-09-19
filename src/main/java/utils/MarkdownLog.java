package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * @description: 将日志信息以Markdown格式输出到文件
 * @author: HWinZnieJ
 * @create: 2023-09-19 14:36
 **/

public class MarkdownLog {
    private static final String BOLD = "**";
    private static final String RED = "<font color=red>";
    private static final String GREEN = "<font color=green>";
    private static final String BLUE = "<font color=blue>";
    private static final String YELLOW = "<font color=yellow>";
    private static final String END = "</font>";
    static Scanner scanner = new Scanner(System.in);

    private static void write(String content) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./ConvertLog.md", true))) {
            bufferedWriter.write(content);
        } catch (IOException e) {
            Logger.error("很抱歉！在写入日志时出现错误！\n错误详情：" + e);
        }
    }

    private static void newLine() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./ConvertLog.md", true))) {
            bufferedWriter.newLine();
        } catch (IOException e) {
            Logger.error("很抱歉！在写入日志时出现错误！\n错误详情：" + e);
        }
    }

    public static void date(String date) {
        write("### " + date);
        newLine();
    }

    public static void playListTitle(String title) {
        write("# " + BOLD + title + BOLD);
        newLine();
    }

    public static void info(String info) {
        write("## " + BLUE + info + END);
        newLine();
    }

    public static void succeedConvertResult(String[] header, String[][] data, int now, String total) {
        write("## " + BOLD + GREEN + now + " / " + total + END + BOLD);
        newLine();
        // Markdown表头
        write("|");
        for (String s : header) {
            write(s + "|");
        }
        newLine();

        // Markdown表头分割线
        write("|");
        for (String s : header) {
            write("-|");
        }
        newLine();

        // Markdown表格内容
        for (String[] datum : data) {
            write("|");
            for (String s : datum) {
                write(s + "|");
            }
            newLine();
        }
        newLine();
    }

    public static void failedConvertResult(String songName, String songArtist, String songAlbum, int now, String total) {
        write("## " + BOLD + RED + now + " / " + total + END + BOLD);
        newLine();
        write("### " + BOLD + RED + songName + " - " + songArtist + " - " + songAlbum + " 匹配失败" + END + BOLD);
        newLine();
    }

    public static void checkLogFile() {
        File logFile = new File("./ConvertLog.md");
        if (logFile.exists()) {
            Logger.info("检测到上次运行本程序时创建的【./ConvertLog.md】转换结果文件，推荐删除后再继续！");
            System.out.print("删除吗？(Y/n)：");
            while (true) {
                String choice = scanner.nextLine();
                if (choice.isEmpty() || choice.equalsIgnoreCase("y")) {
                    delLogFile();
                    Logger.info("转换结果文件已删除");
                    break;
                } else if (choice.equalsIgnoreCase("n")) {
                    Logger.info("删除操作已取消，本次运行时产生的转换结果将追加在原文件尾部");
                    break;
                } else
                    Logger.warning("输入有误，请重新输入！");
            }
            System.out.println();
        }
    }

    public static void delLogFile() {
        File logFile = new File("./ConvertLog.md");
        logFile.delete();
    }
}
