package com.cds.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerMapping;


@RestController
public class CommonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CommonDao commdao;

	final String dsSelect = "SELECT name FROM data_source  WHERE name = :name";
	final String dsInsert = "INSERT INTO data_source\n" +
			"(name, db_url, conn_max_cnt, conn_timeout, update_time, update_user, "+
			"biz_dept, biz_name ,user_id , passwd  , schema_name , db_type)\n" +
			"VALUES(:name, :db_url, :conn_max_cnt, :conn_timeout, now(), :update_user,"+
			" :biz_dept, :biz_name,:user_id , :passwd, :schema_name , :db_type)\n";
	final String dsUpdate = "UPDATE data_source SET db_url = :db_url,  conn_max_cnt  = :conn_max_cnt,  conn_timeout =:conn_timeout , \n" +
			"update_time = now() , update_user=:update_user , biz_dept=:biz_dept , biz_name=:biz_name ,"+
			" user_id=:user_id , passwd=:passwd , schema_name=:schema_name , db_type = :db_type where name = :name ";
	@RequestMapping(value="/cds/ds_upsert",  method= {RequestMethod.GET , RequestMethod.POST} )
	public String dataSourceUpsert(HttpServletRequest request) throws UnsupportedEncodingException {
		if(tokenValidator(request)) {
			Map<String, String[]> parameters = request.getParameterMap();
			Map paramMap = CdsUtil.requserMap2Map(parameters);
			//logger.debug(request.getQueryString());
			//Map paramMap = CdsUtil.splitQuery(request.getQueryString());
			logger.debug(paramMap.toString());
			List lt = commdao.list(dsSelect, paramMap);
			int count =0;
			if (lt.size() > 0) {
				count = commdao.update(dsUpdate, paramMap);
			} else {
				count = commdao.update(dsInsert, paramMap);
			}
			return "Data source update row " + count;
		} else {
			// token error
			throw new SecurityException(HttpStatus.UNAUTHORIZED,"Token Key is invalid");
//			Map result_error = new ConcurrentHashMap();
//			result_error.put("Error", "UNAUTHORIZED");
//			List<Map> result = new ArrayList<>();
//			result.add(result_error);
//
//			return result.toString();
		}
	}

	final String sqlSelect = "SELECT api_url FROM data_service  WHERE api_url = :api_url";
	final String sqlInsert =  "INSERT INTO data_service\n" +
			"(name, api_url, sql, result_max_cnt, result_timeout ,cache_timeout )\n" +
			"VALUES(:name, :api_url, :sql, :result_max_cnt, :result_timeout ,:cache_timeout)";

	final String sqlUpdate = 	"UPDATE data_service SET name = :name,  sql  = :sql,  result_max_cnt =:result_max_cnt ,\n" +
			"result_timeout=:result_timeout, cache_timeout=:cache_timeout WHERE api_url = :api_url ";

	@RequestMapping(value="/cds/sql_upsert",  method= {RequestMethod.GET , RequestMethod.POST} )
	public String sqlUpsert(HttpServletRequest request) {
		if(tokenValidator(request)) {
			Map<String, String[]> parameters = request.getParameterMap();


			Map paramMap = CdsUtil.requserMap2Map(parameters);
			logger.debug(paramMap.toString());
			List lt = commdao.list(sqlSelect, paramMap);
			int count =0;
			if (lt.size() > 0) {
				count = commdao.update(sqlUpdate, paramMap);
			} else {
				count = commdao.update(sqlInsert, paramMap);
			}
			return "Data service update row " + count;
		} else {
			// token error
			throw new SecurityException(HttpStatus.UNAUTHORIZED,"Token Key is invalid");
//			Map result_error = new ConcurrentHashMap();
//			result_error.put("Error", "UNAUTHORIZED");
//			List<Map> result = new ArrayList<>();
//			result.add(result_error);
//
//			return result.toString();
		}
	}

	@RequestMapping(value="/api/**",  method= {RequestMethod.GET , RequestMethod.POST} )
	@ResponseStatus(value = HttpStatus.OK)
	public List<Map> callApi(HttpServletRequest request) throws UnsupportedEncodingException, SQLException   {
		if (tokenValidator(request)) {
			// TODO ACL Function Add

			try{
				// ~ TODO
				String api_url = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
				api_url = api_url.substring(1);
				logger.debug(request.getQueryString());

				//Map paramMap = CdsUtil.splitQuery(request.getQueryString());
				Map paramMap = CdsUtil.requserMap2Map(request.getParameterMap());
				logger.debug(paramMap.toString());
				List returnList = commdao.exec(api_url, paramMap);
				//
				return returnList;
			} catch (Exception e) {
				throw new SecurityException(HttpStatus.INTERNAL_SERVER_ERROR,"API Parameter is invalid");
//				logger.error("API Rest Error", e);
//				Map result_error = new ConcurrentHashMap();
//				result_error.put("Error", e.getMessage());
//				result_error.put("Cause", e.getCause());
//				List<Map> result = new ArrayList<>();
//				result.add(result_error);
//
//				return result;
			}
		} else {
			// token error
			throw new SecurityException(HttpStatus.UNAUTHORIZED,"Token Key is invalid");
//			Map result_error = new ConcurrentHashMap();
//			result_error.put("Error", "UNAUTHORIZED");
//			List<Map> result = new ArrayList<>();
//			result.add(result_error);
//
//			return result;
		}
	}


	@RequestMapping(value="/cds/ds_reload/{dsName}",  method= {RequestMethod.GET , RequestMethod.POST} )
	public String dsReload(@PathVariable String dsName, HttpServletRequest request) {
		if (tokenValidator(request)) {
			int connMaxCnt = commdao.setDsInfo(dsName);
			return "reload ok conn_max_cnt =" + connMaxCnt;
		} else {
			// token error
			throw new SecurityException(HttpStatus.UNAUTHORIZED,"Token Key is invalid");
//			Map result_error = new ConcurrentHashMap();
//			result_error.put("Error", "UNAUTHORIZED");
//			List<Map> result = new ArrayList<>();
//			result.add(result_error);
//
//			return result.toString();
		}
	}

	@RequestMapping(value="/cds/sql_test",  method= {RequestMethod.GET , RequestMethod.POST} )
	public List<Map> sqlTest(HttpServletRequest request) {
		Map paramMap = CdsUtil.requserMap2Map(request.getParameterMap());

		String dsName = (String) paramMap.get("dsname");
		paramMap.remove("dsname");
		String sql = (String) paramMap.get("sql");
		paramMap.remove("sql");
		List returnlist = commdao.execTestSql(dsName, sql, paramMap);
		return returnlist;
	}

	@RequestMapping(value="/cds/listallds",  method= {RequestMethod.GET , RequestMethod.POST} )
	public List<Map> listallds(HttpServletRequest request) {
		if (tokenValidator(request)) {
			Map paramMap = CdsUtil.requserMap2Map(request.getParameterMap());

			List returnlist = commdao.list("select  * from data_source" , paramMap);
			return returnlist;
		} else {
			throw new SecurityException(HttpStatus.UNAUTHORIZED,"Token Key is invalid");
//			Map result_error = new ConcurrentHashMap();
//			result_error.put("Error", "UNAUTHORIZED");
//			List<Map> result = new ArrayList<>();
//			result.add(result_error);
//
//			return result;
		}
	}


	@RequestMapping(value="/cds/listallsql",  method= {RequestMethod.GET , RequestMethod.POST} )
	public List<Map> listallsql(HttpServletRequest request) {
		if (tokenValidator(request)) {
			Map paramMap = CdsUtil.requserMap2Map(request.getParameterMap());

			List returnlist = commdao.list("select  * from data_service" , paramMap);
			return returnlist;
		} else {
			throw new SecurityException(HttpStatus.UNAUTHORIZED,"Token Key is invalid");
//			Map result_error = new ConcurrentHashMap();
//			result_error.put("Error", "UNAUTHORIZED");
//			List<Map> result = new ArrayList<>();
//			result.add(result_error);
//
//			return result;
		}
	}

	@RequestMapping(value="/cds/dsStatus/{dsName}",  method= {RequestMethod.GET} )
	public String dsHealthCheck(@PathVariable String dsName) {
		boolean result = commdao.getDSStatus(dsName);
		if(result) {
			return "[ " + dsName + " ] Data Source Health is Working";
		}
//		throw new IllegalStateException("Data Source Error : " + dsName);
		throw new SecurityException(HttpStatus.INTERNAL_SERVER_ERROR,"Date Source [ "+ dsName + " ]  is invalid");
	}

	@RequestMapping(value="/cds/dsCheck", method= {RequestMethod.POST})
	public List<Map> dsCheck(HttpServletRequest request) {
		if (tokenValidator(request)) {
			Map paramMap = CdsUtil.requserMap2Map(request.getParameterMap());

			List returnlist = commdao.list("select  * from data_source WHERE name = :name", paramMap);
			return returnlist;
		} else {
			throw new SecurityException(HttpStatus.UNAUTHORIZED,"Token Key is invalid");
//			Map result_error = new ConcurrentHashMap();
//			result_error.put("Error", "UNAUTHORIZED");
//			List<Map> result = new ArrayList<>();
//			result.add(result_error);
//
//			return result;
		}
	}

	@RequestMapping(value="/cds/sqlCheck", method= {RequestMethod.POST})
	public List<Map> sqlCheck(HttpServletRequest request) {
		if (tokenValidator(request)) {
			Map paramMap = CdsUtil.requserMap2Map(request.getParameterMap());

			List returnlist = commdao.list("select  * from data_service WHERE api_url = :api_url", paramMap);
			return returnlist;
		} else {
			throw new SecurityException(HttpStatus.UNAUTHORIZED,"Token Key is invalid");
//			Map result_error = new ConcurrentHashMap();
//			result_error.put("Error", "UNAUTHORIZED");
//			List<Map> result = new ArrayList<>();
//			result.add(result_error);
//
//			return result;
		}

	}

	@ControllerAdvice
	public class SecurityControllerAdvice {

		@ExceptionHandler(SecurityException.class)
		@ResponseBody
//		@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//		public SecurityResponse handleSecurityException(SecurityException se) {
//			SecurityResponse response = new SecurityResponse(se.getStatus(), se.getMessage());
//			return response;
//		}
		public ResponseEntity<Object> handleSecurityException(SecurityException se, WebRequest request) {
			Map<String, Object> response = new HashMap<>();

			response.put("message", se.getMessage());
			return new ResponseEntity<>(response, se.getStatus());
		}
	}

	private boolean tokenValidator(HttpServletRequest request) {
		//		String authToken = request.getHeader("Authorization");
		String DEP_token = request.getHeader("x-dep-ticket");
		String CDS_token = request.getHeader("x-cds-authentication");
		String Tokens = "yjWq0Nv5bJOE3sZBZ4sGuK1KNHkD9KTX";

		if((DEP_token != null)) {
			return true;
		} else if((CDS_token != null) && CDS_token.equals(Tokens)){
			return true;
		}
		return false;
	}

	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request){
		// Load file as Resource
		Resource resource = null;
		Path filePath = Paths.get("/data/log/cds",fileName);
		String path = request.getHeader("file-path");

		if(path!=null) {
			filePath = Paths.get(path,fileName);
		}

		System.out.println("Begin to Download file...");

		try {
			resource = new UrlResource(filePath.toUri());

			if(!resource.exists()) {
				throw new SecurityException(HttpStatus.INTERNAL_SERVER_ERROR, filePath + " 파일을 찾을 수 없습니다.");
			}
		}catch(MalformedURLException e) {
			throw new SecurityException(HttpStatus.INTERNAL_SERVER_ERROR, filePath + " 파일을 찾을 수 없습니다.");
		}

		System.out.println("Download file is : " +resource.toString());

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if(contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}


}
