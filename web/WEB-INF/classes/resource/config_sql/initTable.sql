
/*
创建users表
 */
CREATE TABLE
  IF
  NOT EXISTS users (
  uid INT NOT NULL PRIMARY KEY auto_increment,
  uNAME VARCHAR ( 30 ),
  uusername VARCHAR ( 50 ),
  upassword VARCHAR ( 50 ),
  upower INT ( 3 ),
  uusergroup INT ( 8 ),
  uctime TIMESTAMP,
  uinittime TIMESTAMP,
  uindepartment VARCHAR ( 80 )
  );

/*
创建userinfo表
 */
CREATE TABLE
  IF
  NOT EXISTS userinfo (
  usercode INT NOT NULL PRIMARY KEY,
  userhead BLOB,
  userinfo BLOB
);