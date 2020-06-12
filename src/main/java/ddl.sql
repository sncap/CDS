-- public.data_source definition

-- Drop table

-- DROP TABLE data_source;

CREATE TABLE data_source (
	name VARCHAR(200) NOT NULL,
	db_url VARCHAR(400) NULL,
	conn_max_cnt INT8 NULL,
	conn_timeout INT8 NULL,
	update_time TIMESTAMP NULL,
	update_user VARCHAR(200) NULL,
	biz_dept VARCHAR(400) NULL,
	biz_name VARCHAR(400) NULL,
	user_id VARCHAR(200) NULL,
	passwd VARCHAR(200) NULL,
	schema_name VARCHAR(200) NULL,
	db_type VARCHAR(200) NULL,
	CONSTRAINT "primary" PRIMARY KEY (name )
);

-- public.data_service definition

-- Drop table

--DROP TABLE data_service;

CREATE TABLE data_service (
	name VARCHAR(200) NULL,
	api_url VARCHAR(400) PRIMARY KEY,
	sql TEXT NULL,
	result_max_cnt INT8 NULL,
	result_timeout INT8 NULL,
	cache_timeout INT8 NULL
);

CREATE TABLE demo_users (
	user_id VARCHAR(50) PRIMARY KEY,
	name VARCHAR(200) NULL,
	password VARCHAR(200) NULL,
	role VARCHAR(50) NULL,
	use_yn VARCHAR(1) NULL,
	email VARCHAR(100) NULL
);


CREATE TABLE data_log (
    log_id SERIAL PRIMARY KEY,
    api_url VARCHAR(200),
    el_time  INT8 NULL,
    result_cnt INT8 NULL,
    result_max_cnt INT8 NULL,
	result_timeout INT8 NULL,
	cache_timeout INT8 NULL,
	cache_yn char(1) NULL ,
	ds_name VARCHAR(200) NULL,
	conn_max_cnt INT8 NULL,
	conn_timeout INT8 NULL,
	update_time TIMESTAMP NULL,
	msg text NULL
) ;

