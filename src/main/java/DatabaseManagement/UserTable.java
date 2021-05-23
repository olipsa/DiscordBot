package DatabaseManagement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserTable {
    private List<User> userList=new ArrayList<>();
    public UserTable(String id){
        Connection conn=DatabaseConnection.getConn();
        String sql="CREATE TABLE SERVER_"+id+"(ID integer NOT NULL," +
                "NAME VARCHAR2(100) NOT NULL,"+
                "PRIMARY KEY(ID))";

        Statement stmt=null;
        try {
            stmt=conn.createStatement();
            stmt.executeUpdate(sql);

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }finally {
            try {
                assert stmt != null;
                stmt.close();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
    }


}
