package co.marcin.NovaGuilds.Commands;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAdminGuildSetLeader implements CommandExecutor {
    public NovaGuilds plugin;

    public CommandAdminGuildSetLeader(NovaGuilds pl) {
        plugin = pl;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage("1");
            return true;
        }

        String playername = args[0];

        if(!plugin.getPlayerManager().exists(playername)) {
            sender.sendMessage("2");
            return true;
        }

        NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(playername);

        if(nPlayer == null) sender.sendMessage("7");

        if(!nPlayer.hasGuild()) {
            sender.sendMessage("6");
            return true;
        }

        NovaGuild guild = plugin.getGuildManager().getGuildByPlayer(nPlayer);

        if(!guild.isMember(nPlayer)) {
            sender.sendMessage("3");
            return true;
        }

        if(guild.getLeaderName().equalsIgnoreCase(nPlayer.getName())) {
            sender.sendMessage("4");
            return true;
        }

        Player oldleader = plugin.getServer().getPlayer(guild.getLeaderName());

        guild.setLeaderName(nPlayer.getName());

        if(oldleader != null) {
            plugin.updateTagPlayerToAll(oldleader);
        }

        plugin.getGuildManager().saveGuildLocal(guild);

        if(nPlayer.isOnline()) {
            plugin.updateTagPlayerToAll(nPlayer.getPlayer());
        }
        sender.sendMessage("5");
        return true;
    }
}