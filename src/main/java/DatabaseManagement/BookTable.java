package DatabaseManagement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookTable {
    private static final List<Book> bookList=new ArrayList<>();
    public BookTable(){
        Connection conn=DatabaseConnection.getConn();
        Statement stmt;
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT * FROM books"; //adds all users from database to the list
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next( )){

                int id=rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                Book book=new Book(id,title, author);
                bookList.add(book);
            }
            System.out.println("BookList: "+bookList);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
    public static void insert(String title, String author) {
        Connection conn = DatabaseConnection.getConn();
        String sql = "SELECT seq_book.nextval FROM DUAL";
        PreparedStatement ps;
        int idBook=0;
        try {
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                idBook = rs.getInt(1);

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }


        String sql2 = "INSERT INTO BOOKS(ID,TITLE,AUTHOR) VALUES(?,?,?)";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql2);
            pstmt.setInt(1,idBook);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.executeUpdate();
            System.out.println(idBook+". Book "+title+" by "+author+" added to BOOKS table.");

            Book book=new Book(idBook,title, author);
            bookList.add(book);
            pstmt.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Book already exists in BOOKS database.");
        }catch (SQLException e){
            e.printStackTrace();
        }

    }
    public static int getBookId(String title,String author){
        Connection conn=DatabaseConnection.getConn();
        String  newTitle,newAuthor;
        if(title.contains("'")){
            newTitle=addQuotation(title);
        }
        else
            newTitle=title;

        if(author.contains("'")){
            newAuthor=addQuotation(author);
        }
        else
            newAuthor=author;

        String sql="SELECT ID FROM BOOKS WHERE TITLE='"+newTitle+"' AND AUTHOR='"+newAuthor+"'";
        Statement stmt;
        try{
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next( )) return rs.getInt("id");
        }catch(SQLException e){
            e.printStackTrace();
        }

        return -1;
    }
    private static String addQuotation(String initialString){
        StringBuilder newString=new StringBuilder();
        for(int i=0;i<initialString.length();i++)
            if(initialString.charAt(i)=='\'')
            {
                newString.append("''");
            }
            else
                newString.append(initialString.charAt(i));
            return new String(newString);

    }

    /*public static int getBookTitle(int idBook){
        Connection conn=DatabaseConnection.getConn();
        String sql="SELECT TITLE FROM BOOKS WHERE TITLE='"+title+"' AND AUTHOR='"+author+"'";
        Statement stmt=null;
        try{
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next( )) return rs.getInt("id");
        }catch(SQLException e){
            e.printStackTrace();
        }
    }*/
    public static List<Book> getBookList() {
        return bookList;
    }

}
