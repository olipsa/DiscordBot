import DatabaseManagement.DatabaseConnection;
import DatabaseManagement.ServerTable;
import DatabaseManagement.UserServerTable;
import DatabaseManagement.UserTable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;


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

            } catch (SQLException | ClassNotFoundException throwable) {
                throwable.printStackTrace();
            }
            new ServerTable();
            new UserTable();
            new UserServerTable();
            System.out.println(ServerTable.getServerList());
            System.out.println(UserTable.getUserList());
            System.out.println(UserServerTable.getUserServerList());
            api.addEventListener(new MyListener());

        } catch (LoginException e) {
            e.printStackTrace();
        }


    }
}
