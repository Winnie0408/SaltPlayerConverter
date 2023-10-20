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
    static Scanner scanner = new Scanner(System.in, PropertiesRelated.read().getProperty("terminalCharSet"));

    /**
     * 将内容写入文件
     *
     * @param content 要写入的内容
     */
    private static void write(String content) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./ConvertLog.md", true))) {
            bufferedWriter.write(content);
        } catch (IOException e) {
            Logger.error("很抱歉！在写入日志时出现错误！\n错误详情：" + e);
        }
    }

    /**
     * 换行
     */
    private static void newLine() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./ConvertLog.md", true))) {
            bufferedWriter.newLine();
        } catch (IOException e) {
            Logger.error("很抱歉！在写入日志时出现错误！\n错误详情：" + e);
        }
    }

    /**
     * 将日期按预定格式写入文件
     *
     * @param date 当前日期
     */
    public static void date(String date) {
        write("### " + date);
        newLine();
    }

    /**
     * 将标题按预定格式写入文件
     *
     * @param title 要写入的标题
     */
    public static void playListTitle(String title) {
        write("# " + BOLD + title + BOLD);
        newLine();
    }

    /**
     * 将INFO级别的信息写入文件
     *
     * @param info 要写入的日志内容
     */
    public static void info(String info) {
        write("## " + BLUE + info + END);
        newLine();
    }

    /**
     * 转换成功的结果表格写入文件
     *
     * @param header 表头
     * @param data   表的数据
     * @param now    当前是第几首歌
     * @param total  总共有多少首歌
     */
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

    /**
     * 转换失败的结果详情写入文件
     *
     * @param songName   歌名
     * @param songArtist 艺术家
     * @param songAlbum  专辑名
     * @param now        当前是第几首歌
     * @param total      总共有多少首歌
     */
    public static void failedConvertResult(String songName, String songArtist, String songAlbum, int now, String total) {
        write("## " + BOLD + RED + now + " / " + total + END + BOLD);
        newLine();
        write("### " + BOLD + RED + "歌名：" + songName + END + BOLD);
        newLine();
        write("### " + BOLD + RED + "艺术家：" + songArtist + END + BOLD);
        newLine();
        write("### " + BOLD + RED + "专辑：" + songAlbum + END + BOLD);
        newLine();
    }

    /**
     * 检查结果文件是否存在，如存在则询问用户是否删除
     */
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
                    Logger.info("删除操作已取消，本次运行所产生的转换结果将追加在原文件尾部");
                    break;
                } else
                    Logger.warning("输入有误，请重新输入！");
            }
            System.out.println();
        }
    }

    /**
     * 删除转换结果文件
     */
    public static void delLogFile() {
        File logFile = new File("./ConvertLog.md");
        logFile.delete();
    }
}
