DROP TABLE IF EXISTS serial_code_templates;

CREATE TABLE `serial_code_templates` (
  `template` VARCHAR(50) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(500),
  `callback` VARCHAR(50),
  `flag` VARCHAR(50),
  `organization` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`template`, `name`, `organization`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;