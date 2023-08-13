package my.service.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.ChargeType;
import my.service.model.LineItem;
import my.service.model.OneTime;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@EnableWebMvc
public class OneTimeController extends BaseController {

    @RequestMapping(path = "/onetime", method = RequestMethod.GET)
    public List<OneTime> listOneTime(@RequestHeader("Authorization") String token) {
        System.out.println("OneTimeController");
        String email = getUserEmail(token);

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("OneTime")
                .indexName("UserEmailIndex")
                .keyConditionExpression("email = :emailValue")
                .expressionAttributeValues(Map.of(":emailValue", AttributeValue.builder().s(email).build()))
                .build();

        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

        System.out.println("queryResponse");
        System.out.println(queryResponse);

        List<OneTime> listOfOneTime = new ArrayList<>();
        for (Map<String, AttributeValue> item : queryResponse.items()) {
            System.out.println(item);

            String id = item.get("id").s();
            String simulationId = item.get("simulationId").s();
            String title = item.get("title").s();
            Integer age = item.get("age").n() == null ? null : Integer.parseInt(item.get("age").n());
            ChargeType type = ("EXPENSE".equals(item.get("chargeType").s())) ? ChargeType.EXPENSE : ChargeType.INCOME;

            Map<String, AttributeValue> lineItemObj = item.get("lineItem").m();
            String lineItemTitle = lineItemObj.get("title").s();
            Double lineItemAmount = Double.parseDouble(lineItemObj.get("amount").n());
            LineItem lineItem = new LineItem(lineItemTitle, lineItemAmount);

            OneTime oneTime = new OneTime(id, simulationId, title, age, type, lineItem);

            listOfOneTime.add(oneTime);
        }

        System.out.println(listOfOneTime);

        return listOfOneTime;
    }
}
