package DatabaseManagement;

import java.sql.Date;

public class Book {
    private String title,author;
    private Date publicationDate;
    private int id,rating;
    public Book(int id, String title, String author) {
        this.id=id;
        this.title=title;
        this.author = author;
    }


    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }


    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", id=" + id + '}';
    }

    public String getAuthor() {
        return author;
    }
}
