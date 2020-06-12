package com.cds.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CdsUtil {
	public static Map requserMap2Map(Map<String, String[]> rmap) {
		Map param = new HashMap();
		for (String key : rmap.keySet()) {
			String[] vals = rmap.get(key);
			param.put(key, vals[0]);
		}
		return param;
	}

	public static Map<String, List<String>> splitQuery(String url) throws UnsupportedEncodingException {
		final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
		final String[] pairs = url.split("&");
		for (String pair : pairs) {
			final int idx = pair.indexOf("=");
			final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
			if (!query_pairs.containsKey(key)) {
				query_pairs.put(key, new LinkedList<String>());
			}
			//? URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
			final String value = idx > 0 && pair.length() > idx + 1
					? URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
					: null;
			query_pairs.get(key).add(value);
		}
		return query_pairs;
	}
    
}
