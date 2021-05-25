package DatabaseManagement;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserBookTable {
    private static final Map<String, List<Integer>> userBookList=new HashMap<>();
    public UserBookTable(){
        Connection conn=DatabaseConnection.getConn();
        Statement stmt;
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT * FROM USER_BOOKS"; //adds all users from database to the list
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next( )){
                String idUser = rs.getString("id_user");
                Integer idBook = rs.getInt("id_book");
                if(userBookList.containsKey(idUser))
                    userBookList.get(idUser).add(idBook);
                else{
                    List<Integer> books=new ArrayList<>();
                    books.add(idBook);
                    userBookList.put(idUser,books);
                }

            }
            System.out.println("userBooksList: "+userBookList);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
    public static void insert(String idUser, Integer idBook) {
        Connection conn = DatabaseConnection.getConn();
        String sql = "INSERT INTO USER_BOOKS VALUES(?,?)";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, idUser);
            pstmt.setInt(2, idBook);
            pstmt.executeUpdate();
            System.out.println("User "+idUser+" has added book no."+idBook+" to his/her completed books list.");
            if(userBookList.containsKey(idUser))
                userBookList.get(idUser).add(idBook);
            else{
                List<Integer> books=new ArrayList<>();
                books.add(idBook);
                userBookList.put(idUser,books);
            }
            pstmt.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("User already has this book in USER_BOOKS database.");
        }catch (SQLException e){
            e.printStackTrace();
        }

    }
    public static void deleteBook(String id_user,int id_book){
        Connection con=DatabaseConnection.getConn();
        String sql="DELETE FROM USER_BOOKS WHERE ID_user="+id_user+" AND ID_BOOK="+id_book;
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.executeUpdate();
            for(int i=0;i<userBookList.get(id_user).size();i++)
                if(userBookList.get(id_user).get(i)==id_book){
                    userBookList.get(id_user).remove(i);
                    break;
                }

            if(userBookList.get(id_user).isEmpty())
                userBookList.remove(id_user);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public static Map<String, List<Integer>> getUserBookList() {
        return userBookList;
    }


}
