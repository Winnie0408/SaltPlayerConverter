package utils;

import java.util.Properties;
import java.util.Scanner;

/**
 * @description: 检测使用终端的字符编码
 * @author: HWinZnieJ
 * @create: 2023-09-27 19:13
 **/

public class TerminalCharSetDetect {
    public static void get() {
        String charSet = "UTF-8";
        Properties props = PropertiesRelated.read();
        if (props.getProperty("terminalCharSet") == null || props.getProperty("terminalCharSet").isEmpty()) {
            while (true) {
                Scanner scanner = new Scanner(System.in, charSet);
                Logger.info("检测到您未设置终端字符编码，将为您自动检测");
                System.out.print("请随意输入几个中文字符：");
                System.out.println(scanner.nextLine());
                System.out.print("上面的中文字符与您之前输入的是否一致？(y/n)：");
                String s = scanner.nextLine();
                if (s.equalsIgnoreCase("y")) {
                    Logger.info("您当前终端的字符编码为：" + charSet);
                    PropertiesRelated.save("terminalCharSet", charSet);
                    break;
                } else if (s.equalsIgnoreCase("n")) {
                    charSet = "GBK";
                    Logger.info("您当前终端的字符编码为：" + charSet);
                    PropertiesRelated.save("terminalCharSet", charSet);
                    break;
                } else {
                    Logger.error("输入错误，请重新输入！");
                }
            }
        }
    }
}
