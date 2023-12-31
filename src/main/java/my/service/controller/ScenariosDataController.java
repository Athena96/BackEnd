package my.service.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.ChargeType;
import my.service.model.DataType;
import my.service.model.Response.ScenarioDataResponse;

import my.service.model.dynamodb.Assets;
import my.service.model.dynamodb.Recurring;
import my.service.model.dynamodb.Scenario;
import my.service.model.dynamodb.Settings;
import my.service.processors.ScenarioProcessor;
import my.service.processors.SettingsProcessor;
import my.service.services.StockService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@EnableWebMvc
public class ScenariosDataController extends BaseController {

        private static final Logger log = LogManager.getLogger(ScenariosDataController.class);

        ScenarioProcessor scenarioProcessor = new ScenarioProcessor(this.ddbService, this.dynamoDbClient);

        @RequestMapping(path = "/getScenarioData", method = RequestMethod.GET)
        public ScenarioDataResponse getScenarioData(@RequestHeader("Authorization") String accessToken,
         @RequestHeader("idtoken") String token) throws Exception {

                Date startTime = new Date();
                log.info("DataControllerdd");

                String email = getUserEmail(token);
                log.info("email " + email);

                Scenario activeScenario = scenarioProcessor.getActiveScenario(email);
                log.info("activeScenario " + activeScenario);

                String activeScenarioID = activeScenario.scenarioId;
                log.info("Active Scenario: " + activeScenarioID);

                String scenarioDataId = email + "#" + activeScenarioID;
                log.info("PK: " + scenarioDataId);
                QueryRequest queryRequest = QueryRequest.builder()
                                .tableName(System.getenv("DATA_TABLE"))
                                .keyConditionExpression(
                                                "scenarioDataId = :emailScenarioIdValue")
                                .expressionAttributeValues(
                                                Map.of(":emailScenarioIdValue",
                                                                AttributeValue.builder().s(scenarioDataId).build()))
                                .build();

                QueryResponse queryResponse = dynamoDbClient.query(queryRequest);
                log.info("queryResponse");
                log.info(queryResponse);

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
                                                Date startTimeStock = new Date();
                                                price = StockService.getPriceForStock(ticker);
                                                Date endTimeStock = new Date();
                                                log.info("StockService Load Time: "
                                                                + (endTimeStock.getTime() - startTimeStock.getTime())
                                                                + "ms");
                                        } else {
                                                log.info("price quantity -> " + price);

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
                                                        .n() == null ? 
                                                        null : 
                                                        Double.parseDouble(
                                                                item.get("annualInflationPercent").n());
                                        settings = new Settings(
                                                scenarioDataId, 
                                                typeString, 
                                                birthday,
                                                annualAssetReturnPercent, 
                                                quanannualInflationPercenttity);
                                        break;

                                default:
                                        log.info("Invalid dataType -> " + dataType);
                                        break;
                        }

                }

                if (settings == null) {
                        log.info("User does not have settings, adding default settings");
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.YEAR, -30);
                        Date dateThirtyYearsAgo = calendar.getTime();
                        SettingsProcessor settingsProcessor = new SettingsProcessor(this.ddbService);
                        settings = settingsProcessor.addSettings(
                                email, 
                        activeScenarioID, 
                        dateThirtyYearsAgo, 
                        11.77, 
                        3.96);
                }

                ScenarioDataResponse scenarioDataResponse = new ScenarioDataResponse(
                        settings, 
                        listOfAssets,
                        listOfRecurring);

                log.info(scenarioDataResponse);

                Date endTime = new Date();
                log.info("ScenariosDataController Load Time: " + (endTime.getTime() - startTime.getTime())
                                + "ms");

                return scenarioDataResponse;
        }
}
