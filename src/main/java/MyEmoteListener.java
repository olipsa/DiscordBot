import DatabaseManagement.DatabaseConnection;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MyEmoteListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event){
        MessageChannel channel=event.getGuild().getDefaultChannel();
        System.out.println(event.getReactionEmote().getEmoji());
        if(event.getReactionEmote().getEmoji().equals(":thumbsup:" )){
            System.out.println("thumbsup");
            assert channel != null;
            channel.sendMessage("Oops, someone stopped me. See you when my admin boots me.").queue();
            DatabaseConnection.closeConn();
            System.exit(0);
        }
        else{
            System.out.println("no thumbsup");
        }

    }
}
