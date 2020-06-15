# RESTful API Specification


****
## 1. Data Source Service ADD/UPDATE API
#### 1.1 HTTP Request
   * POST /CDS/ds_upsert
#### 1.2 Headers
   | Name | Description | Type | Required |
   |------|-------------|------|----------|
   | x-cds-authentication | Authorization Token | String | True |
#### 1.3 Parameters
   | Properties | Description | Type | Required |
   |------------|-------------|------|----------|
   | name | ResourceName (PK) | String | True |
   | db_url | Data Source Connection URL | String | True |
   | conn_max_cnt | Max Connection Count | String | True |
   | conn_timeout | Connection Timeout | String | True |
   | update_user | Edit User Name | String | True |
   | biz_dept | 담당 부서 | String | True |
   | biz_name | 담당자 이름 | String | True |
   | user_id | 접속 ID | String | True |
   | passwd | 접속 Password(Encrypted Value) | String | True |
   | schema_name | schema 명 | String | True |
   | db_type | Database 종류(mysql, oracle, ... 등) | String | True |

#### 1.4 Example
   * Request
   
    ```
    POST /cds/ds_upsert HTTP/1.1
    Host: localhost:8080
    x-cds-authentication: yjWq0Nv5bJOE3sZBZ4sGuK1KNHkD9KTX
    Cache-Control: no-cache
    Content-Type: multipart/form-data; 
    Content-Disposition: form-data; name="name"
    ds5
    Content-Disposition: form-data; name="db_url"
    jdbc:postgresql://localhost:5432/cds_db
    Content-Disposition: form-data; name="conn_max_cnt"
    10
    Content-Disposition: form-data; name="conn_timeout"
    5
    Content-Disposition: form-data; name="update_user"
    admin
    Content-Disposition: form-data; name="biz_dept"    
    운영그룹 
    Content-Disposition: form-data; name="biz_name"
    나운영 
    Content-Disposition: form-data; name="user_id"
    postgres
    Content-Disposition: form-data; name="passwd"
    YM7rfXlxVKgpi/ZENshlHg==
    Content-Disposition: form-data; name="schema_name"
    public
    Content-Disposition: form-data; name="db_type"
    postgressql
    ```
   * Response
   
    ```
    Data source update row 1
    ```    

****
## 2. SQL Service ADD/UPDATE API
#### 2.1 HTTP Request
   * POST /CDS/sql_upsert
#### 2.2 Headers
   | Name | Description | Type | Required |
   |------|-------------|------|----------|
   | x-cds-authentication | Authorization Token | String | True |
#### 2.3 Parameters
   | Properties | Description | Type | Required |
   |------------|-------------|------|----------|
   | api_url | API Service URL(PK) | String | True |
   | name | Data Resource Name( ex. ds1, ds2...) | String | True |
   | sql | Service SQL | String | True |
   | result_max_cnt | Max Query Result Count | String | True |
   | result_timeout | Max Query waiting time | String | True |
   | cache_timeout | Cache Flush Time | String | True |

#### 2.4 Example
   * Request
     ```
     POST /cds/sql_upsert HTTP/1.1
     Host: localhost:8080
     x-cds-authentication: yjWq0Nv5bJOE3sZBZ4sGuK1KNHkD9KTX
     Cache-Control: no-cache
     Content-Type: multipart/form-data; 
     Content-Disposition: form-data; name="name"
     ds5
     Content-Disposition: form-data; name="api_url"
     api/v3/getAll
     Content-Disposition: form-data; name="sql"
     select 1
     Content-Disposition: form-data; name="result_max_cnt"
     -1
     Content-Disposition: form-data; name="result_timeout"
     5
     Content-Disposition: form-data; name="cache_timeout"
     -1
     ```  
   * Response
     ```
     Data service update row 1   
     ```     
       
