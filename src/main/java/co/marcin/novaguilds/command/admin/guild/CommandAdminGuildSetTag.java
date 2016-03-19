/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
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


import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import org.bukkit.command.CommandSender;

public class CommandAdminGuildSetTag extends AbstractCommandExecutor.ReversedAdminGuild {
	private static final Command command = Command.ADMIN_GUILD_SET_TAG;

	public CommandAdminGuildSetTag() {
		super(command);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length == 0) {
			Message.CHAT_GUILD_ENTERTAG.send(sender);
			return;
		}

		String newTag = args[0];

		if(GuildManager.getGuildFind(newTag) != null) {
			Message.CHAT_CREATEGUILD_TAGEXISTS.send(sender);
			return;
		}

		//all passed
		guild.setTag(newTag);

		TagUtils.refresh();
		TabUtils.refresh();

		Message.CHAT_ADMIN_GUILD_SET_TAG.setVar(VarKey.TAG, newTag).send(sender);
	}
}
