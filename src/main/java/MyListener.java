import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MyListener extends ListenerAdapter
{
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
                System.out.println(feed1);
            } catch (FeedException | IOException e) {
                e.printStackTrace();
            }
            switch(content.substring(2)){
                case "help":
                    System.out.println(content.substring(2));
                    assert feed1!= null;
                    System.out.println("feed is: "+feed1);
                    channel.sendMessage(feed1.getDescription()).queue();
                    break;
                default:
                    channel.sendMessage("Command not implemented").queue();

            }
        }

    }
}