****
## 3. Data API Service
#### 3.1 HTTP Request
   * GET  /api/**
   * POST /api/**
#### 3.2 Headers
   | Name | Description | Type | Required |
   |------|-------------|------|----------|
   | x-cds-authentication | Authorization Token | String | True |

#### 3.3 Parameters
   Depend on follow API URL(Query)

#### 3.4 Example
   * Request
   ```
   GET /api/v2/demo_users?name=%25 HTTP/1.1
   Host: localhost:8080
   x-cds-authentication: yjWq0Nv5bJOE3sZBZ4sGuK1KNHkD9KTX
   Cache-Control: no-cache   
   ```
   * Response 
   ```
   [{"password":"123","role":"administrator","user_id":"admin","use_yn":"Y","name":"권혁민","email":"google@gami.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_1","use_yn":"Y","name":"demo_1","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_10","use_yn":"Y","name":"demo_10","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_100","use_yn":"Y","name":"demo_100","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_1000","use_yn":"Y","name":"demo_1000","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_1001","use_yn":"Y","name":"demo_1001","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_1002","use_yn":"Y","name":"demo_1002","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_1003","use_yn":"Y","name":"demo_1003","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_1004","use_yn":"Y","name":"demo_1004","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_1005","use_yn":"Y","name":"demo_1005","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_1006","use_yn":"Y","name":"demo_1006","email":"mail@user.com"},
     ....
   ]
   ```

****
## 4. SQL Test API
#### 4.1 HTTP Request
   * GET  /cds/sql_test/**
   * POST /cds/sql_test/**
#### 4.2 Headers
   | Name | Description | Type | Required |
   |------|-------------|------|----------|
   | x-cds-authentication | Authorization Token | String | True |

#### 4.3 Body
   | Name | Description | Type | Required |
   |------|-------------|------|----------|
   | dsname | Data Source | String | True |
   | sql | Test SQL | String | True |

#### 4.4 Test Limitation
   * Max Response wait time : 5 Sec.
   * Max Response Result Count : 10 cnt

#### 4.5 Example
   * Request
   ```
   POST /cds/sql_test/ HTTP/1.1
   Host: localhost:8080
   x-cds-authentication: yjWq0Nv5bJOE3sZBZ4sGuK1KNHkD9KTX
   Cache-Control: no-cache
   Content-Type: multipart/form-data;
   
   Content-Disposition: form-data; name="sql"
   select * from demo_users

   Content-Disposition: form-data; name="dsname"
   ds2
   ```
   * Response
   조회 Data 결과와 상관 없이, 10건 이하만 조회 Return 함.
   Query 실행시간이 5초 이상인 경우, Error로 Return 
   ```
   [{"password":"123","role":"administrator","user_id":"admin","use_yn":"Y","name":"권혁민","email":"google@gami.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_1","use_yn":"Y","name":"demo_1","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_10","use_yn":"Y","name":"demo_10","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_100","use_yn":"Y","name":"demo_100","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_1000","use_yn":"Y","name":"demo_1000","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_1001","use_yn":"Y","name":"demo_1001","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_1002","use_yn":"Y","name":"demo_1002","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_1003","use_yn":"Y","name":"demo_1003","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_1004","use_yn":"Y","name":"demo_1004","email":"mail@user.com"},
    {"password":"abc1234","role":"user","user_id":"demo_id_1005","use_yn":"Y","name":"demo_1005","email":"mail@user.com"}]
   ```

****
## 5. Resource Reload API
#### 5.1 HTTP Request
   * GET  /cds/ds_reload/{dsName}
   * POST /cds/ds_reload/{dsName}
#### 5.2 Headers
   | Name | Description | Type | Required |
   |------|-------------|------|----------|
   | x-cds-authentication | Authorization Token | String | True |

#### 5.3 Example
   * Request
   ```
   POST /cds/ds_reload/ds2 HTTP/1.1
   Host: localhost:8080
   x-cds-authentication: yjWq0Nv5bJOE3sZBZ4sGuK1KNHkD9KTX
   Cache-Control: no-cache
   Content-Type: multipart/form-data
   ```
   * Response
   dsName(DataSource Name)에 따른, DB Resource 정보 갱신(Reload & Refresh)후 Connection 개수 Return 
   ```
   reload ok conn_max_cnt =10
   ```
****
## 6. Data Source Check API 
#### 6.1 HTTP Request
   * POST /cds/dsCheck
#### 6.2 Headers
   | Name | Description | Type | Required |
   |------|-------------|------|----------|
   | x-cds-authentication | Authorization Token | String | True |

#### 6.3 Parameters
   | Properties | Description | Type | Required |
   |------------|-------------|------|----------|
   | name | ResourceName (PK) | String | True |

#### 6.4 Example
   * Request
     ```
        POST /cds/dsCheck HTTP/1.1
        Host: localhost:8080
        x-cds-authentication: yjWq0Nv5bJOE3sZBZ4sGuK1KNHkD9KTX
        Cache-Control: no-cache
        Content-Type: multipart/form-data; 
        Content-Disposition: form-data; name="name"
        ds5
     ```
   * Response
     ```
     [
         {
             "name": "ds5",
             "db_url": "jdbc:postgresql://localhost:5432/cds_db",
             "conn_max_cnt": 10,
             "conn_timeout": 5,
             "update_time": "2020-06-15T01:48:34.237+0000",
             "update_user": "admin",
             "biz_dept": "운영그룹 ",
             "biz_name": "나운영 ",
             "user_id": "postgres",
             "passwd": "YM7rfXlxVKgpi/ZENshlHg==",
             "schema_name": "public",
             "db_type": "postgressql"
         }
     ]
     ```

****
## 7. Data Service Check API 
#### 7.1 HTTP Request
   * POST /cds/sqlCheck
#### 7.2 Headers
   | Name | Description | Type | Required |
   |------|-------------|------|----------|
   | x-cds-authentication | Authorization Token | String | True |

#### 7.3 Parameters
   | Properties | Description | Type | Required |
   |------------|-------------|------|----------|
   | api_url | API Service URL(PK) | String | True |

#### 7.4 Example
   * Request
     ```
        POST /cds/sqlCheck HTTP/1.1
        Host: localhost:8080
        x-cds-authentication: yjWq0Nv5bJOE3sZBZ4sGuK1KNHkD9KTX
        Cache-Control: no-cache
        Content-Type: multipart/form-data;
        Content-Disposition: form-data; name="api_url"
        api/v3/getAll
     ```
   * Response
     ```
     [
         {
             "name": "ds5",
             "api_url": "api/v3/getAll",
             "sql": "select 1",
             "result_max_cnt": -1,
             "result_timeout": 5,
             "cache_timeout": -1
         }
     ]
     ```
