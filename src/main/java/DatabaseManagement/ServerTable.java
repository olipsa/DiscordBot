package DatabaseManagement;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServerTable {
    static List<Server> serverList=new ArrayList<>();
    public ServerTable(){

    }
    public static void insert(String id, String name){
        Server server=new Server(id,name);
        serverList.add(server);
        Connection conn = DatabaseConnection.getConn();
            String sql = "INSERT INTO SERVERS VALUES(?,?)";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, server.getId());
            pstmt.setString(2, server.getName());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException throwables) {
            System.out.println("Server already exists in database.");
        }

    }
}
