import SourceAPP.Universal;

import java.util.Scanner;

/**
 * @description: 欢迎页面，功能入口
 * @author: HWinZnieJ
 * @create: 2023-09-04 16:46
 **/

public class Main {
    public static void main(String[] args) {
        while (true) {

            System.out.println("欢迎使用椒盐音乐歌单转换小工具！");
            System.out.println("\t1. 网易云音乐");
            System.out.println("\t2. QQ音乐");
            System.out.println("\t3. 酷狗音乐");
            System.out.println("\t4. 酷我音乐");
            System.out.println("\t其他字符. 退出程序");
            System.out.print("请选择歌单来源(输入数字)：");
            Scanner scanner = new Scanner(System.in);
            switch (scanner.next()) {
                case "1" -> new Universal().init("NeteaseCloudMusic");
                case "2" -> new Universal().init("QQMusic");
                case "3" -> new Universal().init("KugouMusic");
                case "4" -> new Universal().init("KuwoMusic");
                default -> {
                    System.out.println("感谢您的使用，再见！");
                    System.exit(0);
                }
            }
        }


    }
}
