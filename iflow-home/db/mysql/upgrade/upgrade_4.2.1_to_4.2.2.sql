ALTER TABLE `iflow`.`reporting` ADD INDEX `IDX_REPORTING`(`flowid`, `pid`, `subpid`);

ALTER TABLE `iflow`.`users`
 ADD COLUMN `orgadm_users` INT(1) UNSIGNED NOT NULL DEFAULT 1 AFTER `title`,
 ADD COLUMN `orgadm_flows` INT(1) UNSIGNED NOT NULL DEFAULT 1 AFTER `orgadm_users`,
 ADD COLUMN `orgadm_processes` INT(1) UNSIGNED NOT NULL DEFAULT 1 AFTER `orgadm_flows`,
 ADD COLUMN `orgadm_resources` INT(1) UNSIGNED NOT NULL DEFAULT 1 AFTER `orgadm_processes`,
 ADD COLUMN `orgadm_org` INT(1) UNSIGNED NOT NULL DEFAULT 1 AFTER `orgadm_resources`;

;