package app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection mkConn() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://postgres.cv426w0cc4ts.eu-central-1.rds.amazonaws.com:5432/postgres",
                "postgres",
                "mysecretpassword");
    }
}