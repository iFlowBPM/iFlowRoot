-- tvelho 16/10/2010
CREATE TABLE `user_passimage` (
  `passid` INT NOT NULL AUTO_INCREMENT,
  `userid` INT NOT NULL,
  `passimage` LONGBLOB,
  PRIMARY KEY (passid),
  CONSTRAINT `user_passimage_fk` FOREIGN KEY `user_passimage_fk` (`userid`)
  REFERENCES `users` (`userid`)
) 
ENGINE = INNODB DEFAULT CHARSET=utf8;

ALTER TABLE documents ADD COLUMN `numass` INT NOT NULL DEFAULT 0;

ALTER TABLE notifications ADD COLUMN link VARCHAR(45) NOT NULL DEFAULT 'false';