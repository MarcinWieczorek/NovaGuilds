package co.marcin.novaguilds.command;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
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
            Message.CHAT_NOPERMISSIONS.send(sender);
            return true;
        }

        if(args.length == 0) { //no leader
            Message.CHAT_PLAYER_ENTERNAME.send(sender);
            return true;
        }

        String playername = args[0];

        NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(playername);

        if(nPlayer == null) { //invalid player
            Message.CHAT_PLAYER_NOTEXISTS.send(sender);
            return true;
        }

        HashMap<String,String> vars = new HashMap<>();
        vars.put("PLAYERNAME",nPlayer.getName());

        if(!nPlayer.hasGuild()) { //has no guild
            Message.CHAT_PLAYER_HASNOGUILD.send(sender);
            return true;
        }

        NovaGuild guild = nPlayer.getGuild();
        vars.put("GUILDNAME", guild.getName());

        if(!guild.isMember(nPlayer)) { //is not member
            plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.guild.set.leader.notinguild",vars);
            return true;
        }

        if(guild.getLeader().getName().equalsIgnoreCase(nPlayer.getName())) { //already leader
            plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.guild.set.leader.alreadyleader",vars);
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


        plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.guild.set.leader.success",vars);
        plugin.getMessageManager().broadcastMessage("broadcast.guild.newleader",vars);

        return true;
    }
}