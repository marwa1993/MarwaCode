import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class AlmosaferAPIGET {
	
	 private String constructJsonData2(String checkIn, String checkOut, String adultsCount, List<Integer> kidsAges, String placeId) {
		    return String.format("{\"checkIn\":\"%s\",\"checkOut\":\"%s\",\"roomsInfo\":[{\"adultsCount\":%s,\"kidsAges\":%s}],\"searchInfo\":null,\"crossSellDetail\":null,\"chargeCode\":null,\"placeId\":\"%s\"}",
		            checkIn, checkOut, adultsCount, kidsAges.toString(), placeId);
		}

	
	 //convert string to list ,,, 1,2,3 ---> [1,2,3]
	  private List<Integer> parseAges(String ages) {
	        return Arrays.asList(ages.split(",")).stream()
	                .map(Integer::parseInt)
	                .toList();
	    }
	  
	  
	  private boolean validateDateFormat(String date, String format) {
		    try {
		        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
		        LocalDateTime.parse(date, dateTimeFormatter);
		        return true;
		    } catch (DateTimeParseException e) {
		        return false;
		    }
		}
	  
	 @Test
	    @Parameters({"baseUriGet", "tokenEndpointGet", "adultsCountGet", "childrenAgesGet", "checkInGet", "checkOutGet", "placeIdGet",  "apiEndpointGet","apiEndpointResultGet"})
	    public void testApiWithToken2(String baseUri, String token , String adultsCount,  String childrenAges, String checkIn, String checkOut,
	    		String placeId, String apiEndpoint, String apiEndpointResult) {
		 
		 //convert string to list
		 List<Integer> ages = parseAges(childrenAges);
		 
		 String jsonData = constructJsonData2(checkIn, checkOut, adultsCount, ages, placeId);
		  
	      Response response = RestAssured.given()
	                .baseUri(baseUri)
	                .header("Content-Type", ContentType.JSON)
	                .header("Token", token)
	                .body(jsonData)
	                .post(apiEndpoint);

	        String responseBody = response.getBody().asString();
	        
	        if (response.getStatusCode() == 200) {
	            // Extract 'sid' from the response JSON
	            String sessionId = response.jsonPath().getString("sId"); 
	               
	            Assert.assertNotNull(sessionId, "sId is null");
	            
	        
			 
	           Response searchResult =  getStatus(sessionId, apiEndpointResult, token);
	           String status = searchResult.jsonPath().getString("searchStatus");
	            if(searchResult != null) { 
	        	  //assertion
	             
	            	 
	            	Assert.assertNotNull(searchResult.jsonPath().getString("searchStatus"), "searchStatus field is expected in the response");
	            	  
	            	//Check if "totalResults" exists
	            	Assert.assertNotNull(searchResult.jsonPath().get("totalResults"), "totalResults field is expected in the response");
	            	 
	            
	            	// Check if "supplierMeta" exists
	            	Assert.assertNotNull(searchResult.jsonPath().get("supplierMeta"), "supplierMeta field is expected in the response");
	            	 
	            	// Check if "searchResults" exists
	            	Assert.assertNotNull(searchResult.jsonPath().get("searchResults"), "searchResults field is expected in the response");
	            	 
	            	// Check if "sessionCreatedAt" exists and is a valid date
	            	Object sessionCreatedAt = searchResult.jsonPath().get("sessionCreatedAt");
	            	Assert.assertNotNull(sessionCreatedAt, "sessionCreatedAt field is expected in the response");
	            	Assert.assertTrue(validateDateFormat(sessionCreatedAt.toString(), "yyyy-MM-dd HH:mm:ss"), "Invalid date format for sessionCreatedAt");
	            	 
	            	// Check if "sessionExpiredAt" exists and is a valid date
	            	Object sessionExpiredAt = searchResult.jsonPath().get("sessionExpiredAt");
	            	Assert.assertNotNull(sessionExpiredAt, "sessionExpiredAt field is expected in the response");
	            	Assert.assertTrue(validateDateFormat(sessionExpiredAt.toString(), "yyyy-MM-dd HH:mm:ss"), "Invalid date format for sessionExpiredAt");
	             	            	 
	            	// Check if "sId" exists
	            	Assert.assertNotNull(searchResult.jsonPath().get("sId"), "sId field is expected in the response");


	               System.out.println("All assertions passed successfully! TEST num2");
	        	   
	        	   
	            }
	            
	            
	        } else {
	            System.out.println("Failed to create session. Status code: " + response.getStatusCode());
	           // return null;
	        } 
	    }
	
	 private static Response getStatus(String sessionId, String BASE_URL, String token) {
		 
		 
	        Response response = RestAssured.given()
	                .baseUri(BASE_URL)
	                .header("Token", token)
	                .get(sessionId);

	        String responseBody = response.getBody().asString(); 

	        if (response.getStatusCode() == 200) {
	            // Extract 'statusVal' from the response JSON
	            return response;
	        } else {
	            System.out.println("Failed to get status. Status code: " + response.getStatusCode());
	            return null;
	        }
	    }
	
	
	 }
