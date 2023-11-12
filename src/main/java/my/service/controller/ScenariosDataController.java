package my.service.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.ChargeType;
import my.service.model.DataType;
import my.service.model.Response.ScenarioDataResponse;

import my.service.model.dynamodb.Assets;
import my.service.model.dynamodb.Recurring;
import my.service.model.dynamodb.Scenario;
import my.service.model.dynamodb.Settings;
import my.service.services.StockService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@EnableWebMvc
public class ScenariosDataController extends BaseController {

        @RequestMapping(path = "/getScenarioData", method = RequestMethod.GET)
        public ScenarioDataResponse getScenarioData(@RequestHeader("Authorization") String token) throws Exception {

                Date startTime = new Date();
                System.out.println("DataController");

                String email = getUserEmail(token);

                Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
                expressionAttributeValues.put(":emailValue", AttributeValue.builder().s(email).build());
                expressionAttributeValues.put(":activeValue", AttributeValue.builder().n("1").build());
                QueryRequest scenarioqueryRequest = QueryRequest.builder()
                                .tableName(System.getenv("SCENARIO_TABLE"))
                                .keyConditionExpression("email = :emailValue and active = :activeValue")
                                .expressionAttributeValues(expressionAttributeValues)
                                .build();

                QueryResponse scenarioqueryResponse = dynamoDbClient.query(scenarioqueryRequest);

                Scenario activeScenario = null;
                for (Map<String, AttributeValue> item : scenarioqueryResponse.items()) {
                        System.out.println(item.get("active"));
                        if (item.get("active").n() != null && Integer.parseInt(item.get("active").n()) == 1) {
                                String sid = item.get("scenarioId").s();
                                String title = item.get("title").s();
                                String userEmail = item.get("email").s();
                                Integer active = item.get("active").n() == null ? 0
                                                : Integer.parseInt(item.get("active").n());
                                activeScenario = new Scenario(userEmail, active, sid, title);
                        }
                }

                String activeScenarioID = activeScenario.scenarioId;
                System.out.println("Active Scenario: " + activeScenarioID);
                String scenarioDataId = email + "#" + activeScenarioID;
                System.out.println("PK: " + scenarioDataId);
                QueryRequest queryRequest = QueryRequest.builder()
                                .tableName(System.getenv("DATA_TABLE"))
                                .keyConditionExpression(
                                                "scenarioDataId = :emailScenarioIdValue")
                                .expressionAttributeValues(
                                                Map.of(":emailScenarioIdValue",
                                                                AttributeValue.builder().s(scenarioDataId).build()))
                                .build();

                QueryResponse queryResponse = dynamoDbClient.query(queryRequest);
                System.out.println("queryResponse");
                System.out.println(queryResponse);

                List<Recurring> listOfRecurring = new ArrayList<>();
                List<Assets> listOfAssets = new ArrayList<>();
                Settings settings = null;

                for (Map<String, AttributeValue> item : queryResponse.items()) {
                        DataType dataType = DataType.valueOf(item.get("type").s().split("#")[0]);
                        String typeString = item.get("type").s();
                        switch (dataType) {
                                case Recurring:
                                        String recurringId = item.get("id").s();
                                        String title = item.get("title").s();
                                        Integer startAge = Integer.parseInt(item.get("startAge").n());
                                        Integer endAge = Integer.parseInt(item.get("endAge").n());
                                        ChargeType chargeType = ("EXPENSE".equals(item.get("chargeType").s()))
                                                        ? ChargeType.EXPENSE
                                                        : ChargeType.INCOME;
                                        Double amount = Double.parseDouble(item.get("amount").n());

                                        Recurring recurring = new Recurring(scenarioDataId, typeString, recurringId,
                                                        title, startAge, endAge, chargeType, amount);
                                        listOfRecurring.add(recurring);
                                        break;
                                case Assets:
                                        String assetId = item.get("id").s();
                                        String ticker = item.get("ticker").s();
                                        Double quantity = Double.parseDouble(item.get("quantity").n());

                                        Integer hasIndexData = item.get("hasIndexData").n() == null ? null
                                                        : Integer.parseInt(item.get("hasIndexData").n());

                                        Double price = 0.0;
                                        if (hasIndexData == 1) {
                                                Date startTime_stock = new Date();
                                                price = StockService.getPriceForStock(ticker);
                                                Date endTime_stock = new Date();
                                                System.out.println("StockService Load Time: "
                                                                + (endTime_stock.getTime() - startTime_stock.getTime())
                                                                + "ms");
                                        } else {
                                                System.out.println("price quantity -> " + price);

                                                price = Double.parseDouble(item.get("quantity").n());
                                        }
                                        // String type = "Assets" + "#" + assetId;

                                        listOfAssets.add(new Assets(scenarioDataId, typeString, assetId, ticker,
                                                        quantity,
                                                        price, hasIndexData));
                                        break;
                                case Settings:
                                        // Parse Settings

                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                        Date birthday = formatter.parse(item.get("birthday").s());

                                        Double annualAssetReturnPercent = item.get("annualAssetReturnPercent")
                                                        .n() == null
                                                                        ? null
                                                                        : Double.parseDouble(item
                                                                                        .get("annualAssetReturnPercent")
                                                                                        .n());
                                        Double quanannualInflationPercenttity = item.get("annualInflationPercent")
                                                        .n() == null
                                                                        ? null
                                                                        : Double.parseDouble(item
                                                                                        .get("annualInflationPercent")
                                                                                        .n());
                                        settings = new Settings(scenarioDataId, typeString, birthday,
                                                        annualAssetReturnPercent, quanannualInflationPercenttity);
                                        break;

                                default:
                                        System.out.println("Invalid dataType -> " + dataType);
                                        break;
                        }

                }

                ScenarioDataResponse scenarioDataResponse = new ScenarioDataResponse(settings, listOfAssets,
                                listOfRecurring);

                System.out.println(scenarioDataResponse);

                Date endTime = new Date();
                System.out.println("ScenariosDataController Load Time: " + (endTime.getTime() - startTime.getTime())
                                + "ms");

                return scenarioDataResponse;
        }
}
