
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ApiTest {
    final static String ROOT = "http://api.currencylayer.com";
    final static String ACCESS_KEY = "?access_key=8752afd6f411f579ccddeff23e9a34f6";
    final static String ENDPOINT_LIVE = "/live";
    final static String ENDPOINT_HISTORICAL = "/historical";
    final static String ENDPOINT_LIVE_PARAMETER = "&currencies=";
    final static String ENDPOINT_HISTORICAL_PARAMETER = "&Date=";
    final static String CURRENCIES = "CAD,EUR,NIS,RUB";
    final static String DATE = "2020-09-12";
    final static String EXPECTED_TERMS = "https://currencylayer.com/terms";
    final static String EXPECTED_PRIVACY = "https://currencylayer.com/privacy";
    final static String EXPECTED_SOURCE = "USD";
    private static final Logger logger= LogManager.getLogger(ApiTest.class);

    @Test
    public void checkEndpointLivePositiveTest() {
        Response response = given().get(ROOT + ENDPOINT_LIVE + ACCESS_KEY);
        response.then().statusCode(200);
        response.then().log().body();

    }

    @Test
    public void checkEndpointLiveNegativeTest() {
        String invalid_endpoint = "/livee";
        Response response = given().get(ROOT + invalid_endpoint + ACCESS_KEY);
        response.then().body(notNullValue()).extract().path("error.code");
        response.then().body("error.code", equalTo(103));
        logger.info("This API Function does not exist.");


    }

    @Test
    public void checkEndpointLiveSuccessKeyNegativeTest() {
        String invalid_access_key = "?access_=ee4f370553da321791d5986e117027";
        Response response = given().get(ROOT + ENDPOINT_LIVE + invalid_access_key);
        response.then().body(notNullValue()).extract().path("error.code");
        response.then().body("error.code", equalTo(101));
        logger.info("User did not supply an access key or supplied an invalid access key.");

    }

    @Test
    public void checkEndpointLiveBodyPositiveTest() {

        Response response = given().get(ROOT + ENDPOINT_LIVE + ACCESS_KEY);

        response.then().body(notNullValue()).extract().path("success");
        response.then().body("success", equalTo(true));

        response.then().body(notNullValue()).extract().path("terms");
        response.then().body("terms", equalTo(EXPECTED_TERMS));

        response.then().body(notNullValue()).extract().path("privacy");
        response.then().body("privacy", equalTo(EXPECTED_PRIVACY));

        response.then().body(notNullValue()).extract().path("source");
        response.then().body("source", equalTo(EXPECTED_SOURCE));

        response.then().body(notNullValue()).extract().path("timestamp");
        response.then().body("timestamp", notNullValue());

        HashMap<String, Integer> currencyData = new HashMap<String, Integer>();
        currencyData = given().get(ROOT + ENDPOINT_LIVE + ACCESS_KEY).then().body(notNullValue()).extract().path("quotes");
        Set<String> currency_key = currencyData.keySet();

        for (String name : currency_key) {
            System.out.println(name);
            given().get(ROOT + ENDPOINT_LIVE + ACCESS_KEY).then().body("quotes." + name, notNullValue());
        }
    }

    @Test
    public void checkEndpointLiveCurrencyParameterPositiveTest() {

        Response response = given().get(ROOT + ENDPOINT_LIVE + ACCESS_KEY + ENDPOINT_LIVE_PARAMETER + CURRENCIES);
        response.then().statusCode(200);
        response.then().log().body();
    }

    @Test
    public void checkEndpointLiveCurrencyParameterNegativeTest() {
        String invalid_currencies = "USDEUR,USDGBP";
        Response response = given().get(ROOT + ENDPOINT_LIVE + ACCESS_KEY + ENDPOINT_LIVE_PARAMETER + invalid_currencies);
        response.then().body(notNullValue()).extract().path("error.code");
        response.then().body("error.code", equalTo(202));
        logger.info("You have provided one or more invalid Currency Codes. [Required format: currencies=EUR,USD,GBP,...]");

    }

    @Test
    public void checkEndpointHistoricalBodyPositiveTest() {

        Response response = given().get(ROOT + ENDPOINT_HISTORICAL + ACCESS_KEY + ENDPOINT_HISTORICAL_PARAMETER + DATE);
        response.then().body(notNullValue()).extract().path("success");
        response.then().body("success", equalTo(true));

        response.then().body(notNullValue()).extract().path("terms");
        response.then().body("terms", equalTo(EXPECTED_TERMS));

        response.then().body(notNullValue()).extract().path("privacy");
        response.then().body("privacy", equalTo(EXPECTED_PRIVACY));

        response.then().body(notNullValue()).extract().path("source");
        response.then().body("source", equalTo(EXPECTED_SOURCE));

        response.then().body(notNullValue()).extract().path("historical");
        response.then().body("historical", equalTo(true));

        response.then().body(notNullValue()).extract().path("date");
        response.then().body("date", notNullValue());

        response.then().body(notNullValue()).extract().path("timestamp");
        response.then().body("timestamp", notNullValue());
    }


    @Test
    public void checkEndpointHistoricalWithoutDateNegativeTest() {

        Response response = given().get(ROOT + ENDPOINT_HISTORICAL + ACCESS_KEY + ENDPOINT_HISTORICAL_PARAMETER);
        response.then().body(notNullValue()).extract().path("error.code");
        response.then().body("error.code", equalTo(301));
        logger.info("You have not specified a date. [Required format: date=YYYY-MM-DD]");

    }


    @Test
    public void checkEndpointHistoricalInvalidDateNegativeTest() {

        String invalid_date = "2020-09-1";
        Response response = given().get(ROOT + ENDPOINT_HISTORICAL + ACCESS_KEY + ENDPOINT_HISTORICAL_PARAMETER + invalid_date);
        response.then().body(notNullValue()).extract().path("error.code");
        response.then().body("error.code", equalTo(302));
        logger.info("You have entered an invalid date. [Required format: date=YYYY-MM-DD]");

    }

    @Test
    public void checkEndpointHistoricalSuccessKeyNegativeTest() {
        String invalid_access_key = "?access_key=8752afd6f411f579ccddeff23e9a34";
        Response response = given().get(ROOT + ENDPOINT_HISTORICAL + invalid_access_key + ENDPOINT_HISTORICAL_PARAMETER + DATE);
        response.then().body(notNullValue()).extract().path("error.code");
        response.then().body("error.code", equalTo(101));
        logger.info("User did not supply an access key or supplied an invalid access key.");

    }

    @Test
    public void checkEndpointHistoricalDateCurrencyParametrPositiveTest() {
        Response response = given().get(ROOT + ENDPOINT_HISTORICAL + ACCESS_KEY + ENDPOINT_HISTORICAL_PARAMETER + DATE + ENDPOINT_LIVE_PARAMETER + CURRENCIES);
        response.then().statusCode(200);
        response.then().log().body();
    }

}

