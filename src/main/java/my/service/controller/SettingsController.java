package my.service.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import my.service.model.Settings;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@EnableWebMvc
public class SettingsController extends BaseController {

    @RequestMapping(path = "/settings", method = RequestMethod.GET)
    public List<Settings> listSettings(@RequestHeader("Authorization") String token) throws ParseException {
        System.out.println("SettingsController");
        String email = getUserEmail(token);

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("Settings")
                .indexName("UserEmailIndex")
                .keyConditionExpression("email = :emailValue")
                .expressionAttributeValues(Map.of(":emailValue", AttributeValue.builder().s(email).build()))
                .build();

        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

        System.out.println("queryResponse");
        System.out.println(queryResponse);

        List<Settings> settingsItems = new ArrayList<>();
        for (Map<String, AttributeValue> item : queryResponse.items()) {
            System.out.println(item);

            String id = item.get("id").s();
            String simulationId = item.get("simulationId").s();
            String userEmail = item.get("email").s();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date birthday = formatter.parse(item.get("birthday").s());

            Double annualAssetReturnPercent = item.get("annualAssetReturnPercent").n() == null ? null
                    : Double.parseDouble(item.get("annualAssetReturnPercent").n());
            Double quanannualInflationPercenttity = item.get("annualInflationPercent").n() == null ? null
                    : Double.parseDouble(item.get("annualInflationPercent").n());

            Settings settings = new Settings(id, simulationId, userEmail, birthday, annualAssetReturnPercent,
                    quanannualInflationPercenttity);

            settingsItems.add(settings);
        }

        System.out.println(settingsItems);

        return settingsItems;
    }
}
