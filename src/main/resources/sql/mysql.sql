CREATE TABLE `{SQLPREFIX}guilds` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `tag` tinytext CHARACTER SET utf8 NOT NULL,
  `name` tinytext CHARACTER SET utf8 NOT NULL,
  `leader` tinytext CHARACTER SET utf8 NOT NULL,
  `spawn` tinytext CHARACTER SET utf8 NOT NULL,
  `allies` tinytext CHARACTER SET utf8 NOT NULL,
  `alliesinv` tinytext CHARACTER SET utf8 NOT NULL,
  `war` tinytext CHARACTER SET utf8 NOT NULL,
  `nowarinv` tinytext CHARACTER SET utf8 NOT NULL,
  `money` double NOT NULL,
  `points` int(11) unsigned NOT NULL,
  `lives` int(11) NOT NULL,
  `timerest` int(11) NOT NULL,
  `lostlive` int(11) NOT NULL,
  `activity` int(11) NOT NULL,
  `bankloc` tinytext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
--
CREATE TABLE `{SQLPREFIX}players` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` tinytext CHARACTER SET utf8 NOT NULL,
  `name` tinytext CHARACTER SET utf8 NOT NULL,
  `guild` tinytext CHARACTER SET utf8 NOT NULL,
  `invitedto` tinytext CHARACTER SET utf8 NOT NULL,
  `points` int(11) NOT NULL,
  `kills` int(11) NOT NULL,
  `deaths` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
--
CREATE TABLE `{SQLPREFIX}regions` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `loc_1` tinytext CHARACTER SET utf8 NOT NULL,
  `loc_2` tinytext CHARACTER SET utf8 NOT NULL,
  `guild` tinytext CHARACTER SET utf8 NOT NULL,
  `world` tinytext CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
