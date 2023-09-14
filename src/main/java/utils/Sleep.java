package utils;

/**
 * @description: 主动让程序暂停
 * @author: HWinZnieJ
 * @create: 2023-09-04 17:03
 **/

public class Sleep {
    public static void start(int miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException e) {
            Logger.error(e.getMessage());
        }
    }
}
