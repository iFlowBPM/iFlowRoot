insert into counter values ('nodekey',0,NOW());

DROP TABLE IF EXISTS `iflow`.`active_node`;
CREATE TABLE  `iflow`.`active_node` (
  `nodekey` varchar(50) NOT NULL,
  `expiration` datetime NOT NULL,
  PRIMARY KEY (`nodekey`)
);

DELIMITER $$
DROP PROCEDURE IF EXISTS `get_next_nodekey` $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_next_nodekey`(OUT retnodekey INTEGER)
BEGIN
    DECLARE tmp integer;
    SELECT GET_LOCK('iflow.get_next_nodekey',-1);
    set retnodekey = 1;
    select value into tmp from counter where name='nodekey';
    update counter set value=(tmp +1) where  name='nodekey';
    select value into retnodekey from counter where name='nodekey';
    SELECT RELEASE_LOCK('iflow.get_next_nodekey');
END $$
DELIMITER ;

DROP TABLE IF EXISTS `iflow`.`sharedobjectrefresh`;
CREATE TABLE  `iflow`.`sharedobjectrefresh` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `flowid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;