package com.sjonsson.demo.restassured;

import org.junit.Test;
import static org.junit.Assert.*;
import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sjonsson
 */
public class ProductSearchTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    private static final String GOOGLE_KEY = TestProperties.get("googleKey");
    private static final String GOOGLE_CX = TestProperties.get("googleCx");
    private static final String SEMANTICS3_KEY = TestProperties.get("semantics3Key");
    
    @Test
    public void testGoToWebSiteThatSellsBestDeviceOf2012() {
        
        // Google search for best device of 2012
        // E.g. https://www.googleapis.com/customsearch/v1?q=best+device+of+2012&key=AIzaSyAHwDxBT4e8AadkrHSUxdCyxy8AI0OumFQ&cx=000242167149563501212:0zjofyhcpzm&alt=json
        
        String title =        
        given().
                queryParam("q", "best device of 2012").
                queryParam("alt", "json").
                queryParam("key", GOOGLE_KEY).
                queryParam("cx", GOOGLE_CX).
        expect().
                statusCode(200).
                body("searchInformation.totalResults", not("0")).
        when().
                get("https://www.googleapis.com/customsearch/v1").        
        getBody().jsonPath().get("items[0].title");
        log.debug("title: {}", title);
        
        String productName = title.split(" ")[0] + " " + title.split(" ")[1]; //First two words in title

        // Semantics3 search for merchant that sells device
        // https://www.googleapis.com/shopping/search/v1/public/products?country=US&q=Galaxy%20S3&key=AIzaSyAHwDxBT4e8AadkrHSUxdCyxy8AI0OumFQ&cx=000242167149563501212:0zjofyhcpzm
       
        String productUrl =       
        given().
                log().all().
                header("api_key", SEMANTICS3_KEY).
                queryParam("q", "{\"search\":\"" + productName + "\",\"cat_id\":12181}").
                
        expect().
                log().all().
                statusCode(200).
                body("total_results_count", greaterThan(0)).
                body("results[0].sitedetails[0].url", startsWith("http")). 
        when().
                get("https://api.semantics3.com/test/v1/products").        
        getBody().jsonPath().get("results[0].sitedetails[0].url"); 
        log.debug("productUrl: {}", productUrl);       
        
        // Web request for product site        

        String html =
        expect().
                statusCode(200).
        when().
                get(productUrl).asString();
        
        log.debug("html snipped: {}", html.substring(0,2000).replaceAll("(?m)^ *\r?\n", ""));        
        assertTrue(html.contains(productName));
        
    }

}
