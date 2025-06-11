INSERT INTO iflow.organizations
(organizationid, name, description, style_url, logo_url, locked)
VALUES(12001, 'universo', 'Universo', NULL, NULL, 0);

INSERT INTO iflow.organizational_units
(unitid, parent_id, organizationid, name, description)
VALUES(12001, -1, 12001, 'Universo', 'Universo');

INSERT INTO iflow.organization_theme
(organizationid, theme, style_url, logo_url, menu_location, menu_style, proc_menu_visible)
VALUES(12001, 'default', 'default', 'Logo', 'left', 'list', 1);

INSERT INTO iflow.organization_settings
(organizationid, lang, region, timezone)
VALUES(12001, 'pt', 'PT', 'Europe/Lisbon');

INSERT INTO `users` (`unitid`, `username`, `userpassword`, `email_address`, `gender`, `first_name`, `last_name`, `phone_number`, `fax_number`, `mobile_number`, `company_phone`, `sessionid`, `activated`, `password_reset`, `orgadm`, `department`, `employeeid`, `manager`, `telephonenumber`, `title`, `orgadm_users`, `orgadm_flows`, `orgadm_processes`, `orgadm_resources`, `orgadm_org`) 
VALUES (12001, 'universo', 'n9Y9r6Adubeo+vUWig3fiA==', 'universo@uniksystem.com', 'M', 'Universo', 'Admin', '', '', '', '', NULL, 1, 0, 1, '', NULL, NULL, NULL, NULL, 1, 1, 1, 1, 1);
insert into user_settings(userid,lang,region,timezone,tutorial,help_mode,tutorial_mode)
values('universo','pt','PT','Europe/Lisbon','none',0,0
);

INSERT INTO unitmanagers (userid, unitid)
VALUES (LAST_INSERT_ID(), 12001);


