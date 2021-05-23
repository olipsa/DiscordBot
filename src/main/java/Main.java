import DatabaseManagement.DatabaseConnection;
import DatabaseManagement.ServerTable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.EnumSet;

public class Main {

    public static void main(String[] args) {

        try {
            String BOT_TOKEN = "ODQ1MjU5NjY5OTczNzYyMDU4.YKeXaQ.LtaohN1a3CIJYKe4BWEortbC4sA";
            JDA api= JDABuilder.createDefault(BOT_TOKEN)
                                .setChunkingFilter(ChunkingFilter.ALL)
                                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                                .setMemberCachePolicy(MemberCachePolicy.ALL)
                                .build();

            try {
                DatabaseConnection.getInstance();

            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
            api.addEventListener(new MyListener());

        } catch (LoginException e) {
            e.printStackTrace();
        }


    }
}
