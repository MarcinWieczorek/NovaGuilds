package co.marcin.NovaGuilds.command;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAdminGuildSetLeader implements CommandExecutor {
    public final NovaGuilds plugin;

    public CommandAdminGuildSetLeader(NovaGuilds pl) {
        plugin = pl;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            //TODO messages
            return true;
        }

        String playername = args[0];

        if(!plugin.getPlayerManager().exists(playername)) {

            return true;
        }

        NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(playername);

        if(!nPlayer.hasGuild()) {

            return true;
        }

        NovaGuild guild = plugin.getGuildManager().getGuildByPlayer(nPlayer);

        if(!guild.isMember(nPlayer)) {

            return true;
        }

        if(guild.getLeaderName().equalsIgnoreCase(nPlayer.getName())) {

            return true;
        }

        Player oldleader = plugin.getServer().getPlayer(guild.getLeaderName());

        guild.setLeaderName(nPlayer.getName());

        if(oldleader != null) {
            plugin.tagUtils.updateTagPlayerToAll(oldleader);
        }

        plugin.getGuildManager().saveGuildLocal(guild);

        if(nPlayer.isOnline()) {
            plugin.tagUtils.updateTagPlayerToAll(nPlayer.getPlayer());
        }

        return true;
    }
}