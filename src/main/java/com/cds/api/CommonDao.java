package com.cds.api;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import com.zaxxer.hikari.HikariDataSource;

@Repository
public class CommonDao {
	private final Logger logger = LoggerFactory.getLogger(CommonDao.class);

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired
	private RedisTemplate redisTemplate;

	static Map ds_info = new ConcurrentHashMap();
	static Map ds_meta_info = new ConcurrentHashMap();

	public List list(String sql, Map paramMap) {
		return jdbcTemplate.queryForList(sql, paramMap);
	}

	public int update(String sql, Map paramMap) {
		return jdbcTemplate.update(sql, paramMap);
	}

	int execTestSqlmilliseconds = 5 * 1000;
	int exexResult_max_cnt =10;
	public List execTestSql(String dsName, String sql, Map param) {
		Sql2o engine = (Sql2o) ds_info.get(dsName);
		Map ds_meta_map = (Map) ds_meta_info.get(dsName);
		int conn_max_cnt = (int) ds_meta_map.get("conn_max_cnt");
		int conn_timeout = (int) ds_meta_map.get("conn_timeout");
		String db_type = (String) ds_meta_map.get("db_type");
		db_type = db_type.toLowerCase();

		if (engine == null) {
			List alist = new ArrayList();
			alist.add(Collections.singletonMap("error", "No DataSource check " + dsName));
			return alist;
		}
		try (Connection con = engine.open()) {
			con.getJdbcConnection().setNetworkTimeout(Executors.newSingleThreadExecutor(), execTestSqlmilliseconds);

			if (sql.indexOf("#") > -1) {
				sql = DynamicSqlService.getDynamicSQL(sql, param);
			}
			if ("mysql".equals(db_type) || "postgres".equals(db_type)) {
				sql = sql + " LIMIT " + exexResult_max_cnt;
			} else if ("mssql".equals(db_type)) {
				String topQuery = "SELECT  TOP " + exexResult_max_cnt + " ";
				int lio = sql.toUpperCase().indexOf("SELECT");
				sql = topQuery + sql.substring(lio + 6);
				logger.debug("mssql topQuery");
				logger.debug(sql);
			} else if ("oracle".equals(db_type)) {
				String rownumQuery = "SELECT * FROM ( " + sql + "  )  WHERE ROWNUM <= " + exexResult_max_cnt;
				sql = rownumQuery;
				logger.debug("oracle rownumQuery");
				logger.debug(sql);
			}
			Query query = con.createQuery(sql);
			Iterator<String> keys = param.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				query.addParameter(key, param.get(key));
			}
			return query.executeAndFetchTable().asList();
		} catch (Exception e) {
			List alist = new ArrayList();
			alist.add(Collections.singletonMap(dsName + "_error", e.getMessage()));
			return alist;
		}
//		NamedParameterJdbcTemplate engine  = (NamedParameterJdbcTemplate) ds_info.get(dsName);
//
//		 engine.getJdbcTemplate().setQueryTimeout(10);
//		 engine.getJdbcTemplate().setMaxRows(10);
//		 List lt =  engine.queryForList(sql, param);
//		 return lt;
	}

	private void setValueToCache (String cacheKey , List list , int cache_timeout) {
		try {
			ValueOperations<String, Object> vop = redisTemplate.opsForValue();
			Duration duration = Duration.ofSeconds(cache_timeout);
			vop.set(cacheKey, list, duration);
 		} catch(Exception e) {
			logger.error("set cache Redis not work" , e);
		}
	}

	private Map  getValueFromCache (String cacheKey) {
		Map returnMap = new HashMap();
		try {
			ValueOperations<String, Object> vop = redisTemplate.opsForValue();
			List lt = (List) vop.get(cacheKey);
			returnMap.put ("success" , lt);
			return  returnMap;
 		} catch(Exception e) {
			logger.error("Redis not work" , e);
			returnMap.put ("fail" , Boolean.TRUE);
			return  returnMap;
		}
	}
	public List exec(String query_id, Map param) throws SQLException {
		long start = System.currentTimeMillis();
		Map item = getQuery(query_id);
		String sql = (String) item.get("sql");
		int result_max_cnt = Integer.parseInt(item.get("result_max_cnt").toString());
		int result_timeout = Integer.parseInt(item.get("result_timeout").toString());
		int cache_timeout = Integer.parseInt(item.get("cache_timeout").toString());

		String ds_name = (String) item.get("name");
		Map ds_meta_map = (Map) ds_meta_info.get(ds_name);
		int conn_max_cnt = (int) ds_meta_map.get("conn_max_cnt");
		int conn_timeout = (int) ds_meta_map.get("conn_timeout");
		String db_type = (String) ds_meta_map.get("db_type");
		db_type = db_type.toLowerCase();
		String key = "";
		String cacheError = "";
		if (cache_timeout > 0) {
			key = getCacheKey(query_id, param);
			Map resultMap  = getValueFromCache(key);

			if (resultMap.get("fail") == Boolean.TRUE) {
				cacheError = "cacheError";
			} else {
				List result = (List) resultMap.get("success");
				if ( result != null ) {
					long finish = System.currentTimeMillis();
					long timeElapsed = finish - start;
					logger.debug("timeElapsed = " + timeElapsed);
					logger.info("query_id={},timeElapsed={},size={},result_max_cnt={},result_timeout={},cache_timeout={},cachYn=Y,ds_name={},conn_max_cnt={},conn_timeout={}" ,
							query_id, timeElapsed, result.size(), result_max_cnt, result_timeout, cache_timeout, ds_name, conn_max_cnt, conn_timeout );
					//dbLog(query_id, timeElapsed, result.size(), result_max_cnt, result_timeout, cache_timeout, "Y", ds_name,
					//		conn_max_cnt, conn_timeout);
					logger.debug("cache return");
					return result;
				}
			}
		}

		// NamedParameterJdbcTemplate engine = (NamedParameterJdbcTemplate)
		// ds_info.get(ds_name);
		Sql2o engine = (Sql2o) ds_info.get(ds_name);

		try (Connection con = engine.open()) {
			if (result_timeout > 0) {
				int milliseconds = result_timeout * 1000;
				con.getJdbcConnection().setNetworkTimeout(Executors.newSingleThreadExecutor(), milliseconds);
			}
			if (sql.indexOf("#") > -1) {
				sql = DynamicSqlService.getDynamicSQL(sql, param);
			}
			if (result_max_cnt > 0) {
				if ("mysql".equals(db_type) || "postgres".equals(db_type)) {
					sql = sql + " LIMIT " + result_max_cnt;
				} else if ("mssql".equals(db_type)) {
					String topQuery = "SELECT  TOP " + result_max_cnt + " ";
					int lio = sql.toUpperCase().indexOf("SELECT");
					sql = topQuery + sql.substring(lio + 6);
					logger.debug("mssql topQuery");
					logger.debug(sql);
				} else if ("oracle".equals(db_type)) {
					String rownumQuery = "SELECT * FROM ( " + sql + "  )  WHERE ROWNUM <= " + result_max_cnt;
					sql = rownumQuery;
					logger.debug("oracle rownumQuery");
					logger.debug(sql);
				}

			}
			Query query = con.createQuery(sql) ;
			Iterator<String> paramKeys = param.keySet().iterator();
			while (paramKeys.hasNext()) {
				String id = paramKeys.next();
				query.addParameter(id, param.get(id));
			}

			List lt = query.executeAndFetchTable().asList();
//	    if(result_timeout > 0){
//	    	engine.getJdbcTemplate().setQueryTimeout(result_timeout);
//
//		}
//	    if(result_max_cnt > 0){
//	    	engine.getJdbcTemplate().setMaxRows(result_max_cnt);
//	    }
//	    List lt =  engine.queryForList(sql, param);
			logger.debug("result size = " + lt.size());
			if ("".equals(cacheError)  && cache_timeout > 0 && lt.size() > 0 ) {
				setValueToCache(key, lt, cache_timeout);
			}
			long finish = System.currentTimeMillis();
			long timeElapsed = finish - start;
			logger.debug("timeElapsed = " + timeElapsed);
			logger.info("query_id={},timeElapsed={},size={},result_max_cnt={},result_timeout={},cache_timeout={},cachYn=N,ds_name={},conn_max_cnt={},conn_timeout={}" ,
					query_id, timeElapsed, lt.size(), result_max_cnt, result_timeout, cache_timeout, ds_name, conn_max_cnt, conn_timeout );
//			dbLog(query_id, timeElapsed, lt.size(), result_max_cnt, result_timeout, cache_timeout, "N", ds_name,
//					conn_max_cnt, conn_timeout);
			return lt;
		}
//		catch (Exception e) {
//			logger.error(query_id + ":" , e);
//			//dbErrLog(query_id, e.getMessage());
//			List alist = new ArrayList();
//			alist.add(Collections.singletonMap(query_id + "_error", e.getMessage()));
//			return alist;
//		}
	}

	private String getCacheKey(String query_id, Map param) {
		String key = query_id;
		key = key.concat("_" + param.toString().hashCode());
		return key;
	}

	public Map getQuery(String apiUrl) {
		List lt = jdbcTemplate.queryForList("SELECT sql,result_max_cnt,result_timeout, name , cache_timeout "
				+ "FROM data_service WHERE api_url = :key", Collections.singletonMap("key", apiUrl));
		return (Map) lt.get(0);
	}

	public Map getDS(String name) {
		List lt = jdbcTemplate
				.queryForList("SELECT db_url, conn_max_cnt, conn_timeout , user_id , passwd , schema_name , db_type"
						+ " FROM data_source WHERE name = :key", Collections.singletonMap("key", name));
		return (Map) lt.get(0);
	}

	public boolean getDSStatus(String dsName) {
		try {
		Map item = getDS(dsName);

		String new_db_url = (String) item.get("db_url");
		int conn_max_cnt = Integer.parseInt(item.get("conn_max_cnt").toString());
		int conn_timeout = Integer.parseInt(item.get("conn_timeout").toString());
		String user_id = (String) item.get("user_id");
		String passwd = (String) item.get("passwd");
		passwd = DbPassword.decAES(passwd) ;
		String schemaName = (String) item.get("schema_name");
		String db_type = (String) item.get("db_type");

		String sql = "select 1";

		HikariDataSource dataSource = new HikariDataSource();
//		dataSource.setConnectionTimeout(conn_timeout * 1000);
		dataSource.setValidationTimeout(conn_timeout * 1000);
		dataSource.setMaximumPoolSize(conn_max_cnt);
		dataSource.setJdbcUrl(new_db_url);
		if (user_id != null && !"".equals(user_id)) {
			dataSource.setUsername(user_id);
		}
		if (passwd != null && !"".equals(passwd)) {
			dataSource.setPassword(passwd);
		}
		if (schemaName != null && !"".equals(schemaName)) {
			dataSource.setSchema(schemaName);
		}

		Sql2o engine = new Sql2o(dataSource);
		Connection con = engine.open();
//		try (Connection con = engine.open()) {
			Query query = con.createQuery(sql);

			List lt = query.executeAndFetchTable().asList();
			if(((Map)lt.get(0)).size() > 0)
				return true;
		} catch(Exception e) {
			logger.error("Datasource is not working .." , e);
		}

		return false;
	}

	public int setDsInfo(String dsName) {
		Map item = getDS(dsName);

		String new_db_url = (String) item.get("db_url");
		int conn_max_cnt = Integer.parseInt(item.get("conn_max_cnt").toString());
		int conn_timeout = Integer.parseInt(item.get("conn_timeout").toString());
		String user_id = (String) item.get("user_id");
		String passwd = (String) item.get("passwd");
		passwd = DbPassword.decAES(passwd) ;
		String schemaName = (String) item.get("schema_name");
		String db_type = (String) item.get("db_type");

		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setConnectionTimeout(conn_timeout * 1000);
//		dataSource.setValidationTimeout(conn_timeout * 1000);
		dataSource.setMaximumPoolSize(conn_max_cnt);
		dataSource.setJdbcUrl(new_db_url);
		if (user_id != null && !"".equals(user_id)) {
			dataSource.setUsername(user_id);
		}
		if (passwd != null && !"".equals(passwd)) {
			dataSource.setPassword(passwd);
		}
		if (schemaName != null && !"".equals(schemaName)) {
			dataSource.setSchema(schemaName);
		}
		// NamedParameterJdbcTemplate newOne = new
		// NamedParameterJdbcTemplate(dataSource);
		Sql2o newOne = new Sql2o(dataSource);
		ds_info.put(dsName, newOne);

		Map ds_meta = new HashMap();
		ds_meta.put("conn_max_cnt", conn_max_cnt);
		ds_meta.put("conn_timeout", conn_timeout);
		ds_meta.put("db_type", db_type);
		ds_meta_info.put(dsName, ds_meta);

		return conn_max_cnt;
	}

