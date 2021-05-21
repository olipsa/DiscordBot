import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Feed {
    public static void main(String[] args) {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_1.0");
        feed.setTitle("Test title");
        feed.setLink("http://www.somelinkOndina.com");
        feed.setDescription("Basic description");
        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle("Entry title");
        entry.setLink("http://www.somelinkOndina.com/entry1");

        feed.setEntries(Collections.singletonList(entry));
        SyndContent description = new SyndContentImpl();
        description.setType("text/html");
        description.setValue("First entry");

        entry.setDescription(description);
        List<SyndCategory> categories = new ArrayList<>();
        SyndCategory category = new SyndCategoryImpl();
        category.setName("Sophisticated category");
        categories.add(category);

        entry.setCategories(categories);
        Writer writer = null;
        try {
            writer = new FileWriter("xyz.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        SyndFeedOutput syndFeedOutput = new SyndFeedOutput();
        try {
            syndFeedOutput.output(feed, writer);
        } catch (IOException | FeedException e) {
            e.printStackTrace();
        }
        try {
            assert writer != null;
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        URL feedSource = null;
        try {
            feedSource = new URL("https://blogs.oracle.com/java/rss");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        SyndFeedInput input = new SyndFeedInput();
        try {
            assert feedSource != null;
            SyndFeed feed1 = input.build(new XmlReader(feedSource));
        } catch (FeedException | IOException e) {
            e.printStackTrace();
        }
    }
}
