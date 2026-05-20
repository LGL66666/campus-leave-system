package com.campus.leave.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBUtil {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/campus_leave"
            + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "Qq18124847385";

    private DBUtil() {
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getProperty("leave.db.url", DEFAULT_URL);
        String user = System.getProperty("leave.db.user", DEFAULT_USER);
        String password = System.getProperty("leave.db.password", DEFAULT_PASSWORD);
        return DriverManager.getConnection(url, user, password);
    }
}
