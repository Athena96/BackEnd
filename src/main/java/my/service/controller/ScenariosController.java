package my.service.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.dynamodb.Scenario;
import my.service.services.DDBTables;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@EnableWebMvc
public class ScenariosController extends BaseController {

    private static final Logger log = LogManager.getLogger(ScenariosController.class);

    @RequestMapping(path = "/listScenarios", method = RequestMethod.GET)
    public List<Scenario> listScenarios(@RequestHeader("Authorization") String token) throws Exception {
        Date startTime = new Date();
        log.info("ScenariosController");

        String email = getUserEmail(token);

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(System.getenv("SCENARIO_TABLE"))
                .keyConditionExpression("email = :emailValue")
                .expressionAttributeValues(Map.of(":emailValue", AttributeValue.builder().s(email).build()))
                .build();

        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

        log.info("queryResponse");
        log.info(queryResponse);

        List<Scenario> listOfScenarios = new ArrayList<>();
        for (Map<String, AttributeValue> item : queryResponse.items()) {
            log.info(item);

            String scenarioId = item.get("scenarioId").s();
            String title = item.get("title").s();
            String userEmail = item.get("email").s();
            Integer active = item.get("active").n() == null ? 0
                    : Integer.parseInt(item.get("active").n());
            Scenario scenario = new Scenario(userEmail, active, scenarioId, title);
            listOfScenarios.add(scenario);
        }

        log.info(listOfScenarios);

        Date endTime = new Date();
        log.info("ScenariosController Load Time: " + (endTime.getTime() - startTime.getTime()) + "ms");

        if (listOfScenarios.size() == 0) {
            log.info("No scenarios found for user: " + email);
            log.info("Setting up initial scenario");
            listOfScenarios.add(setUpInitialScenario(email));
        }
        return listOfScenarios;
    }

    private Scenario setUpInitialScenario(String email) {
        String scenarioId = UUID.randomUUID().toString();
        String title = email + " Default Scenario";
        Scenario scenario = new Scenario(email, 1, scenarioId, title);

        try {
            ddbService.putItem(Scenario.class, DDBTables.getScenarioTableName(), email, scenario);
            return scenario;
        } catch (Exception e) {
            log.info("Error in DDBService.addItem");
            e.printStackTrace();
            throw e;
        }
    }
}
