package my.service.processors;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import my.service.model.dynamodb.Scenario;
import my.service.services.CognitoService;
import my.service.services.DDBService;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class AccountProcessor extends BaseProcessor {
    private static final Logger log = LogManager.getLogger(AssetProcessor.class);

    private final CognitoService cognitoService;
    private final ScenarioProcessor scenarioProcessor;

    public AccountProcessor(DDBService ddbService, DynamoDbClient dynamoDbClient, CognitoService cognitoService) {
        log.info("AccountProcessor");

        this.cognitoService = cognitoService;
        this.scenarioProcessor = new ScenarioProcessor(ddbService, dynamoDbClient);
    }

    public void deleteAccount(String accessToken, String email) throws Exception {
        log.info("AccountProcessor.deleteAccount");

        List<Scenario> listOfScenarios = scenarioProcessor.listScenarios(email);
        for (Scenario scenario : listOfScenarios) {
            log.info("delete scenario: " + scenario.scenarioId + " for user: " + email + "");
            scenarioProcessor.deleteScenario(email, scenario.scenarioId);
        }

        cognitoService.deleteAccount(accessToken, email);
    }
}
