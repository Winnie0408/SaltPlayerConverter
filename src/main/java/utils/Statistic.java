package utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 向统计服务器报告程序运行结果
 * @author: HWinZnieJ
 * @create: 2023-09-19 16:34
 **/

public class Statistic {

    private static final char[] CHAR_SET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_".toCharArray();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * @description: 将一个长整数转换为指定进制的字符串
     * @param: [i, radix]
     * @return: 转换结果
     * @date: 2023-09-20 11:14
     */
    private static String toCustomRadix(long i, int radix) {
        StringBuilder sb = new StringBuilder();
        while (i != 0) {
            sb.append(CHAR_SET[(int) (i % radix)]);
            i /= radix;
        }
        return sb.reverse().toString();
    }

    /**
     * @description: 获取当前机器的UUID
     * @date: 2023-09-20 11:15
     */
    public static void saveUuid() {
        if (isEnable()) {
            Properties prop = PropertiesRelated.read();
            if (prop.getProperty("uuid") == null || prop.getProperty("uuid").isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String str64 = toCustomRadix(uuid.getMostSignificantBits() & 0x7fffffffffffffffL, 64) + toCustomRadix(uuid.getLeastSignificantBits() & 0x7fffffffffffffffL, 64);
                PropertiesRelated.save("uuid", str64);
            }
        } else
            Logger.info("已禁用【发送统计数据】功能");
    }

    /**
     * @description: 报告程序运行结果
     * @param: [type, result]
     * @date: 2023-09-20 11:15
     */
    public static void report(Map<String, Object> result) {
        if (isEnable()) {
            Logger.info("正在向服务器发送本次转换的统计数据...");
            Properties prop = PropertiesRelated.read();
            String uuid = prop.getProperty("uuid");
            String time = dateFormat.format(new Date());
            result.put("uuid", uuid);
            result.put("time", time);
            send(new JSONObject(result));
        }
    }

    /**
     * 向指定URL发送包含统计数据的POST请求
     *
     * @param data 待发送的统计数据
     */
    private static void send(JSONObject data) {
        String url = "https://dns.hwinzniej.top:28082/info/save";
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(url);

            StringEntity stringEntity = new StringEntity(data.toString());
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
//            Logger.info("状态码：" + statusCode);
//            Logger.info("响应体：" + responseBody);

            response.close();
            httpClient.close();
            if (statusCode == 200)
                Logger.success("统计数据发送成功！");
            else
                Logger.error("统计数据发送失败！\n错误详情：" + responseBody);
        } catch (IOException e) {
            Logger.error("很抱歉！在发送统计数据时出现错误！\n错误详情：" + e);
        }
    }

    /**
     * 判断是否在配置文件中启用了“发送统计数据”功能
     *
     * @return 启用-true 禁用-false
     */
    private static boolean isEnable() {
        Properties prop = PropertiesRelated.read();
        if (prop.get("enableStatistic") == null)
            return true;
        else return !prop.getProperty("enableStatistic").equals("false");
    }

}
