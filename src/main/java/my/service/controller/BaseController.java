package my.service.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.CrossOrigin;

import my.service.services.DDBService;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@CrossOrigin(origins = "*")
public class BaseController {
    private static final Logger log = LogManager.getLogger(BaseController.class);

    protected final DynamoDbClient dynamoDbClient;
    protected final CognitoIdentityProviderClient cognitoClient;
    protected final DDBService ddbService;

    public BaseController() {
        log.info("BaseController");
        Date startTime = new Date();

        dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();
        cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1) // Change to your Cognito region
                .build();
        ddbService = new DDBService(dynamoDbClient);
        Date endTime = new Date();
        log.info("BaseController Load Time: " + (endTime.getTime() - startTime.getTime()) + "ms");
    }

    protected String getUserEmail(String token) {
        try {
            GetUserRequest getUserRequest = GetUserRequest.builder()
                    .accessToken(token)
                    .build();

            GetUserResponse getUserResponse = cognitoClient.getUser(getUserRequest);

            String email = getUserResponse.userAttributes().stream()
                    .filter(attr -> attr.name().equals("email"))
                    .map(AttributeType::value)
                    .findFirst()
                    .orElse("Email not found");

            log.info("User email: " + email);
            return email;

        } catch (Error e) {
            log.info("getUserEmail Error");

            e.printStackTrace();
            throw e;
        }

    }

}
