package my.service.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.ChargeType;
import my.service.model.DataType;
import my.service.model.LineItem;
import my.service.model.Response.ScenarioDataResponse;

import my.service.model.dynamodb.Assets;
import my.service.model.dynamodb.OneTime;
import my.service.model.dynamodb.Recurring;

import my.service.model.dynamodb.Settings;
import my.service.services.StockService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@EnableWebMvc
public class ScenariosDataController extends BaseController {

        @RequestMapping(path = "/getScenarioData", method = RequestMethod.GET)
        public ScenarioDataResponse getScenarioData(@RequestHeader("Authorization") String token,
                        @RequestParam(name = "scenarioId", required = true) String scenarioId) throws Exception {
                System.out.println("DataController");
                System.out.println("scenarioId");
                System.out.println(scenarioId);

                String email = getUserEmail(token);
                String scenarioDataId = email + "#" + scenarioId;

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
                List<OneTime> listOfOneTime = new ArrayList<>();
                List<Assets> listOfAssets = new ArrayList<>();
                Settings settings = null;

                for (Map<String, AttributeValue> item : queryResponse.items()) {
                        System.out.println(item);
                        System.out.println(item.get("type"));
                        System.out.println(item.get("type").s());
                        System.out.println(item.get("type").s().split("#")[0]);

                        DataType dataType = DataType.valueOf(item.get("type").s().split("#")[0]);

                        switch (dataType) {
                                case Recurring:
                                        String recurringId = item.get("id").s();
                                        String title = item.get("title").s();
                                        Integer startAge = Integer.parseInt(item.get("startAge").n());
                                        Integer endAge = Integer.parseInt(item.get("endAge").n());
                                        ChargeType chargeType = ("EXPENSE".equals(item.get("chargeType").s()))
                                                        ? ChargeType.EXPENSE
                                                        : ChargeType.INCOME;

                                        List<LineItem> lineItemsList = new ArrayList<>();
                                        for (AttributeValue lineItem : item.get("lineItems").l()) {
                                                Map<String, AttributeValue> lineItemObj = lineItem.m();

                                                String lineItemName = lineItemObj.get("title").s();
                                                Double amount = Double.parseDouble(lineItemObj.get("amount").n());
                                                LineItem line = new LineItem(lineItemName, amount);
                                                lineItemsList.add(line);
                                        }

                                        Recurring recurring = new Recurring(scenarioDataId, dataType, recurringId,
                                                        title, startAge, endAge, chargeType, lineItemsList);
                                        listOfRecurring.add(recurring);
                                        break;
                                case OneTime:
                                        String oneTimeId = item.get("id").s();
                                        String oneTimeTitle = item.get("title").s();
                                        Integer oneTimeAge = Integer.parseInt(item.get("age").n());
                                        ChargeType oneTimeChargeType = ("EXPENSE".equals(item.get("chargeType").s()))
                                                        ? ChargeType.EXPENSE
                                                        : ChargeType.INCOME;
                                        Double amount = Double.parseDouble(item.get("amount").n());

                                        OneTime oneTime = new OneTime(scenarioDataId, dataType, oneTimeId, oneTimeTitle,
                                                        oneTimeAge, oneTimeChargeType,
                                                        amount);
                                        listOfOneTime.add(oneTime);
                                        break;
                                case Assets:
                                        String assetId = item.get("id").s();
                                        String ticker = item.get("ticker").s();
                                        Double quantity = Double.parseDouble(item.get("quantity").n());

                                        Integer hasIndexData = item.get("hasIndexData").n() == null ? null
                                                        : Integer.parseInt(item.get("hasIndexData").n());

                                        Double price = 0.0;
                                        if (hasIndexData == 1) {
                                                price = StockService.getPriceForStock(ticker);
                                                System.out.println("price -> " + price);
                                        } else {
                                                System.out.println("price quantity -> " + price);

                                                price = Double.parseDouble(item.get("quantity").n());
                                        }

                                        listOfAssets.add(new Assets(scenarioDataId, dataType, assetId, ticker, quantity,
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
                                        settings = new Settings(scenarioDataId, dataType, birthday,
                                                        annualAssetReturnPercent, quanannualInflationPercenttity);
                                        break;

                                default:
                                        System.out.println("Invalid dataType -> " + dataType);
                                        break;
                        }

                }

                ScenarioDataResponse scenarioDataResponse = new ScenarioDataResponse(settings, listOfAssets,
                                listOfRecurring, listOfOneTime);

                System.out.println(scenarioDataResponse);

                return scenarioDataResponse;
        }
}
