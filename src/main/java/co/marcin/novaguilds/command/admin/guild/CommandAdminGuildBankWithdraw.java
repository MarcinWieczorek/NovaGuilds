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
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildBankWithdraw implements Executor.ReversedAdminGuild {
	private NovaGuild guild;
	private final Commands command = Commands.ADMIN_GUILD_BANK_WITHDRAW;

	public CommandAdminGuildBankWithdraw() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void guild(NovaGuild guild) {
		this.guild = guild;
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

		if(args.length != 1) { //invalid arguments
			Message.CHAT_USAGE_NGA_GUILD_BANK_WITHDRAW.send(sender);
			return;
		}

		String money_str = args[0];

		if(!NumberUtils.isNumeric(money_str)) { //money not int
			Message.CHAT_ENTERINTEGER.send(sender);
			return;
		}

		double money = Double.parseDouble(money_str);

		if(money < 0) {
			Message.CHAT_BASIC_NEGATIVENUMBER.send(sender);
			return;
		}

		if(guild.getMoney() < money) { //guild has not enough money
			Message.CHAT_GUILD_BANK_WITHDRAW_NOTENOUGH.send(sender);
			return;
		}

		money = NumberUtils.roundOffTo2DecPlaces(money);

		guild.takeMoney(money);

		HashMap<String,String> vars = new HashMap<>();
		vars.put("MONEY",money_str);
		vars.put("GUILDNAME", guild.getName());
		Message.CHAT_ADMIN_GUILD_BANK_WITHDREW.vars(vars).send(sender);
	}
}
