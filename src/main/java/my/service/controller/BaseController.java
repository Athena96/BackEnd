package my.service.controller;

import java.util.Base64;

import org.springframework.web.bind.annotation.CrossOrigin;

import my.service.services.DDBService;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

@CrossOrigin(origins = "*")
public class BaseController {
    private static final Logger log = LogManager.getLogger(BaseController.class);

    protected final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();
    protected final DDBService ddbService = new DDBService(dynamoDbClient);

    public BaseController() {
        log.info("BaseController");
    }

    protected String getUserEmail(String idToken) {
        try {
            String[] splitString = idToken.split("\\.");
            String base64EncodedBody = splitString[1];
            Base64.Decoder decoder = Base64.getUrlDecoder();

            String body = new String(decoder.decode(base64EncodedBody));
            JSONObject jsonObj = new JSONObject(body);

            String email = jsonObj.getString("email");
            log.info("User email: " + email);
            return email;

        } catch (Error e) {
            log.info("getUserEmail Error");

            e.printStackTrace();
            throw e;
        }

    }

}
