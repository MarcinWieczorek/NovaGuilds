/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.TagUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminGuildSetLeader implements Executor {
    private final Command command = Command.ADMIN_GUILD_SET_LEADER;

    public CommandAdminGuildSetLeader() {
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

        Map<String, String> vars = new HashMap<>();
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

        if(nPlayer.isLeader()) { //already leader
            Message.CHAT_ADMIN_GUILD_SET_LEADER_ALREADYLEADER.vars(vars).send(sender);
            return;
        }

        Player oldLeader = guild.getLeader().getPlayer();

        guild.getLeader().cancelToolProgress();

        guild.setLeader(nPlayer);

        if(oldLeader != null) {
            TagUtils.updatePrefix(oldLeader);
        }

        if(nPlayer.isOnline()) {
            TagUtils.updatePrefix(nPlayer.getPlayer());
        }

        Message.CHAT_ADMIN_GUILD_SET_LEADER_SUCCESS.vars(vars).send(sender);
        Message.BROADCAST_GUILD_NEWLEADER.vars(vars).broadcast();
    }
}