//	private final String dbLogErrSQL = "INSERT INTO data_log (api_url,  msg , update_time ) VALUES (:api_url, :msg , now())";
//
//	public void dbErrLog(String api_url, String msg) {
//		Map param = new HashMap();
//		param.put("api_url", api_url);
//		param.put("msg", msg);
//
//		update(dbLogErrSQL, param);
//	}

//	private final String dbLogSQL = "INSERT INTO data_log (api_url, el_time, result_cnt, result_max_cnt, "
//			+ "result_timeout, cache_timeout, cache_yn, ds_name, conn_max_cnt, " + "conn_timeout, update_time) VALUES "
//			+ "(:api_url, :el_time, :result_cnt, :result_max_cnt, "
//			+ ":result_timeout, :cache_timeout, :cache_yn, :ds_name, :conn_max_cnt, :conn_timeout , now())";
//
//	public void dbLog(String api_url, long el_time, int result_cnt, int result_max_cnt, int result_timeout,
//			int cache_timeout, String cache_yn, String ds_name, int conn_max_cnt, int conn_timeout) {
//		Map param = new HashMap();
//		param.put("api_url", api_url);
//		param.put("el_time", el_time);
//		param.put("result_cnt", result_cnt);
//		param.put("result_max_cnt", result_max_cnt);
//		param.put("result_timeout", result_timeout);
//		param.put("cache_timeout", cache_timeout);
//		param.put("cache_yn", cache_yn);
//		param.put("ds_name", ds_name);
//		param.put("conn_max_cnt", conn_max_cnt);
//		param.put("conn_timeout", conn_timeout);
//		update(dbLogSQL, param);
//	}


	@PostConstruct
	public void init() {
		Map paramMap =new HashMap();
		List lt = list("SELECT name FROM data_source " , paramMap);
		int ltSize = lt.size();
		for (int i = 0; i < ltSize; i++) {
			Map map = (Map) lt.get(i);
			setDsInfo(map.get("name").toString());
		}
        //		setDsInfo("ds1");
		// data_source 에 name 을 몽땅 읽어서 init 한번 해주세요 by 권영오
	}
}
//
//if ("postgres".equals(db_type) ) {
//	String timeoutSQL = "SET statement_timeout TO " + (result_timeout * 1000);
//	con.createQuery(timeoutSQL).executeUpdate();
//}
//if ("mysql".equals(db_type) ) {
//	/*+ MAX_EXECUTION_TIME(1000) */
//	String timeoutHint =  "SELECT  /*+ MAX_EXECUTION_TIME(" + (result_timeout * 1000) + ") */  ";
//	sql = sql.toUpperCase();
//	sql = sql.replaceFirst("SELECT ", timeoutHint);
//	logger.debug("mysql timeoutHint");
//	logger.debug(sql);
//}
////SET LOCK_TIMEOUT 1800;
//if ("mssql".equals(db_type) ) {
//	String timeoutSQL = "SET LOCK_TIMEOUT " + (result_timeout * 1000) + ";";
//	sql = timeoutSQL +  sql;
//	logger.debug("mssql timeoutSQL");
//	logger.debug(sql);
//}
