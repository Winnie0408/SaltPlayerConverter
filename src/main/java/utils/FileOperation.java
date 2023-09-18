package utils;

import java.io.File;
import java.util.Scanner;

/**
 * @description: 文件相关操作
 * @author: HWinZnieJ
 * @create: 2023-09-04 16:49
 **/

public class FileOperation {
    static Scanner scanner = new Scanner(System.in);

    /**
     * 删除指定文件夹下的所有文件
     *
     * @param file
     */
    public static void deleteSubItem(File file) {
        if (file.isFile() || file.list().length == 0) {
            file.delete();
        } else {
            for (File f : file.listFiles()) {
                deleteSubItem(f); // 递归删除每一个文件
            }
//            file.delete(); // 删除文件夹
        }
    }

    /**
     * 创建所需目录
     *
     * @param file
     */
    public static void createDir(File file) {
        if (!file.exists()) {
            Logger.info("输出目录不存在，已为您自动创建");
            file.mkdirs();
        }
    }

    /**
     * 检测指定目录是否为空：
     * 若不为空，询问用户是否对目录进行清空操作；
     *
     * @param file
     */
    public static void checkDir(File file) {
        if (file.isDirectory()) {
            if (file.list().length > 0) {
                Logger.warning("输出目录" + (file.getParent() + '/' + file.getName()).replaceAll("\\\\", "/") + "不为空！推荐清空目录后再继续！");
                while (true) {
                    System.out.print("清空吗？(Y/n) ");
                    String choice = scanner.nextLine();
                    if (choice.isEmpty() || choice.equalsIgnoreCase("y")) {
                        Logger.info("输出目录已清空");
                        deleteSubItem(file);
                        break;
                    } else if (choice.equalsIgnoreCase("n")) {
                        Logger.info("清空操作已取消");
                        break;
                    } else {
                        Logger.warning("输入有误，请重新输入！");
                    }
                }
            }
        }
    }

    /**
     * 删除字符串中的双引号
     *
     * @param str 需要删除双引号的字符串
     * @return 删除结果
     */
    public static String deleteQuotes(String str) {
        return str.replaceAll("\"", "");
    }
}
