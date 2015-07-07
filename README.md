<img src="http://novaguilds.marcin.co/img/newlogo.png" /><br/><br/>

NovaGuilds is my own guilds plugin, still in development, but I want to hear your opinions and ideas.<br/>
Please leave feedback!<br/><br/>
<b>Vault</b> is required!<br/>
<b>BarAPI</b> is required unless you turn it off in config!<br/>
<b>HolographicDisplays</b> is required unless you turn it off in config!<br/>
<br/><br/><br/>

<h2><b>Setup</b></h2>
<ul>
    <li>Download latest version</li>
    <li>Put it in your plugins/ directory</li>
    <li>Also put there Vault</li>
    <li>Add BarAPI and HolographicDisplays plugins if you need.</li>
    <li>Restart/reload the server</li>
    <li>Edit <b>config.yml</b> to setup your database</li>
    <li>Add <b>{TAG}</b> to players chat message/prefix (I recommend Essentials config)</li>
    <li>Restart/reload the server</li>
    <li>Enjoy and leave feedback!</li>
</ul>

<br/>
<h2><b>Features</b></h2>
<ul>
    <li>MySQL and SQLlite support</li>
    <li>Money required to create a guild (Vault)(Configurable)</li>
    <li>Items required to create a guild (Configurable)</li>
    <li>Configurable region interact (you can allow others to use stuff in guilds)</li>
    <li>Configurable messages</li>
    <li>Tags in chat, above player and in the tablist</li>
    <li>Advanced region selection</li>
    <li>Automatic MySQL tables configuration</li>
    <li>Broadcast messages</li>
    <li>Pay/withdraw money to/from guild's bank</li>
    <li>Allies, wars between guilds</li>
    <li>Language support</li>
    <li>Guild/Ally chat</li>
    <li>Configurable command names</li>
    <li>Automatic regions</li>
</ul>

<br/>
<h2><b>Planned features</b></h2>
<ul>
    <li>Autoupdate to latest build (?)</li>
    <li>Auto update MySQL tables</li>
    <li>Auto update config (?)</li>
    <li>Advanced horse protection</li>
    <li>Region resizing</li>
    <li>You tell me!</li>
</ul>

<br/>
<h2><b>Undocumented features (bugs)</b></h2>
<ul>
    <li>Player objects bug sometimes</li>
</ul>

<br/>
<h2><b>Commands</b></h2>
<table>
    <tr>
        <td>Command</td>
        <td>Description</td>
        <td>Usage</td>
    </tr>
    <tr>
        <td>/novaguilds, /ng</td>
        <td>Main cmd and plugin info</td>
        <td>/novaguilds [cmd] [params]</td>
    </tr>
    <tr>
        <td>/guild, /g</td>
        <td>Main guild command</td>
        <td>/g to list commands</td>
    </tr>
    <tr>
        <td>/ng tool</td>
        <td>Get NovaGuilds tool!</td>
        <td>Read its lore.</td>
    </tr>
    <tr>
        <td>/nga</td>
        <td>Admin commands</td>
        <td>Alias: /ng admin</td>
    </tr>
    <tr>
        <td>/nga reload</td>
        <td>Reload the plugin</td>
        <td>/nga reload</td>
    </tr>
    <tr>
        <td>/nga rg bypass</td>
        <td>Toggle region bypass
        <td>/nga rg bypass [player]</td>
    </tr>
    <tr>
        <td>/create</td>
        <td>Create a guild</td>
        <td>/create <tag> <name></td>
    </tr>
    <tr>
        <td>/abandon</td>
        <td>Abandon your guild</td>
        <td>/abandon</td>
    </tr>
    <tr>
        <td>/guildinfo, /gi</td>
        <td>Guild's information</td>
        <td>/gi <name></td>
    </tr>
    <tr>
        <td>/join</td>
        <td>Join a guild</td>
        <td>/join [name]</td>
    </tr>
    <tr>
        <td>/leave</td>
        <td>leave the guild</td>
        <td>/leave</td>
    </tr>
</table>

<br/>
<h2><b>Permissions</b></h2>
<table>
    <tr>
        <td>Permission</td>
        <td>Description</td>
    </tr>
    <tr>
        <td>novaguilds.region.create</td>
        <td>Create a region</td>
    </tr>
    <tr>
        <td>novaguilds.region.resize</td>
        <td>Resize regions</td>
        <td>Not implemented</td>
    </tr>
    <tr>
        <td>novaguilds.region.delete</td>
        <td>Delete region</td>
        <td>Not implemented</td>
    </tr>
    
    <tr>
        <td>novaguilds.guild.access</td>
        <td>Access to /g</td>
    </tr>
    <tr>
        <td>novaguilds.guild.home</td>
        <td>TP to guild's home</td>
    </tr>
    <tr>
        <td>novaguilds.guild.create</td>
        <td>Create a guild</td>
    </tr>
    <tr>
        <td>novaguilds.guild.abandon</td>
        <td>Abandon your guild</td>
    </tr>
    <tr>
        <td>novaguilds.guild.info</td>
        <td>Guild's information</td>
    </tr>
    <tr>
        <td>novaguilds.guild.leave</td>
        <td>Leaving the guild</td>
    </tr>
    
    <tr>
        <td>novaguilds.tool.get</td>
        <td>/ng tool permission</td>
    </tr>
    <tr>
        <td>novaguilds.tool.check</td>
        <td>Check Mode</td>
    </tr>
    
    <tr>
        <td>novaguilds.admin.access</td>
        <td>Access to <b>/nga</b></td>
    </tr>
    <tr>
        <td>novaguilds.admin.guild.kick</td>
        <td>Kick player from his guild</td>
    </tr>
    <tr>
        <td>novaguilds.admin.guild.abandon</td>
        <td>Abandon any guild</td>
    </tr>
    <tr>
        <td>novaguilds.admin.guild.tp</td>
        <td>TP to any guild's home</td>
    </tr>
    <tr>
        <td>novaguilds.admin.guild.tp.other</td>
        <td>TP somebody to guild's home</td>
    </tr>
    <tr>
        <td>novaguilds.admin.guild.money</td>
        <td>Manage money of any guild</td>
    </tr>
    <tr>
        <td>novaguilds.admin.guild.fullinfo</td>
        <td>Show guild's full info</td>
    </tr>
    <tr>
        <td>novaguilds.admin.guild.setname</td>
        <td>Change guild's name</td>
    </tr>
    <tr>
        <td>novaguilds.admin.guild.settag</td>
        <td>Change guild's tag</td>
    </tr>
    <tr>
        <td>novaguilds.admin.guild.list</td>
        <td>Show all guilds</td>
    </tr>
    <tr>
        <td>novaguilds.admin.region.remove</td>
        <td>Remove any region</td>
    </tr>
    <tr>
        <td>novaguilds.admin.region.list</td>
        <td>List regions</td>
    </tr>
    <tr>
        <td>novaguilds.admin.region.resize</td>
        <td>Resize any region</td>
        <td>Not implemented</td>
    </tr>
    <tr>
        <td>novaguilds.admin.region.bypass</td>
        <td>Region bypass</td>
    </tr>
    <tr>
        <td>novaguilds.admin.region.bypass.other</td>
        <td>Toggle someones region bypass</td>
    </tr>
    <tr>
        <td>novaguilds.admin.reload</td>
        <td>Reload the plugin</td>
    </tr>
    <tr>
        <td>novaguilds.admin.save</td>
        <td>Save plugin data</td>
    </tr>
    <tr>
        <td>novaguilds.chat.notag</td>
        <td>No tag in chat</td>
    </tr>
</table>
