import DatabaseManagement.Book;
import DatabaseManagement.BookTable;
import DatabaseManagement.UserBookTable;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MyMessageListener extends ListenerAdapter {
    int currentBookId;
    MessageChannel channel;
    User author;
    public MyMessageListener(MessageChannel channel, User author, int id_book){
        this.channel=channel;
        this.author=author;
        this.currentBookId=id_book;
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if(event.getAuthor() !=author || event.getChannel()!=channel)  return;

        //Sender is the one who decided to add the book
        String content=event.getMessage().getContentRaw();
        content=content.toLowerCase();
        int idBook=currentBookId;
        MessageChannel channel=event.getChannel();
        if(content.startsWith("y")){
            if(idBook!=-1){
                UserBookTable.insert(event.getAuthor().getId(),idBook);
                Book finishedBook=MyListener.getBookById(idBook, BookTable.getBookList());
                channel.sendMessage("Congratulations for reading \""+ finishedBook.getTitle()+"\" by "+finishedBook.getAuthor()+" :tada: It is now added to your finished reading list.").queue();
                System.out.println(event.getAuthor().getName()+" finished reading the following books: "+UserBookTable.getUserBookList().get(event.getAuthor().getId()));
            }
            else
                System.out.println("Book with id "+idBook+" not found");
        }
        else
        {
            channel.sendMessage("Book \""+MyListener.getBookById(idBook, BookTable.getBookList()).getTitle()+"\" not added to your finished reading list.").queue();
        }
        event.getJDA().removeEventListener(this);
    }




}
