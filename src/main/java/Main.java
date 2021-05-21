import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
            String BOT_TOKEN = "ODQ1MjU5NjY5OTczNzYyMDU4.YKeXaQ.LtaohN1a3CIJYKe4BWEortbC4sA";
            JDA api= JDABuilder.createDefault(BOT_TOKEN).build();
            api.addEventListener(new MyListener());
        } catch (LoginException e) {
            e.printStackTrace();
        }


    }
}
