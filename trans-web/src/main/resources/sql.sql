SELECT user_id as userid
	, 100000 as maxloginperip 
	, 100000 as maxloginnumber 
	, user_password as userpassword
	, '/home/pelom/desen/files/' || user_id as homedirectory
	, true as enableflag
	, true as writepermission
	, true as readpermission
	, 100000 as idletime
	, 100000 as uploadrate
	, 100000 as downloadrate 

FROM
	FTP_USER WHERE user_id = 'ANDRE'

DROP TABLE ftp_user;
CREATE TABLE ftp_user
(
  user_id character varying NOT NULL,
  user_password character varying,
  homedirectory character varying,
  enableflag boolean,
  writepermission boolean,
  idletime integer,
  uploadrate integer,
  downloadrate integer,
  CONSTRAINT ftp_user_pkey PRIMARY KEY (user_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE ftp_user
  OWNER TO postgres;
