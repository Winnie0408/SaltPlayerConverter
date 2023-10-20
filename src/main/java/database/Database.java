package database;

import utils.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @description: 数据库相关操作
 * @author: HWinZnieJ
 * @create: 2023-09-04 16:36
 **/

public class Database {

    /**
     * 获取数据库的连接
     *
     * @param dbPath 数据库文件的名称或绝对路径
     * @return 数据库连接接口
     */
    public Connection getConnection(String dbPath) {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:" + dbPath;
            conn = DriverManager.getConnection(url);
            Logger.success("成功连接SQLite数据库");
        } catch (SQLException e) {
            Logger.error("数据库连接失败！\n错误详情：" + e.getMessage());
        }
        return conn;
    }

    /**
     * 关闭到数据库的连接
     *
     * @param conn 数据库连接接口
     */
    public void closeConnection(Connection conn) {
        try {
            conn.close();
            Logger.success("成功断开SQLite数据库");
        } catch (SQLException e) {
            Logger.error("数据库断开失败！\n错误详情：" + e.getMessage());
        }
    }
}
