PRAGMA encoding = "UTF-8";
--
CREATE TABLE `{SQLPREFIX}guilds` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `tag` tinytext NOT NULL,
  `name` tinytext NOT NULL,
  `leader` tinytext NOT NULL,
  `spawn` tinytext NOT NULL,
  `allies` tinytext NOT NULL,
  `alliesinv` tinytext NOT NULL,
  `war` tinytext NOT NULL,
  `nowarinv` tinytext NOT NULL,
  `money` double NOT NULL,
  `points` int(11) NOT NULL,
  `lives` int(11) NOT NULL,
  `timerest` int(11) NOT NULL,
  `lostlive` int(11) NOT NULL,
  `activity` int(11) NOT NULL,
  `created` int(11) NOT NULL,
  `bankloc` tinytext NOT NULL,
  `slots` tinytext NOT NULL,
  `openinv` int(1) NOT NULL,
);
--
CREATE TABLE `{SQLPREFIX}players` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `uuid` tinytext NOT NULL,
  `name` tinytext NOT NULL,
  `guild` tinytext NOT NULL,
  `invitedto` tinytext NOT NULL,
  `points` int(11) NOT NULL,
  `kills` int(11) NOT NULL,
  `deaths` int(11) NOT NULL
);
--
CREATE TABLE `{SQLPREFIX}regions` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `loc_1` tinytext NOT NULL,
  `loc_2` tinytext NOT NULL,
  `guild` tinytext NOT NULL,
  `world` tinytext NOT NULL
);
