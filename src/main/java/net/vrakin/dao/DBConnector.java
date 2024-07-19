package net.vrakin.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class DBConnector {

    private String url;
    private String user;
    private String password;

    private Connection conn;

    public DBConnector() {
        InputStream in = null;
        try {
            in = DBConnector.class.getClassLoader().getResourceAsStream("application.properties");
            Properties props = new Properties();
            props.load(in);
            this.url = props.getProperty("db.url");
            this.user = props.getProperty("db.user");
            this.password = props.getProperty("db.password");
        }catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if(conn == null || conn.isClosed()) {
            this.conn = DriverManager.getConnection(url, user, password);
            log.info("Opened connection successful");
        }

        return this.conn;
    }

    public void closeConnection(){
        try {
            if (this.conn != null) {
                this.conn.close();
            }
            log.info("Connection closed successful");
        } catch (SQLException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }


}
