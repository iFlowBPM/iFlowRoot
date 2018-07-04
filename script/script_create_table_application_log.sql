CREATE TABLE `iflow`.`application_log` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `date` DATETIME NOT NULL,
  `type` VARCHAR(45) NOT NULL,
  `value` VARCHAR(2000) NOT NULL,
  PRIMARY KEY (`id`));