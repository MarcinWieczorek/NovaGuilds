package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandAdminGuildSetLeader implements Executor {
    private final Commands command;

    public CommandAdminGuildSetLeader(Commands command) {
        this.command = command;
        plugin.getCommandManager().registerExecutor(command, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!command.hasPermission(sender)) {
            Message.CHAT_NOPERMISSIONS.send(sender);
            return;
        }

        if(!command.allowedSender(sender)) {
            Message.CHAT_CMDFROMCONSOLE.send(sender);
            return;
        }

        if(args.length == 0) { //no leader
            Message.CHAT_PLAYER_ENTERNAME.send(sender);
            return;
        }

        String playername = args[0];

        NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(playername);

        if(nPlayer == null) { //invalid player
            Message.CHAT_PLAYER_NOTEXISTS.send(sender);
            return;
        }

        HashMap<String,String> vars = new HashMap<>();
        vars.put("PLAYERNAME",nPlayer.getName());

        if(!nPlayer.hasGuild()) { //has no guild
            Message.CHAT_PLAYER_HASNOGUILD.send(sender);
            return;
        }

        NovaGuild guild = nPlayer.getGuild();
        vars.put("GUILDNAME", guild.getName());

        if(!guild.isMember(nPlayer)) { //is not member
            Message.CHAT_ADMIN_GUILD_SET_LEADER_NOTINGUILD.vars(vars).send(sender);
            return;
        }

        if(guild.getLeader().getName().equalsIgnoreCase(nPlayer.getName())) { //already leader
            Message.CHAT_ADMIN_GUILD_SET_LEADER_ALREADYLEADER.vars(vars).send(sender);
            return;
        }

        Player oldleader = plugin.getServer().getPlayer(guild.getLeader().getName());

        guild.getLeader().cancelToolProgress();

        guild.setLeader(nPlayer);

        if(oldleader != null) {
            plugin.tagUtils.updatePrefix(oldleader);
        }

        if(nPlayer.isOnline()) {
            plugin.tagUtils.updatePrefix(nPlayer.getPlayer());
        }

        Message.CHAT_ADMIN_GUILD_SET_LEADER_SUCCESS.vars(vars).send(sender);
        Message.BROADCAST_GUILD_NEWLEADER.vars(vars).broadcast();
    }
}