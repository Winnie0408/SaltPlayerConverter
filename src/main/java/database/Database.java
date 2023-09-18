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

    public Connection getConnection(String dbName) {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:SQLite/" + dbName;
            conn = DriverManager.getConnection(url);
            Logger.success("成功连接SQLite数据库");
        } catch (SQLException e) {
            Logger.error("数据库连接失败！\n错误详情：" + e.getMessage());
        }
        return conn;
    }

    public void closeConnection(Connection conn) {
        try {
            conn.close();
            Logger.success("成功断开SQLite数据库");
        } catch (SQLException e) {
            Logger.error("数据库断开失败！\n错误详情：" + e.getMessage());
        }
    }
}
