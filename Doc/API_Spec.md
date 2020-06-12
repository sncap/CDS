# RESTful API Specification

****
## 1. Data API Service
#### 1.1 HTTP Request
   * GET  /api/**
   * POST /api/**
#### 1.2 Headers
   | Name | Description | Type | Required |
   |------|-------------|------|----------|
   | x-cds-authentication | Authorization Token | String | True |
   | Content-Type | application/json | String | True |
#### 1.3 Parameters
   Depend on follow API URL(Query)

#### 1.4 Example
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
## 2. SQL Test API
#### 1.1 HTTP Request
   * GET  /cds/sql_test/**
   * POST /cds/sql_test/**
#### 1.2 Headers
   | Name | Description | Type | Required |
   |------|-------------|------|----------|
   | x-cds-authentication | Authorization Token | String | True |
   | Content-Type | multipart/form-data | String | True |
#### 1.3 Body
   | Name | Description | Type | Required |
   |------|-------------|------|----------|
   | dsname | Data Source | String | True |
   | sql | Test SQL | String | True |

#### 1.4 Test Limitation
   * Max Response wait time : 5 Sec.
   * Max Response Result Count : 10 cnt

#### 1.5 Example
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
## 3. Resource Reload API
#### 1.1 HTTP Request
   * GET  /cds/ds_reload/{dsName}
   * POST /cds/ds_reload/{dsName}
#### 1.2 Headers
   | Name | Description | Type | Required |
   |------|-------------|------|----------|
   | x-cds-authentication | Authorization Token | String | True |
   | Content-Type | multipart/form-data | String | True |

#### 1.3 Example
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

