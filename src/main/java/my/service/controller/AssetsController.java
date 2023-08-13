package my.service.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.Assets;
import my.service.services.StockService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@EnableWebMvc
public class AssetsController extends BaseController {

    @RequestMapping(path = "/assets", method = RequestMethod.GET)
    public List<Assets> listAssets(@RequestHeader("Authorization") String token) throws Exception {
        System.out.println("AssetsController");
        String email = getUserEmail(token);

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("Assets")
                .indexName("UserEmailIndex")
                .keyConditionExpression("email = :emailValue")
                .expressionAttributeValues(Map.of(":emailValue", AttributeValue.builder().s(email).build()))
                .build();

        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

        System.out.println("queryResponse");
        System.out.println(queryResponse);

        List<Assets> listOfAssets = new ArrayList<>();
        for (Map<String, AttributeValue> item : queryResponse.items()) {
            System.out.println(item);

            String id = item.get("id").s();
            String simulationId = item.get("simulationId").s();
            String user = item.get("email").s();
            String ticker = item.get("ticker").s();
            Double quantity = item.get("quantity").n() == null ? null : Double.parseDouble(item.get("quantity").n());
            Integer hasIndexData = item.get("hasIndexData").n() == null ? null
                    : Integer.parseInt(item.get("hasIndexData").n());
            Double price = 0.0;
            if (hasIndexData == 1) {
                price = StockService.getPriceForStock(ticker);
                System.out.println("price -> " + price);
            } else {
                price = 1.0;
            }
            Assets asset = new Assets(id, simulationId, user, ticker, quantity, price, hasIndexData);
            listOfAssets.add(asset);
        }

        System.out.println(listOfAssets);

        return listOfAssets;
    }
}
