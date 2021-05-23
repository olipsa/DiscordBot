import DatabaseManagement.DatabaseConnection;
import DatabaseManagement.ServerTable;
import DatabaseManagement.UserTable;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyListener extends ListenerAdapter
{
    @Override
    public void onGuildReady(GuildReadyEvent event)
    {

        Guild guild=event.getGuild();
        ServerTable.insert(guild.getId(),guild.getName());
        System.out.println("Connected to server "+guild);
        List<Member> membersGuild=guild.getMembers();
        List<User>usersList=new ArrayList<>();
        for (Member member:membersGuild) {
            if(!member.getUser().isBot()){
                usersList.add(member.getUser());
            }
        }

        System.out.println("Members in this server:\n"+usersList);
        System.out.println();
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        // We don't want to respond to other bot accounts, including ourself
        Message message = event.getMessage();
        String content = message.getContentRaw();
        MessageChannel channel = event.getChannel();
        if(content.startsWith("->"))
        {
            URL feedSource = null;
            try {
                feedSource = new URL("http://localhost:8088/test/xyz.txt");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed1=null;
            try {
                assert feedSource != null;
                feed1 = input.build(new XmlReader(feedSource));
            } catch (FeedException | IOException e) {
                e.printStackTrace();
            }
            assert feed1!= null;
            switch(content.substring(2)){
                case "help":
                    channel.sendMessage(feed1.getDescription()).queue();
                    break;
                case "stop":
                    channel.sendMessage("Warning, you are about to stop the Discord Bot. This action cannot be undone. Are you sure you want to continue? React with :thumbsup: or :thumbsdown:").queue();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    DatabaseConnection.closeConn();
                    System.exit(0);
                default:
                    channel.sendMessage("Command not implemented").queue();

            }
        }

    }
    @Override
    public void onEmoteAdded(EmoteAddedEvent event){

    }
}