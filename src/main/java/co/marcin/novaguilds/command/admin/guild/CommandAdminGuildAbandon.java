package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.AbandonCause;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.event.GuildAbandonEvent;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.interfaces.ExecutorReversedAdminGuild;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildAbandon implements Executor, ExecutorReversedAdminGuild {
	private NovaGuild guild;
	private final Commands command;

	public CommandAdminGuildAbandon(Commands command) {
		this.command = command;
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

		//fire event
		GuildAbandonEvent guildAbandonEvent = new GuildAbandonEvent(guild, AbandonCause.ADMIN);
		plugin.getServer().getPluginManager().callEvent(guildAbandonEvent);

		//if event is not cancelled
		if(!guildAbandonEvent.isCancelled()) {
			guild.getLeader().cancelToolProgress();
			plugin.getHologramManager().refreshTopHolograms();

			//delete guild
			plugin.getGuildManager().delete(guild);

			HashMap<String, String> vars = new HashMap<>();
			vars.put("PLAYERNAME", sender.getName());
			vars.put("GUILDNAME", guild.getName());
			Message.BROADCAST_ADMIN_GUILD_ABANDON.vars(vars).broadcast();
		}

		plugin.tagUtils.refreshGuild(guild);
	}
	
}
