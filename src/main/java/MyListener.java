import DatabaseManagement.*;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyListener extends ListenerAdapter
{
    private List<Book>latestSearch=new ArrayList<>();

    @Override
    public void onGuildJoin(GuildJoinEvent event){
        Guild guild=event.getGuild();
        System.out.println("Bot joined server "+guild.getName());
        //Add joined server to database
        ServerTable.insert(guild.getId(),guild.getName());

        //Add members from joined server to database
        List<Member> membersGuild=guild.getMembers();
        List<String>usersList=new ArrayList<>();
        for (Member member:membersGuild) {
            if(!member.getUser().isBot()){
                User currentUser=member.getUser();
                usersList.add(currentUser.getName());
                UserTable.insert(currentUser.getId(),currentUser.getName());
                UserServerTable.insert(currentUser.getId(),guild.getId());
            }
        }
        MessageChannel channel = event.getGuild().getDefaultChannel();
        assert channel != null;
        channel.sendMessage("Hello! I will be your virtual library where you can keep track of your currently reading books, as well as the finished ones :smiling_face_with_3_hearts:").queue();
        System.out.println("Members in "+guild.getName()+" server:\n"+usersList);
        System.out.println();
    }
    @Override
    public void onGuildReady(GuildReadyEvent event)
    {
        Guild guild=event.getGuild();
        System.out.println("Members in "+guild.getName()+" server:\n"+UserServerTable.getUsersFromGuild(guild.getId()));
    }
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event){
        if(event.getUser().isBot()) return;
        UserTable.insert(event.getUser().getId(),event.getUser().getName());
        UserServerTable.insert(event.getUser().getId(),event.getGuild().getId());
        MessageChannel channel = event.getGuild().getDefaultChannel();
        assert channel != null;
        System.out.println("User "+event.getUser().getName()+" joined "+event.getGuild().getName()+" server.");
        channel.sendMessage("Welcome to our Reading group, "+event.getUser().getName()+" :books: :heartpulse:").queue();
        System.out.println("Members in "+event.getGuild().getName()+" server:\n"+UserServerTable.getUsersFromGuild(event.getGuild().getId()));
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        // We don't want to respond to other bot accounts, including ourself
        Message message = event.getMessage();
        String content = message.getContentRaw();
        content=content.toLowerCase();
        MessageChannel channel = event.getChannel();
        if(content.startsWith("->"))
        {
            int endCommand=content.indexOf(" ");
            String command;
            if(endCommand!=-1)
                command=content.substring(2,endCommand);
            else
                command=content.substring(2);
            switch(Objects.requireNonNull(command)){
                case "help": {
                    channel.sendMessage("Glad to help you! Please find below a list of all available commands:\n" +
                            " ->search : Retrieves all books from the existing database :book:\n" +
                            " ->add [bookINumber] : adds the book that is on the specified index in the searched list to your list of completed books.\n" +
                            "For instance, if you send '->add 5' the :five:th book on the list will be marked as complete :ballot_box_with_check:\n"+
                            ":exclamation:WARNING: You cannot add more than one book at a time\n" +
                            " ->done : lists all the books that you finished\n" +
                            " ->done [person] : lists all the books that person finished (if the person is in the current server) :technologist:\n" +
                            " ->remove [bookNumber] : removes the book that is on the specified position in your list from finished reading")
                            .queue();
                    break;
                }
                case "search": {
                    latestSearch=new ArrayList<>();
                    channel.sendMessage("Loading books...").queue();
                    LoadBooks();
                    channel.sendMessage(printAvailableBooks()).queue();
                    break;
                }
                case "add":{
                    //check if a search has been made first
                    if(latestSearch.isEmpty()){
                        channel.sendMessage("Please search for some books first.").queue();
                        break;
                    }

                    //check if command not empty
                    if(command.equals(content.substring(2))){
                        channel.sendMessage("Invalid book number. Please add the book number that you want to add.").queue();
                        break;
                    }
                    String book=content.substring(endCommand+1);
                    int idBook;
                    try{
                        idBook=Integer.parseInt(book);
                    }catch(NumberFormatException e){
                        channel.sendMessage("Invalid book number.").queue();
                        break;
                    }

                    //check if book exists
                    if(idBook<1||idBook>latestSearch.size()){
                        channel.sendMessage("Invalid book number. Please try a number in [1-"+latestSearch.size()+"] interval.").queue();
                        break;
                    }

                    //check if user already has the book in his list
                    String id_user=event.getAuthor().getId();
                    Book chosenBook=getBookById(idBook,latestSearch);
                    if(chosenBook.getId()!=-1)
                            idBook = BookTable.getBookId(chosenBook.getTitle(),chosenBook.getAuthor());

                    if(UserBookTable.getUserBookList().containsKey(id_user)&&UserBookTable.getUserBookList().get(id_user).contains(idBook)){
                        channel.sendMessage("This book is already in your finished reading list.").queue();
                        break;
                    }

                    //optional:ask before adding the book
                    channel.sendMessage("Are you sure you want to add this book to your finished reading list?").queue();
                    event.getJDA().addEventListener(new MyMessageListener(event.getChannel(),event.getAuthor(),idBook));

                    break;

                }
                case "done":{
                    //->finished_reading command lists books of the current user
                    String currentUserId=event.getAuthor().getId();
                    if(command.equals(content.substring(2))){
                        if(!UserBookTable.getUserBookList().containsKey(currentUserId)){
                            channel.sendMessage("You don't have any books finished yet. You can add books with search and add commands, please check our documentation for more info .").queue();
                            break;
                        }
                        channel.sendMessage("Your finished books are:").queue();
                        getFinishedBooks(channel, currentUserId);

                    }
                    else {
                        //->finished_reading with arguments lists books of specified user
                        String personName=content.substring(endCommand+1);
                        String personId=null;
                        //get personId
                        for(DatabaseManagement.User user:UserTable.getUserList())
                            if(user.getUsername().toLowerCase().equals(personName)){
                                personId=user.getId();
                                break;
                            }
                        if(personId==null){
                            channel.sendMessage("Username '"+personName+"' doesn't exist").queue();
                            break;
                        }

                        //check if personName exists in this server
                        String serverId=event.getGuild().getId();
                        System.out.println(UserServerTable.getUserServerList());
                        if(!UserServerTable.getUserServerList().containsKey(personId) || !UserServerTable.getUserServerList().get(personId).contains(serverId)){
                            channel.sendMessage("Username '"+personName+"' doesn't exist in this server").queue();
                            break;
                        }
                        //check if the person has books in her/his list
                        if(!UserBookTable.getUserBookList().containsKey(personId)){
                            channel.sendMessage(personName+" didn't finish any books yet :laughing: :satisfied:").queue();
                            break;
                        }
                        channel.sendMessage(personName+"'s finished books are:").queue();
                        getFinishedBooks(channel, personId);

                    }
                    break;


                }
                case "remove":{

                    //check if command not empty
                    if(command.equals(content.substring(2))){
                        channel.sendMessage("Please add the book number that you want to remove.").queue();
                        break;
                    }
                    String book=content.substring(endCommand+1);
                    int idBook;
                    try{
                        idBook=Integer.parseInt(book);
                    }catch(NumberFormatException e){
                        channel.sendMessage("Invalid book number.").queue();
                        break;
                    }

                    //check if book exists
                    String personId=event.getAuthor().getId();
                    if(!UserBookTable.getUserBookList().containsKey(personId)){
                        channel.sendMessage("You have no books in your list to remove.").queue();
                        break;
                    }
                    List<Integer> currentUserBooks=UserBookTable.getUserBookList().get(personId);

                    if(idBook<1||idBook>currentUserBooks.size()){
                        channel.sendMessage("Invalid book number. Please try a number in [1-"+currentUserBooks.size()+"] interval, because your current list of books is:.").queue();
                        getFinishedBooks(channel,personId);
                        break;
                    }

                    //remove book from list&database
                    int idChosenBook=currentUserBooks.get(idBook-1);
                    UserBookTable.deleteBook(personId,idChosenBook);
                    channel.sendMessage("Book \""+getBookById(idChosenBook,BookTable.getBookList()).getTitle()+"\" was successfully removed from your list").queue();
                    break;

                }


                /*case "stop":
                    channel.sendMessage("Warning, you are about to stop the Discord Bot. This action cannot be undone. Are you sure you want to continue? React with :thumbsup: or :thumbsdown:").queue();
                    //event.getJDA().addEventListener(new MyEmoteListener());
                    break;*/
                default:
                    channel.sendMessage("Command not implemented").queue();

            }
        }

    }

    private void getFinishedBooks(MessageChannel channel, String personId) {
        int iterator=1;
        for(Integer bookId: UserBookTable.getUserBookList().get(personId)){
            Book book=getBookById(bookId, BookTable.getBookList());
            StringBuilder booksFormat=new StringBuilder();
            booksFormat.append(iterator).append(". ").append(book.getTitle()).append("   by   ").append(book.getAuthor()).append("\n");
            channel.sendMessage(booksFormat).queue();
            iterator++;
        }
    }

    public StringBuilder printAvailableBooks(){
        System.out.println("Booklist is:");
        System.out.println(latestSearch);
        StringBuilder booksFormat=new StringBuilder();
        for(Book book:latestSearch)
            booksFormat.append(book.getId()).append(". ").append(book.getTitle()).append("   by   ").append(book.getAuthor()).append("\n");


        return booksFormat;
    }
    public String convertRSS(String url){
        URL feedSource = null;
        try {
            feedSource = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = null;
        try {
            assert feedSource != null;
            feed = input.build(new XmlReader(feedSource));
        }catch (FeedException | IOException e) {
            e.printStackTrace();
        }
        assert feed != null;
        return feed.toString();
    }
    public String formatFile(String feed){
        //first var
       StringBuilder file=new StringBuilder();
        for(int i=0;i<feed.length();i++){
            if(feed.substring(i).startsWith("by ")&&feed.charAt(i+3)!='['){
                int end=feed.substring(i).indexOf('<');
                String bookNew="by [["+feed.substring(i+3,i+end)+"]]";
                file.append(bookNew);
                i=i+bookNew.length()-4;
            }
            else
                file.append(feed.charAt(i));
        }
        return file.toString();
    }
    public void getBookFromFeed(String feed,List<String>bookList,String startsWith){
        for(int i=0;i<feed.length()-10;i++){
            String substring=feed.substring(i);
            if(substring.startsWith(startsWith))
                for(int j=5;j<feed.length()-20;j++)
                {
                    String substring2=substring.substring(j);
                    if(substring2.startsWith("]]")){
                        String book= feed.substring(i+startsWith.length(),i+j);
                        if(book.contains("|"))
                            book=book.substring(book.indexOf("|")+1);
                        if(bookList.isEmpty()||!bookList.get(bookList.size()-1).equals(book))
                            bookList.add(book);
                        break;
                    }
                    if(feed.charAt(i+j)=='<')
                        break;
                }
        }
    }
    public void LoadBooks(){
        List<String>bookList=new ArrayList<>();
        String feed=formatFile(convertRSS("https://en.wikipedia.org/w/index.php?title=List_of_fantasy_novels_(A%E2%80%93H)&action=history&feed=rss"));
        getBookFromFeed(feed,bookList,"*''[[");
        List<String>authorList=new ArrayList<>();
        getBookFromFeed(feed,authorList,"by [[");
        int index=1;
        for(int i=0;i<bookList.size();i++){
            boolean bookAdded=true;
            String title=bookList.get(i);
            String author=authorList.get(i);
            Book foundBook=new Book(index,title,author);
            if(latestSearch.isEmpty()){
                latestSearch.add(foundBook);
                index++;
            }


            else{
                for(Book b:latestSearch)
                    if(b.getTitle().equals(title)&&b.getAuthor().equals(author)){
                        bookAdded=false;
                        break;
                    }
                if(bookAdded){
                    latestSearch.add(foundBook);
                    index++;
                }

            }



            //if the same book already exists in the database, we don't add it again
            if(BookTable.getBookId(title,author)!=-1)
                continue;

            BookTable.insert(title,author);
            System.out.println("New book inserted");
        }
        System.out.println(latestSearch);

    }
    //retrieves the book from the latest search by id in the desired List of Books
    public static Book getBookById(int id_book,List<Book>bookList){
        for(Book book:bookList) {
            if (book.getId()== id_book){
                return new Book(id_book,book.getTitle(),book.getAuthor());
            }
        }
        return new Book(-1,null,null);
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event){
        if(event.getUser().isBot()) return;
        UserServerTable.deleteUser(event.getUser().getId(),event.getGuild().getId());
        UserTable.delete(event.getUser().getId());
        System.out.println("User "+event.getUser().getName()+" left "+event.getGuild().getName()+" server.");
        MessageChannel channel = event.getGuild().getDefaultChannel();
        assert channel != null;
        channel.sendMessage(event.getUser().getName()+" left our Reading group :cry:").queue();
        System.out.println("Members in "+event.getGuild().getName()+" server:\n"+UserServerTable.getUsersFromGuild(event.getGuild().getId()));
    }
    @Override
    public void	onGuildLeave(GuildLeaveEvent event){
        Guild guild= event.getGuild();
        UserServerTable.deleteServer(guild.getId());
        ServerTable.delete(guild.getId());
        System.out.println("Bot left "+guild.getName()+" server.");
        System.out.println("Bot is in the following servers now:");
        System.out.println(ServerTable.getServerList());
    }

}