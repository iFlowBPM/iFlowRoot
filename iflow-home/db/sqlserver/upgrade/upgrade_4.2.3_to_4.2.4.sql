ALTER TABLE users ADD employee_number VARCHAR(50);
UPDATE dbo.users SET employee_number = employeeid;
