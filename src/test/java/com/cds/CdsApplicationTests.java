package com.cds;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


public class CdsApplicationTests {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    String BASE_URL= "http://localhost:8080";

    RestTemplate restTemplate;

    @Before
    public void init() {
        restTemplate = new RestTemplate();
    }

    @Test
    public void testPostParams() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        MultiValueMap<String, String> parameters= new LinkedMultiValueMap<String, String>();
        parameters.add("a", "aaaa");
        parameters.add("b", "bbbb");

        ResponseEntity<String> response = restTemplate.postForEntity( BASE_URL + "/posttest" , parameters , String.class );
        logger.debug("returnString: =  {}", response.getBody());
    }

    @Test
    public void testPostParamsExchange() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        MultiValueMap<String, String> parameters= new LinkedMultiValueMap<String, String>();
        parameters.add("a", "aaaa");
        parameters.add("b", "bbbb");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(parameters, headers);

        ResponseEntity<String> response = restTemplate.exchange( BASE_URL + "/posttest" , HttpMethod.POST, requestEntity, String.class );
        logger.debug("returnString: =  {}", response.getBody());
    }

}
