package DatabaseManagement;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServerTable {
    private static Map<String,List<String>> userServerList=new HashMap<>();
    public UserServerTable(){
        Connection conn=DatabaseConnection.getConn();
        Statement stmt;
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT * FROM USER_SERVERS"; //adds all users from database to the list
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next( )){
                String idUser = rs.getString("id_user");
                String idServer = rs.getString("id_server");
                if(userServerList.containsKey(idUser))
                    userServerList.get(idUser).add(idServer);
                else{
                    List<String> servers=new ArrayList<>();
                    servers.add(idServer);
                    userServerList.put(idUser,servers);
                }

            }
            System.out.println("userServerList: "+userServerList);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
    public static void insert(String idUser, String idServer) {
        Connection conn = DatabaseConnection.getConn();
        String sql = "INSERT INTO USER_SERVERS VALUES(?,?)";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, idUser);
            pstmt.setString(2, idServer);
            pstmt.executeUpdate();
            System.out.println("User "+idUser+" from server "+idServer+" added to USERS table.");
            if(userServerList.containsKey(idUser))
                userServerList.get(idUser).add(idServer);
            else{
                List<String> servers=new ArrayList<>();
                servers.add(idServer);
                userServerList.put(idUser,servers);
            }
            pstmt.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("User already exists in USER_SERVERS database.");
        }catch (SQLException e){
            e.printStackTrace();
        }

    }
    public static List<User> getUsersFromGuild(String id_server){
        List<User> retrievedUsers=new ArrayList<>();
        String sql="SELECT id_user FROM user_servers WHERE id_server="+id_server; //gets users from current guild
        Connection con=DatabaseConnection.getConn();
        Statement stmt = null;
        try {
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next( )){
                String idUser = rs.getString("id_user");
                for(User user:UserTable.getUserList())
                    if(user.getId().equals(idUser))
                        retrievedUsers.add(user);
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return retrievedUsers;
    }
    public static void deleteUser(String id_user,String id_server){
        Connection con=DatabaseConnection.getConn();
        String sql="DELETE FROM USER_SERVERS WHERE ID_USER="+id_user+" AND ID_SERVER="+id_server;
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.executeUpdate();
            userServerList.get(id_user).remove(id_server);
            if(userServerList.get(id_user).isEmpty())
                userServerList.remove(id_user);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
    public static void deleteServer(String id_server){
        Connection con=DatabaseConnection.getConn();
        String sql="DELETE FROM USER_SERVERS WHERE ID_SERVER="+id_server;
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.executeUpdate();
            for(Map.Entry<String,List<String>> entry : userServerList.entrySet()){
                for(String server:entry.getValue())
                    if(server.equals(id_server)){
                        entry.getValue().remove(server);
                        break;
                    }
                if(entry.getValue().isEmpty())
                    userServerList.remove(entry.getKey());

            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
    public static Map<String, List<String>> getUserServerList() {
        return userServerList;
    }

    public static void setUserServerList(Map<String, List<String>> userServerList) {
        UserServerTable.userServerList = userServerList;
    }

}
