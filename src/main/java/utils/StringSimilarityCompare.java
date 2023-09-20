package utils;

/**
 * @description: 获取两个字符串的相似度
 * @author: HWinZnieJ
 * @create: 2023-09-04 16:42
 **/

public class StringSimilarityCompare {
    /**
     * 获取最长子串 (参数顺序与字符串长短无关)
     *
     * @param strA
     * @param strB
     * @return
     */
    public static String longestCommonSubstringNoOrder(String strA, String strB) {
        if (strA.length() >= strB.length()) {
            return longestCommonSubstring(strA, strB);
        } else {
            return longestCommonSubstring(strB, strA);
        }
    }

    /**
     * 获取最长子串 （长串在前，短串在后）
     *
     * @param strLong
     * @param strShort
     * @return <p>summary</p>:较长的字符串放到前面有助于提交效率
     */
    private static String longestCommonSubstring(String strLong, String strShort) {
        char[] chars_strA = strLong.toCharArray();
        char[] chars_strB = strShort.toCharArray();
        int m = chars_strA.length;
        int n = chars_strB.length;
        int[][] matrix = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (chars_strA[i - 1] == chars_strB[j - 1]) {
                    matrix[i][j] = matrix[i - 1][j - 1] + 1;
                } else {
                    matrix[i][j] = Math.max(matrix[i][j - 1], matrix[i - 1][j]);
                }
            }
        }
        char[] result = new char[matrix[m][n]];
        int currentIndex = result.length - 1;
        while (matrix[m][n] != 0) {
            if (matrix[n] == matrix[n - 1]) {
                n--;
            } else if (matrix[m][n] == matrix[m - 1][n]) {
                m--;
            } else {
                result[currentIndex] = chars_strA[m - 1];
                currentIndex--;
                n--;
                m--;
            }
        }
        return new String(result);
    }

    private static boolean charReg(char charValue) {
        return (charValue >= 0x4E00 && charValue <= 0X9FA5) || (charValue >= 'a' && charValue <= 'z') || (charValue >= 'A' && charValue <= 'Z') || (charValue >= '0' && charValue <= '9');
    }

    private static String removeSign(String str) {
        StringBuilder sb = new StringBuilder();
        for (char item : str.toCharArray()) {
            if (charReg(item)) {
                sb.append(item);
            }
        }
        return sb.toString();
    }

    /**
     * 比较俩个字符串的相似度（方式一）
     * 步骤1：获取两个串中最长共同子串（有序非连续）
     * 步骤2：共同子串长度 除以 较长串的长度
     *
     * @param strA
     * @param strB
     * @return 两个字符串的相似度
     */
    public static double SimilarDegree(String strA, String strB) {
        String newStrA = removeSign(strA);
        String newStrB = removeSign(strB);
        int temp = Math.max(newStrA.length(), newStrB.length());
        int temp2 = longestCommonSubstringNoOrder(newStrA, newStrB).length();
        return temp2 * 1.0 / temp;
    }

    /**
     * 第二种实现方式 (获取两串不匹配字符数)
     *
     * @param str
     * @param target
     * @return
     */
    private static int compare(String str, String target) {
        int[][] d; // 矩阵
        int n = str.length();
        int m = target.length();
        int i; // 遍历str
        int j; // 遍历target
        char ch1; // str
        char ch2; // target
        int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new int[n + 1][m + 1];
        // 初始化第一列
        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        // 初始化第一行
        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }
        // 遍历str
        for (i = 1; i <= n; i++) {
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++) {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    private static int min(int one, int two, int three) {
        return (one = Math.min(one, two)) < three ? one : three;
    }

    /**
     * 比较俩个字符串的相似度（方式一）
     * 步骤1：获取两个串中不相同的字符数
     * 步骤2：不同字符数 除以 较长串的长度
     *
     * @param strA
     * @param strB
     * @return
     */
    public static double similarityRatio(String strA, String strB) {
        return 1 - (double) compare(strA, strB) / Math.max(strA.length(), strB.length());
    }

}


