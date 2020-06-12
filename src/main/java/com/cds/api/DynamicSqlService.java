package com.cds.api;

import java.io.StringWriter;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

@Service
public class DynamicSqlService {

	static VelocityEngine velocityEngine = new VelocityEngine();

	static public String getDynamicSQL(String sql, Map paramMap) {

		StringWriter w = new StringWriter();
		VelocityContext context = new VelocityContext(paramMap);
		velocityEngine.evaluate(context, w, "DynamicSQL", sql);
		return w.getBuffer().toString();

	}

	@PostConstruct
	public void init() {
		velocityEngine.init();
	}
}
