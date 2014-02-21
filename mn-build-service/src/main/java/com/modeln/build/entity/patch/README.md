This package will be used later when we will implement the patch service feature.
As of now these entities haven't been tested yet!

CREATE TABLE IF NOT EXISTS `patch_approvals` (
  `patch_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `status` enum('approved','rejected') NOT NULL default 'approved',
  `patch_status` enum('approval','complete') NOT NULL default 'approval',
  `comment` text,
  PRIMARY KEY  (`patch_id`,`user_id`,`patch_status`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `patch_approvers` (
  `approver_id` int(10) unsigned NOT NULL auto_increment,
  `build_version` varchar(127) NOT NULL,
  `group_id` int(10) unsigned NOT NULL,
  `status` enum('approval','complete') NOT NULL default 'approval',
  KEY `approver_idx` (`approver_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=122 ;

CREATE TABLE IF NOT EXISTS `patch_assignment` (
  `user_id` int(10) unsigned NOT NULL,
  `patch_id` int(10) unsigned NOT NULL,
  `start_date` datetime NOT NULL default '0000-00-00 00:00:00',
  `end_date` datetime NOT NULL default '0000-00-00 00:00:00',
  `deadline` datetime NOT NULL default '0000-00-00 00:00:00',
  `priority` enum('low','medium','high') NOT NULL default 'low',
  `comments` text,
  KEY `user_id` (`user_id`,`patch_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `patch_assignment_history` (
  `patch_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `change_date` datetime NOT NULL default '0000-00-00 00:00:00',
  `old_user` int(10) unsigned NOT NULL,
  `new_user` int(10) unsigned NOT NULL,
  KEY `patch_idx` (`patch_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `patch_comments` (
  `comment_id` int(10) unsigned NOT NULL auto_increment,
  `patch_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `save_date` datetime NOT NULL default '0000-00-00 00:00:00',
  `status` enum('show','hide','admin') NOT NULL default 'show',
  `comment` text,
  PRIMARY KEY  (`comment_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=171 ;

CREATE TABLE IF NOT EXISTS `patch_fixes` (
  `patch_id` int(10) unsigned NOT NULL,
  `bug_id` int(10) unsigned NOT NULL,
  `version_ctrl_root` varchar(255) default NULL,
  `exclusions` varchar(255) default NULL,
  `notes` text,
  `origin` int(10) unsigned default NULL,
  `request_date` datetime NOT NULL,
  PRIMARY KEY  (`patch_id`,`bug_id`),
  KEY `origin` (`origin`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `patch_group` (
  `group_id` int(10) unsigned NOT NULL auto_increment,
  `group_name` varchar(255) NOT NULL,
  `group_desc` text,
  `status` enum('optional','recommended','required') NOT NULL default 'optional',
  `build_version` varchar(127) default NULL,
  PRIMARY KEY  (`group_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=17 ;

CREATE TABLE IF NOT EXISTS `patch_group_fixes` (
  `group_id` int(10) unsigned NOT NULL,
  `bug_id` int(10) unsigned NOT NULL,
  `version_ctrl_root` varchar(255) default NULL,
  `exclusions` varchar(255) default NULL,
  `notes` text,
  PRIMARY KEY  (`group_id`,`bug_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `patch_notification` (
  `notification_id` int(10) unsigned NOT NULL auto_increment,
  `user_id` int(10) unsigned NOT NULL,
  `account_id` int(10) unsigned default NULL,
  `build_version` varchar(127) default NULL,
  `status` set('saved','approval','rejected','pending','canceled','running','failed','complete','release') NOT NULL default 'approval',
  KEY `notification_id` (`notification_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=12 ;

CREATE TABLE IF NOT EXISTS `patch_request` (
  `patch_id` int(10) unsigned NOT NULL auto_increment,
  `patch_name` varchar(127) NOT NULL,
  `request_date` datetime NOT NULL,
  `account_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `env_id` int(10) unsigned NOT NULL,
  `build_id` int(10) unsigned NOT NULL,
  `patch_options` varchar(255) default NULL,
  `status` enum('saved','approval','rejected','pending','canceled','running','branching','branched','building','built','failed','complete','release') NOT NULL default 'saved',
  `justification` text,
  `notification` text,
  `internal_only` enum('true','false') NOT NULL default 'false',
  `comments` text,
  `owner_to` int(10) unsigned default NULL,
  `patch_build` int(10) unsigned default NULL,
  `previous_patch` int(10) unsigned default NULL,
  PRIMARY KEY  (`patch_id`),
  KEY `customer_idx` (`account_id`),
  KEY `user_idx` (`user_id`),
  KEY `status` (`status`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=683 ;