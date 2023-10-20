package utils;

/**
 * @description: 以表格形式输出所给数据
 * @author: HWinZnieJ
 * @create: 2023-09-04 16:44
 **/

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class TablePrinter {
    private static final String HORIZONTAL_LINE = "-";
    private static final String VERTICAL_LINE = "|";
    private static final String CROSS_LINE = "+";
    private static final String SPACE = " ";
    private static final String NEW_LINE = System.lineSeparator();
    private static final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    /**
     * @param header 表头
     * @param data   数据
     * @param title  表的总标题
     */
    public static void printTable(String[] header, String[][] data, String title) {
//        System.out.println("\n");
        int numColumns = header.length;
        int numRows = data.length;

        // 计算每一列的最大宽度
        int[] columnWidths = new int[numColumns];
        for (int i = 0; i < numColumns; i++) {
            int maxWidth = getDisplayWidth(header[i]);
            for (String[] datum : data) {
                int dataWidth = getDisplayWidth(datum[i]);
                if (dataWidth > maxWidth) {
                    maxWidth = dataWidth;
                }
            }
            columnWidths[i] = maxWidth;
        }

        // 输出表格标题
        int titleWidth = getDisplayWidth(title);
        int padding = (numColumns * 3 - titleWidth) / 2;
        out.print(VERTICAL_LINE);
        for (int i = 0; i < padding; i++) {
            out.print(SPACE);
        }
        out.print(title);
        for (int i = padding + titleWidth; i < numColumns * 3; i++) {
            out.print(SPACE);
        }
        out.println(VERTICAL_LINE);

        // 输出表头
        out.print(VERTICAL_LINE);
        for (int i = 0; i < numColumns; i++) {
            out.print(SPACE);
            int paddingSize = columnWidths[i] - getDisplayWidth(header[i]);
            for (int j = 0; j < paddingSize / 2; j++) {
                out.print(SPACE);
            }
            out.print(header[i]);
            for (int j = paddingSize / 2 + (paddingSize % 2); j < paddingSize; j++) {
                out.print(SPACE);
            }
            out.print(SPACE + VERTICAL_LINE + SPACE);
        }
        out.println();

        // 输出分隔线
        out.print(CROSS_LINE);
        for (int i = 0; i < numColumns; i++) {
            out.print(HORIZONTAL_LINE);
            for (int j = 0; j < columnWidths[i]; j++) {
                out.print(HORIZONTAL_LINE);
            }
            out.print(HORIZONTAL_LINE + CROSS_LINE);
        }
        out.println();

        // 输出数据行
        for (String[] datum : data) {
            out.print(VERTICAL_LINE);
            for (int j = 0; j < numColumns; j++) {
                out.print(SPACE + datum[j]);
                int paddingSize = columnWidths[j] - getDisplayWidth(datum[j]);
                for (int k = 0; k < paddingSize; k++) {
                    out.print(SPACE);
                }
                out.print(SPACE + VERTICAL_LINE + SPACE);
            }
            out.println();
        }

        // 输出底部分隔线
        out.print(CROSS_LINE);
        for (int i = 0; i < numColumns; i++) {
            out.print(HORIZONTAL_LINE);
            for (int j = 0; j < columnWidths[i]; j++) {
                out.print(HORIZONTAL_LINE);
            }
            out.print(HORIZONTAL_LINE + CROSS_LINE);
        }
        out.println();
    }

    /**
     * 获取字符串的宽度
     *
     * @param s 要处理的字符串
     * @return 字符串的宽度
     */
    private static int getDisplayWidth(String s) {
        int width = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) >= '一' && s.charAt(i) <= '龥') {
                width += 2;
            } else {
                width++;
            }
        }
        return width;
    }
}
