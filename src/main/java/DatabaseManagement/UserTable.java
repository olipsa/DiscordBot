package DatabaseManagement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserTable {
    private static List<User> userList=new ArrayList<>();

    public static List<User> getUserList() {
        return userList;
    }

    public static void setUserList(List<User> userList) {
        UserTable.userList = userList;
    }

    public UserTable(){
        Connection conn=DatabaseConnection.getConn();
        Statement stmt = null;
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT * FROM users"; //adds all users from database to the list
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next( )){
                String id = rs.getString("id");
                String name = rs.getString("name");

                User newUser= new User(id,name);
                userList.add(newUser);

            }
            System.out.println("UserList: "+userList);


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public static void insert(String id, String username) {

        Connection conn = DatabaseConnection.getConn();
        String sql = "INSERT INTO USERS VALUES(?,?)";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            User user=new User(id,username);
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getUsername());
            pstmt.executeUpdate();
            System.out.println("User "+username+" added to USERS table.");
            userList.add(user);
            pstmt.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("User already exists in USERS database.");
        }catch (SQLException e){
            e.printStackTrace();
        }

    }
    public static void delete(String id_user){
        Connection con=DatabaseConnection.getConn();
        if(UserServerTable.getUserServerList().containsKey(id_user)) return;
        String sql="DELETE FROM USERS WHERE ID="+id_user;
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.executeUpdate();
            for(User userRemoved:userList)
                if(userRemoved.getId().equals(id_user))
                {
                    userList.remove(userRemoved);
                    break;
                }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

}
