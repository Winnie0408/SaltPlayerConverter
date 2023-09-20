package utils;

/**
 * @description: 主动让程序暂停
 * @author: HWinZnieJ
 * @create: 2023-09-04 17:03
 **/

public class Sleep {
    /**
     * 程序暂停
     * @param miliseconds 要暂停的毫秒数
     */
    public static void start(int miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException e) {
            Logger.error(e.getMessage());
        }
    }
}
