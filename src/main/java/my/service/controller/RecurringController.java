package my.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.ChargeType;
import my.service.model.LineItem;
import my.service.model.Recurring;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@EnableWebMvc
public class RecurringController extends BaseController {

    @RequestMapping(path = "/recurring", method = RequestMethod.GET)
    public List<Recurring> listRecurring(@RequestHeader("Authorization") String token) {
        System.out.println("RecurringController");
        String email = getUserEmail(token);

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("Recurring")
                .indexName("UserEmailIndex")
                .keyConditionExpression("email = :emailValue")
                .expressionAttributeValues(Map.of(":emailValue", AttributeValue.builder().s(email).build()))
                .build();

        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

        System.out.println("queryResponse");
        System.out.println(queryResponse);

        List<Recurring> recurringItems = new ArrayList<>();
        for (Map<String, AttributeValue> item : queryResponse.items()) {
            System.out.println(item);

            String name = item.get("title").s();
            String user = item.get("email").s();
            Integer startAge = Integer.parseInt(item.get("startAge").n());
            Integer endAge = Integer.parseInt(item.get("endAge").n());
            ChargeType type = item.get("chargeType").s() == "EXPENSE" ? ChargeType.EXPENSE : ChargeType.INCOME;

            List<LineItem> lineItems = new ArrayList<>();
            for (AttributeValue lineItem : item.get("lineItems").l()) {
                String lineItemName = lineItem.m().get("title").s();
                Double amount = Double.parseDouble(lineItem.m().get("amount").n());
                LineItem lineItemObj = new LineItem(lineItemName, amount);
                lineItems.add(lineItemObj);
            }

            Recurring recurring = new Recurring(name, user, startAge, endAge, type, lineItems);
            // Set the Recurring properties based on the item attributes
            // For example: recurring.setId(item.get("id").s());
            recurringItems.add(recurring);
        }

        System.out.println(recurringItems);

        return recurringItems;
    }
}
