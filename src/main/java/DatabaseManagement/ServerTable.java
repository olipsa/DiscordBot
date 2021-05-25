package DatabaseManagement;



import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServerTable {
    private static List<Server> serverList=new ArrayList<>();
    public ServerTable(){
        Connection conn=DatabaseConnection.getConn();
        Statement stmt = null;
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT * FROM SERVERS"; //adds all users from database to the list
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next( )){
                String id = rs.getString("id");
                String name = rs.getString("name");
                Server newServer= new Server(id,name);
                serverList.add(newServer);
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
    public static void insert(String id, String name){

        Connection conn = DatabaseConnection.getConn();
            String sql = "INSERT INTO SERVERS VALUES(?,?)";

        try {
            Server server=new Server(id,name);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, server.getId());
            pstmt.setString(2, server.getName());
            pstmt.executeUpdate();
            System.out.println("Server "+name+" added to SERVERS table.");
            serverList.add(server);
            pstmt.close();
        } catch (SQLException throwables) {
            System.out.println("Server already exists in database.");
        }

    }
    public static void delete(String id_server){
        Connection con=DatabaseConnection.getConn();
        String sql="DELETE FROM SERVERS WHERE ID="+id_server;
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.executeUpdate();
            for(Server serverRemoved:serverList)
                if(serverRemoved.getId().equals(id_server))
                {
                    serverList.remove(serverRemoved);
                    break;
                }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public static List<Server> getServerList() {
        return serverList;
    }

    public static void setServerList(List<Server> serverList) {
        ServerTable.serverList = serverList;
    }
}
