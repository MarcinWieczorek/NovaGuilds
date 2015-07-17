PRAGMA encoding = "UTF-8";
--
CREATE TABLE `{SQLPREFIX}guilds` (
  `id` unsigned int(11) primary key NOT NULL,
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
  `banklock` tinytext NOT NULL
);
--
CREATE TABLE `{SQLPREFIX}players` (
  `id` unsigned int(11) primary key NOT NULL,
  `uuid` tinytext NOT NULL,
  `name` tinytext NOT NULL,
  `guild` tinytext NOT NULL,
  `invitedto` tinytext NOT NULL
);
--
CREATE TABLE `{SQLPREFIX}regions` (
  `id` unsigned int(11) primary key NOT NULL,
  `loc_1` tinytext NOT NULL,
  `loc_2` tinytext NOT NULL,
  `guild` tinytext NOT NULL,
  `world` tinytext NOT NULL
);
