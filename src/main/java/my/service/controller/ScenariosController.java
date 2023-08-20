package my.service.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.dynamodb.Scenario;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@EnableWebMvc
public class ScenariosController extends BaseController {

    @RequestMapping(path = "/listScenarios", method = RequestMethod.GET)
    public List<Scenario> listScenarios(@RequestHeader("Authorization") String token) throws Exception {
        System.out.println("ScenariosController");
        String email = getUserEmail(token);

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(System.getenv("SCENARIO_TABLE"))
                .indexName("UserEmailIndex")
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

        return listOfScenarios;
    }
}
