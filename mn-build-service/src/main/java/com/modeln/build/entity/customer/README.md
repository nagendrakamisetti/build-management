This package contains entity that are used as a bridge from the build package to the patch package.
Since the patch service is a feature that will be implemented later on, I won't do much more here.
However the entity implementation has been tested already.

CREATE TABLE IF NOT EXISTS `customer_account` (
  `account_id` int(10) unsigned NOT NULL auto_increment,
  `account_name` varchar(127) NOT NULL,
  `short_name` varchar(20) NOT NULL,
  `branch_type` enum('product','customer') NOT NULL default 'product',
  PRIMARY KEY  (`account_id`),
  KEY `name_idx` (`account_name`)
);

CREATE TABLE IF NOT EXISTS `customer_env` (
  `env_id` int(10) unsigned NOT NULL auto_increment,
  `account_id` int(10) unsigned NOT NULL,
  `env_name` varchar(127) NOT NULL,
  `short_name` varchar(20) default NULL,
  `product_id` int(10) unsigned NOT NULL,
  `build_id` int(10) unsigned NOT NULL,
  KEY `account_idx` (`account_id`),
  KEY `env_id` (`env_id`)
);

CREATE TABLE IF NOT EXISTS `release_product` (
  `product_id` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(127) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY  (`product_id`),
  KEY `name_idx` (`name`)
);

