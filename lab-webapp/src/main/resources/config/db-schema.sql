-- generate by longkai@2013-07-31
DROP TABLE IF EXISTS info_notices;
DROP TABLE IF EXISTS info_users;

CREATE TABLE info_users (
    id bigint(20) auto_increment,
    about VARCHAR(255) DEFAULT NULL,
    account VARCHAR(30) NOT NULL,
    authed_name VARCHAR(30) NOT NULL,
    blocked tinyint(1) NOT NULL DEFAULT 0,
    contact VARCHAR(50) NOT NULL,
    join_date datetime NOT NULL,
    last_login_ip VARCHAR(20) DEFAULT NULL,
    last_login_date datetime DEFAULT NULL,
    last_modified_date datetime DEFAULT NULL,
    org VARCHAR(30) NOT NULL,
    password CHAR(32) NOT NULL,
    type VARCHAR(10) NOT NULL DEFAULT 'DEFAULT',
    CONSTRAINT PK_INFO_USERS PRIMARY KEY (id)
) DEFAULT CHARSET=utf8;

CREATE TABLE info_notices (
    id bigint(20) auto_increment,
    add_date datetime NOT NULL,
    blocked tinyint(1) DEFAULT 0,
    click_times bigint(20) DEFAULT 0,
    content VARCHAR(20000) NOT NULL,
    doc_name VARCHAR(255) DEFAULT NULL,
    doc_url VARCHAR(255) DEFAULT NULL,
    last_modified_date datetime DEFAULT NULL,
    title VARCHAR (255) NOT NULL,
    uid bigint(20) NOT NULL,
    CONSTRAINT PK_INFO_NOTICES PRIMARY KEY (id),
    CONSTRAINT FK_INFO_NOTICES FOREIGN KEY (uid) REFERENCES info_users(id)
) DEFAULT CHARSET=utf8;
