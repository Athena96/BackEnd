
package my.service.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.processors.AccountProcessor;
import my.service.services.CognitoService;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@EnableWebMvc
public class AccountController extends BaseController {
    private static final Logger log = LogManager.getLogger(AccountController.class);

    private final CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
            .region(Region.US_EAST_1) // Change to your Cognito region
            .build();
    private final CognitoService cognitoService = new CognitoService(cognitoClient);
    private final AccountProcessor accountProcessor = new AccountProcessor(ddbService, dynamoDbClient, cognitoService);

    @RequestMapping(path = "/deleteAccount", method = RequestMethod.DELETE)
    public void deleteAccount(@RequestHeader("Authorization") String accessToken,
            @RequestHeader("idtoken") String token) throws Exception {
        try {
            log.info("AccountController.deleteAccount()");
            String email = getUserEmail(token);
            accountProcessor.deleteAccount(accessToken, email);
        } catch (Exception e) {
            log.info("Error in AccountController.deleteAccount");
            e.printStackTrace();
        }
    }

}
