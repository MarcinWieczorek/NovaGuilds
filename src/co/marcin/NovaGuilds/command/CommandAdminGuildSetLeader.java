package co.marcin.NovaGuilds.command;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandAdminGuildSetLeader implements CommandExecutor {
    private final NovaGuilds plugin;

    public CommandAdminGuildSetLeader(NovaGuilds pl) {
        plugin = pl;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("novaguilds.admin.guild.leader")) {
            plugin.sendMessagesMsg(sender,"chat.nopermissions");
            return true;
        }

        if(args.length == 0) { //no leader
            plugin.sendMessagesMsg(sender,"chat.player.entername");
            return true;
        }

        String playername = args[0];

        HashMap<String,String> vars = new HashMap<>();

        if(!plugin.getPlayerManager().exists(playername)) { //invalid player
            plugin.sendMessagesMsg(sender,"chat.player.notexists");
            return true;
        }

        NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(playername);
        vars.put("PLAYERNAME",nPlayer.getName());

        if(!nPlayer.hasGuild()) { //has no guild
            plugin.sendMessagesMsg(sender,"chat.player.hasnoguild");
            return true;
        }

        NovaGuild guild = plugin.getGuildManager().getGuildByPlayer(nPlayer);
        vars.put("GUILDNAME", guild.getName());

        if(!guild.isMember(nPlayer)) { //is not member
            plugin.sendMessagesMsg(sender,"chat.admin.guild.setleader.notinguild",vars);
            return true;
        }

        if(guild.getLeaderName().equalsIgnoreCase(nPlayer.getName())) { //already leader
            plugin.sendMessagesMsg(sender,"chat.admin.guild.setleader.alreadyleader",vars);
            return true;
        }

        Player oldleader = plugin.getServer().getPlayer(guild.getLeaderName());

        guild.setLeaderName(nPlayer.getName());
        nPlayer.setLeader(true);
        plugin.getPlayerManager().getPlayerByPlayer(oldleader).setLeader(false);

        if(oldleader != null) {
            plugin.tagUtils.updatePrefix(oldleader);
        }

        if(nPlayer.isOnline()) {
            plugin.tagUtils.updatePrefix(nPlayer.getPlayer());
        }


        plugin.sendMessagesMsg(sender,"chat.admin.guild.setleader.success",vars);
        plugin.broadcastMessage("broadcast.guild.newleader",vars);

        return true;
    }
}