package utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @description: 读取和保存properties配置文件
 * @author: HWinZnieJ
 * @create: 2023-09-15 09:05
 **/

public class PropertiesRelated {
    public static boolean save(String key, String value) {
        Properties prop = PropertiesRelated.read();
        prop.setProperty(key, value);
        try {
            FileOutputStream fos = new FileOutputStream("./config.properties");
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            prop.store(osw, null);
            return true;
        } catch (IOException e) {
            Logger.error("无法写入配置文件！\n错误详情：" + e);
            return false;
        }
    }

    public static Properties read() {
        File file = new File("./config.properties");
        if (file.exists()) {
            Properties prop = new Properties();
            try {
                FileInputStream fis = new FileInputStream("./config.properties");
                InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                prop.load(isr);
            } catch (IOException e) {
                Logger.error("无法读取配置文件！\n错误详情：" + e);
            }
            return prop;
        } else {
            return new Properties();
        }
    }
}
