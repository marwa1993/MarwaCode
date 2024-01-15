import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import io.restassured.response.Response;
import static org.testng.Assert.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;


 

public class AlmosaferAPIPOST {

	public static void main(String[] args) {	
		// TODO Auto-generated method stub
	}
	
	
 
	
	
	private String constructJsonData(String lookupId, String checkIn, String checkOut) {
	    return String.format("{\"searchCriteria\":[{\"lookupTypeId\":2,\"lookupId\":[\"%s\"]}],\"checkIn\":\"%s\",\"checkOut\":\"%s\",\"sortBy\":\"rank\",\"sortOrder\":\"DESC\",\"rankType\":\"dynamic\",\"pageNo\":1,\"pageSize\":10}",
	            lookupId, checkIn, checkOut);
	}
	
	
	private void assertDateFormat(String dateValue, String expectedFormat, String message) {
        String dateFormatRegex = "\\d{4}-\\d{2}-\\d{2}";

        Assert.assertTrue(dateValue.matches(dateFormatRegex), message);
    }


	 	@Test
	    @Parameters({"baseUri", "tokenEndpoint", "cityId", "checkIn", "checkOut", "apiEndpoint"})
	    public void testApiWithToken(String baseUri, String token , String cityId, String checkIn, String checkOut,
	    		 String apiEndpoint) {
		 
	 
	        
	         String jsonData = constructJsonData(cityId, checkIn, checkOut);
             
 
	        
		      Response response = RestAssured.given().log().all()
		                .baseUri(baseUri).log().all()
		                .header("Content-Type", ContentType.JSON)
		                .header("Token", token).log().all()
		                .body(jsonData).log().all()
		                .post(apiEndpoint);
	        
	        // Assertions
	         Assert.assertEquals(response.getStatusCode(), 200, "Unexpected status code");
         
             // Asserting on specific values in the JSON response
            Assert.assertNotNull(response.jsonPath().getString("searchId") , "Unexpected searchId value");
            Assert.assertNotNull(response.jsonPath().getLong("searchIdExpiry"),   "Unexpected searchIdExpiry value");
             
             // Asserting on specific values in the JSON response //test assertion
            assertDateFormat(response.jsonPath().getString("checkIn"), "yyyy-MM-dd", "Unexpected checkIn date format");
            assertDateFormat(response.jsonPath().getString("checkOut"), "yyyy-MM-dd", "Unexpected checkOut date format");
 	        
	        Assert.assertNotNull(response.jsonPath().getInt("totalCount"),  "Unexpected totalCount value");
	        Assert.assertNotNull(response.jsonPath().getInt("pageNo"),  "Unexpected pageNo value");
	        Assert.assertNotNull(response.jsonPath().getInt("pageSize"),  "Unexpected pageSize value");
	        Assert.assertNotNull(response.jsonPath().getString("currencyCode"),  "Unexpected currencyCode value");
         
	        System.out.println("Done! Assertion success TEST num 1");
 		     
		 
		 
	    }
	 
	 

	 
	 
	

}
