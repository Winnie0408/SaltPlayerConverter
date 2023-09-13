package Utils;

/**
 * @description: 从字符串二维数组中找到包含关键词的所有元素
 * @author: HWinZnieJ
 * @create: 2023-09-13 10:52
 **/

public class FindStringArray {
    public static String[][] findStringArray(String[][] stringArray, String keyWord) {
        int count = 0;
        for (int i = 0; i < stringArray.length; i++) {
            for (int j = 0; j < stringArray[i].length; j++) {
                if (stringArray[i][j].toLowerCase().contains(keyWord.toLowerCase())) {
                    count++;
                    break; // 如果找到关键词，跳出内部循环
                }
            }
        }
        String[][] result = new String[count][];
        int index = 0;
        for (int i = 0; i < stringArray.length; i++) {
            for (int j = 0; j < stringArray[i].length; j++) {
                if (stringArray[i][j].toLowerCase().contains(keyWord.toLowerCase())) {
                    result[index] = stringArray[i].clone(); // 复制整行
                    index++;
                    break; // 如果找到关键词，跳出内部循环
                }
            }
        }
        return result;
    }
}
