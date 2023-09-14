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
            Logger.info("成功连接SQLite数据库");
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
        return conn;
    }

    public void closeConnection(Connection conn) {
        try {
            conn.close();
            Logger.info("成功连断开SQLite数据库");
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
    }
}
