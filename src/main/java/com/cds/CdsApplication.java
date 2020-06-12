package com.cds;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;

import com.cds.api.DbPassword;
import com.zaxxer.hikari.HikariDataSource;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class CdsApplication {
	
	public static void main(String[] args) {
		
		//System.setProperty("org.apache.catalina.connector.CoyoteAdapter.ALLOW_BACKSLASH", "true");
		//System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
		SpringApplication.run(CdsApplication.class, args);
	}
	@Bean
    public DataSource datasource(DataSourceProperties dataSourceProperties) {
        HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        dataSource.setMaximumPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        String password = dataSource.getPassword();
        dataSource.setPassword(DbPassword.decAES(password));
        return dataSource;
   }
//	 public void configurePathMatch(PathMatchConfigurer configurer) {
//	        UrlPathHelper urlPathHelper = new UrlPathHelper();
//	        urlPathHelper.setUrlDecode(false);
//	        configurer.setUrlPathHelper(urlPathHelper);
//	    }
//	@Bean
//	public HttpFirewall allowEncodedParamsFirewall() {
//	    StrictHttpFirewall firewall = new StrictHttpFirewall();
//	    firewall.setAllowUrlEncodedPercent(true);    
//	    return firewall;
//	}
}