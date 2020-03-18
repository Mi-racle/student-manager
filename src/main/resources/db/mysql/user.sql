CREATE DATABASE IF NOT EXISTS studentmanager;

ALTER DATABASE studentmanager
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;

GRANT ALL PRIVILEGES ON studentmanager.* TO 'studentmanager@%' IDENTIFIED BY 'studentmanager';
