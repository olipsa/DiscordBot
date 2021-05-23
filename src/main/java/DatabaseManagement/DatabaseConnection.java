package DatabaseManagement;

import java.sql.*;

public class DatabaseConnection {
    private static DatabaseConnection single_instance = null;
    private static Connection conn=null;
    private DatabaseConnection()
    {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        try{
            conn = DriverManager.getConnection(
                    url, "STUDENT", "STUDENT");
            if (conn != null) {
                System.out.println("Connected to the database!");
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            System.err.println("Cannot connect to DB: " + e);
        }
    }


    public static DatabaseConnection getInstance() throws SQLException, ClassNotFoundException {
        if (single_instance == null)
            single_instance = new DatabaseConnection();

        return single_instance;
    }

    public static Connection getConn() {
        return conn;
    }
    public static void closeConn(){
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            System.out.println("Connection closed.");
        }
    }
}