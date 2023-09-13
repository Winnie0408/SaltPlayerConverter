package Utils;

/**
 * @description: 从字符串二维数组中找到包含关键词的所有元素
 * @author: HWinZnieJ
 * @create: 2023-09-13 10:52
 **/

public class FindStringArray {
    public static String[][] findStringArray(String[][] stringArray, String keyWord) {
        int count = 0;
        for (String[] strings : stringArray) {
            for (String string : strings) {
                if (string.toLowerCase().contains(keyWord.toLowerCase())) {
                    count++;
                    break; // 如果找到关键词，跳出内部循环
                }
            }
        }
        String[][] result = new String[count][];
        int index = 0;
        for (String[] strings : stringArray) {
            for (String string : strings) {
                if (string.toLowerCase().contains(keyWord.toLowerCase())) {
                    result[index] = strings.clone(); // 复制整行
                    index++;
                    break; // 如果找到关键词，跳出内部循环
                }
            }
        }
        return result;
    }
}
