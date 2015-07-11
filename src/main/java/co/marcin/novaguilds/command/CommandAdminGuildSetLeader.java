package co.marcin.novaguilds.command;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
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
            plugin.getMessageManager().sendNoPermissionsMessage(sender);
            return true;
        }

        if(args.length == 0) { //no leader
            plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.entername");
            return true;
        }

        String playername = args[0];

        if(!plugin.getPlayerManager().exists(playername)) { //invalid player
            plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.notexists");
            return true;
        }

        NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(playername);
        HashMap<String,String> vars = new HashMap<>();
        vars.put("PLAYERNAME",nPlayer.getName());

        if(!nPlayer.hasGuild()) { //has no guild
            plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.hasnoguild");
            return true;
        }

        NovaGuild guild = nPlayer.getGuild();
        vars.put("GUILDNAME", guild.getName());

        if(!guild.isMember(nPlayer)) { //is not member
            plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.guild.setleader.notinguild",vars);
            return true;
        }

        if(guild.getLeader().getName().equalsIgnoreCase(nPlayer.getName())) { //already leader
            plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.guild.setleader.alreadyleader",vars);
            return true;
        }

        Player oldleader = plugin.getServer().getPlayer(guild.getLeader().getName());

        guild.setLeader(nPlayer);

        if(oldleader != null) {
            plugin.tagUtils.updatePrefix(oldleader);
        }

        if(nPlayer.isOnline()) {
            plugin.tagUtils.updatePrefix(nPlayer.getPlayer());
        }


        plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.guild.setleader.success",vars);
        plugin.getMessageManager().broadcastMessage("broadcast.guild.newleader",vars);

        return true;
    }
}