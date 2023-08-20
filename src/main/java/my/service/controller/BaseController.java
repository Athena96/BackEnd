package my.service.controller;

import org.springframework.web.bind.annotation.CrossOrigin;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@CrossOrigin(origins = "*")
public class BaseController {
    protected static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_1)
            .build();
    protected static final CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
            .region(Region.US_EAST_1) // Change to your Cognito region
            .build();

    public BaseController() {
        System.out.println("BaseController");
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

            System.out.println("User email: " + email);
            return email;

        } catch (Error e) {
            System.out.println("getUserEmail Error");

            e.printStackTrace();
            throw e;
        }

    }

}
