ALTER TABLE `process` DROP PRIMARY KEY,
 ADD PRIMARY KEY  USING BTREE(`flowid`, `pid`, `subpid`, `pnumber`);
