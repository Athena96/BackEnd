package my.service.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.Response.AddAssetResponse;
import my.service.model.dynamodb.Assets;
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

@RestController
@EnableWebMvc
public class ScenariosController extends BaseController {

    @RequestMapping(path = "/listScenarios", method = RequestMethod.GET)
    public List<Scenario> listScenarios(@RequestHeader("Authorization") String token) throws Exception {
        Date startTime = new Date();
        System.out.println("ScenariosController");

        String email = getUserEmail(token);

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(System.getenv("SCENARIO_TABLE"))
                .keyConditionExpression("email = :emailValue")
                .expressionAttributeValues(Map.of(":emailValue", AttributeValue.builder().s(email).build()))
                .build();

        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

        System.out.println("queryResponse");
        System.out.println(queryResponse);

        List<Scenario> listOfScenarios = new ArrayList<>();
        for (Map<String, AttributeValue> item : queryResponse.items()) {
            System.out.println(item);

            String scenarioId = item.get("scenarioId").s();
            String title = item.get("title").s();
            String userEmail = item.get("email").s();
            Integer active = item.get("active").n() == null ? 0
                    : Integer.parseInt(item.get("active").n());
            Scenario scenario = new Scenario(userEmail, active, scenarioId, title);
            listOfScenarios.add(scenario);
        }

        System.out.println(listOfScenarios);

        Date endTime = new Date();
        System.out.println("ScenariosController Load Time: " + (endTime.getTime() - startTime.getTime()) + "ms");

        if (listOfScenarios.size() == 0) {
            System.out.println("No scenarios found for user: " + email);
            System.out.println("Setting up initial scenario");
            listOfScenarios.add(setUpInitialScenario(email));
        }

        return listOfScenarios;
    }


    // @RequestMapping(path = "/addScenario", method = RequestMethod.GET)
    // public void addScenario(@RequestHeader("Authorization") String token) throws Exception {

    // }

    private Scenario setUpInitialScenario(String email) {

        String scenarioId = UUID.randomUUID().toString();
        String title = email + " Default Scenario";
        Scenario scenario = new Scenario(email, 1, scenarioId, title);

        try {
            ddbService.putItem(Scenario.class, DDBTables.getScenarioTableName(), email, scenarioId, scenario);
            return scenario;
        } catch (Exception e) {
            System.out.println("Error in DDBService.addItem");
            e.printStackTrace();
            throw e;
        }

    }


}
