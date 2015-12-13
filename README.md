<a href="http://novaguilds.pl/"><img src="http://novaguilds.marcin.co/img/newlogo.png" /></a><br/><br/>

NovaGuilds is my own guilds plugin, still in development, but I want to hear your opinions and ideas.<br/>
Please leave feedback!<br/><br/>
<b>Vault</b> is required!<br/>
<b>BarAPI</b> is required unless you turn it off in config!<br/>
<b>HolographicDisplays</b> is required unless you turn it off in config!<br/>
<br/><br/>
[![Join the chat at https://gitter.im/MarcinWieczorek/NovaGuilds](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/MarcinWieczorek/NovaGuilds?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/MarcinWieczorek/NovaGuilds.svg?branch=master)](https://travis-ci.org/MarcinWieczorek/NovaGuilds)
<br/>

<h2><b>Downloads</b></h2>
The plugin works on all <b>1.7</b> and <b>1.8</b> versions!<br/>
<a href="http://novaguilds.pl">http://novaguilds.pl/</a>
<br/><br/><br/>

<img src="http://novaguilds.marcin.co/img/ss/ngss1.jpg" alt="ss" />
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
    <li>Region resizing</li>
    <li>Banks</li>
    <li>VanishNoPacket support</li>
    <li>Auto update MySQL tables</li>
</ul>

<br/>
<h2><b>Planned features</b></h2>
<ul>
    <li>Auto update to latest build (?)</li>
    <li>Auto update config (?)</li>
    <li>Advanced horse protection</li>
    <li>You tell me!</li>
</ul>

<br/>
<h2><b>Undocumented features (bugs)</b></h2>
<ul>
    <li>Flat and SQLite are not stable, use MySQL</li>
    <li>Found any? Github -> issues</li>
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
    <thead>
        <tr>
            <td>Permission</td>
            <td>Description</td>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>novaguilds.admin.access</td>
            <td>Access to /nga</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.access</td>
            <td>Access to /nga g</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.abandon</td>
            <td>/nga g <guild> abandon</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.bank.pay</td>
            <td>/nga g <guild> pay <amount></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.bank.withdraw</td>
            <td>/nga g <guild> withdraw <amount></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.inactive.update</td>
            <td>/nga g inactive update</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.inactive.clean</td>
            <td>/nga g inactive clean</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.inactive.list</td>
            <td>/nga g inactive list</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.invite</td>
            <td>/nga g <guild> invite <player></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.kick</td>
            <td>/nga g <guild> kick <player></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.list</td>
            <td>/nga g list</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.leader</td>
            <td>/nga g <guild> leader <player></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.liveregenerationtime</td>
            <td>/nga g <guild> liveregentime <timestring></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.lives</td>
            <td>/nga g <guild> lives <lives></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.setname</td>
            <td>/nga g <guild> setname <newname></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.setpoints</td>
            <td>/nga g <guild> setpoints <points></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.settag</td>
            <td>/nga g <guild> setpoints <points></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.timerest</td>
            <td>/nga g <guild> timerest <timestring></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.tp</td>
            <td>/nga g <guild> tp</td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.bypass</td>
            <td>/nga rg bypass</td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.bypass.other</td>
            <td>/nga rg bypass <player></td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.delete</td>
            <td>/nga rg delete <guild></td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.list</td>
            <td>/nga rg list</td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.tp</td>
            <td>/nga rg tp <guild></td>
        </tr>
        <tr>
            <td>novaguilds.admin.reload</td>
            <td>/nga reload</td>
        </tr>
        <tr>
            <td>novaguilds.admin.save</td>
            <td>/nga save [guilds/players/regions]</td>
        </tr>
        <tr>
            <td>novaguilds.admin.save.notify</td>
            <td>Autosave notify message</td>
        </tr>
        <tr>
            <td>novaguilds.admin.updateavailable</td>
            <td>Update notify message</td>
        </tr>
        <tr>
            <td>novaguilds.guild.access</td>
            <td>/g</td>
        </tr>
        <tr>
            <td>novaguilds.guild.leave</td>
            <td>/leave</td>
        </tr>
        <tr>
            <td>novaguilds.guild.ally</td>
            <td>/g ally [guild]</td>
        </tr>
        <tr>
            <td>novaguilds.guild.bank.pay</td>
            <td>/g pay <amount></td>
        </tr>
        <tr>
            <td>novaguilds.guild.bank.withdraw</td>
            <td>/g withdraw <amount></td>
        </tr>
        <tr>
            <td>novaguilds.guild.compass</td>
            <td>/g compass</td>
        </tr>
        <tr>
            <td>novaguilds.guild.create</td>
            <td>/create <tag> <guildname></td>
        </tr>
        <tr>
            <td>novaguilds.guild.effect</td>
            <td>/g effect</td>
        </tr>
        <tr>
            <td>novaguilds.guild.home</td>
            <td>/g home</td>
        </tr>
        <tr>
            <td>novaguilds.guild.home.set</td>
            <td>/g home set</td>
        </tr>
        <tr>
            <td>novaguilds.guild.invite</td>
            <td>/invite <player></td>
        </tr>
        <tr>
            <td>novaguilds.guild.join</td>
            <td>/join [guild]</td>
        </tr>
        <tr>
            <td>novaguilds.guild.kick</td>
            <td>/g kick <player></td>
        </tr>
        <tr>
            <td>novaguilds.guild.gui</td>
            <td>/gg</td>
        </tr>
        <tr>
            <td>novaguilds.guild.pvptoggle</td>
            <td>/g pvp</td>
        </tr>
        <tr>
            <td>novaguilds.guild.requireditems</td>
            <td>/g items</td>
        </tr>
        <tr>
            <td>novaguilds.guild.top</td>
            <td>/g top</td>
        </tr>
        <tr>
            <td>novaguilds.guild.war</td>
            <td>/g war [guild]</td>
        </tr>
        <tr>
            <td>novaguilds.region.create</td>
            <td>/g buyregion</td>
        </tr>
        <tr>
            <td>novaguilds.region.resize</td>
            <td>Resize with the tool</td>
        </tr>
        <tr>
            <td>novaguilds.chat.notag</td>
            <td>No tag in chat</td>
        </tr>
        <tr>
            <td>novaguilds.playerinfo</td>
            <td>/pi or right click player</td>
        </tr>
        <tr>
            <td>novaguilds.tool.check</td>
            <td>Checking regions with the tool</td>
        </tr>
        <tr>
            <td>novaguilds.tool.get</td>
            <td>/ng tool</td>
        </tr>
    </tbody>
</table>
