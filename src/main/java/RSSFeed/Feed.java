package RSSFeed;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Feed {
    public static void main(String[] args) {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_1.0");
        feed.setTitle("BOT title");
        feed.setLink("http://localhost:8088/test/xyz.txt");
        feed.setDescription("Whatever description");
        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle("Entry title");
        entry.setLink("http://localhost:8088/test/xyz.txt/entry1");

        feed.setEntries(Collections.singletonList(entry));
        SyndContent description = new SyndContentImpl();
        description.setType("text/html");
        description.setValue("BOT");

        entry.setDescription(description);
        List<SyndCategory> categories = new ArrayList<>();
        SyndCategory category = new SyndCategoryImpl();
        category.setName("Sophisticated category");
        categories.add(category);

        entry.setCategories(categories);
        File rss=new File("C:\\xampp\\htdocs\\test","xyz.txt");
        if (!rss.exists()) {
            try {
                rss.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(rss.getAbsoluteFile()));
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

    }
}
