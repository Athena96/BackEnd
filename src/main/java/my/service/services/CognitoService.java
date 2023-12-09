package my.service.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;

public class CognitoService {
    private static final Logger log = LogManager.getLogger(CognitoService.class);

    protected final CognitoIdentityProviderClient cognitoClient;

    public CognitoService(CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
    }

    public void deleteAccount(String accessToken, String email) {
        
        log.info("CognitoService.deleteAccount()");
        GetUserRequest getUserRequest = GetUserRequest.builder()
                .accessToken(accessToken)
                .build();

        GetUserResponse getUserResponse = cognitoClient.getUser(getUserRequest);
        String username = getUserResponse.username();
        log.info("username: "   + username);
        log.info("System.getenv(\"MT_USER_POOL_ID\"): "   + System.getenv("MT_USER_POOL_ID"));

        AdminDeleteUserRequest deleteUserRequest = AdminDeleteUserRequest.builder()
                .userPoolId(System.getenv("MT_USER_POOL_ID"))
                .username(username)
                .build();

        cognitoClient.adminDeleteUser(deleteUserRequest);

        log.info("Deleted user: " + email + " from Cognito.");
    }
}
