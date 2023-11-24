package my.service.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import my.service.model.dynamodb.Assets;
import my.service.model.dynamodb.Recurring;
import my.service.model.dynamodb.Scenario;
import my.service.model.dynamodb.Settings;
import my.service.services.DDBTables;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

public class ScenarioProcessor extends BaseProcessor {

    private static final Logger log = LogManager.getLogger(ScenarioProcessor.class);

    AssetProcessor assetProcessor = new AssetProcessor();
    RecurringProcessor recurringProcessor = new RecurringProcessor();
    SettingsProcessor settingsProcessor = new SettingsProcessor();

    public List<Scenario> listScenarios(String email) throws Exception {
            log.info("listScenarios ");

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(System.getenv("SCENARIO_TABLE"))
                .keyConditionExpression("email = :emailValue")
                .expressionAttributeValues(Map.of(":emailValue", AttributeValue.builder().s(email).build()))
                .build();
            log.info("queryRequest " + queryRequest);

        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

        log.info("queryResponse");
        log.info(queryResponse);

        List<Scenario> listOfScenarios = new ArrayList<>();
        for (Map<String, AttributeValue> item : queryResponse.items()) {
            String scenarioId = item.get("scenarioId").s();
            String title = item.get("title").s();
            String userEmail = item.get("email").s();
            Integer active = item.get("active").n() == null ? 0
                    : Integer.parseInt(item.get("active").n());
            Scenario scenario = new Scenario(userEmail, active, scenarioId, title);
            listOfScenarios.add(scenario);
        }
        log.info(listOfScenarios);

        return listOfScenarios;
    }

    public Scenario getActiveScenario(String email) throws Exception {
        log.info("getActiveScenario");

        List<Scenario> listOfScenarios = listScenarios(email);
        log.info("listOfScenarios " + listOfScenarios);

        for (Scenario scenario : listOfScenarios) {
            if (scenario.active == 1) {
                return scenario;
            }
        }
        throw new Exception("No active scenario found for user: " + email);
    }

    public Scenario getScenario(String email, String id) throws Exception {
        List<Scenario> listOfScenarios = listScenarios(email);
        for (Scenario scenario : listOfScenarios) {
            if (scenario.scenarioId == id) {
                return scenario;
            }
        }
        throw new Exception("No scenario found for id: " + id);
    }

    public void changeActiveScenario(String email, String idOfNewActiveScenario) throws Exception {
        // create the new scenario
        Scenario activeScenario = getActiveScenario(email);
        activeScenario.active = 0;
        Scenario newActiveScenario = getScenario(email, idOfNewActiveScenario);
        newActiveScenario.active = 1;

        ddbService.putItem(Scenario.class, DDBTables.getScenarioTableName(), email, activeScenario);
        ddbService.putItem(Scenario.class, DDBTables.getScenarioTableName(), email, newActiveScenario);

        log.info("changeActiveScenario to: " + newActiveScenario.title);
    }

    public void updateScenario(String email, String scenarioId, String title, Integer active) throws Exception {
        Scenario scenario = new Scenario(email, active, scenarioId, title);
        ddbService.putItem(Scenario.class, DDBTables.getScenarioTableName(), email, scenario);
        log.info("updated scenario: " + scenario.title);
    }

    public Scenario addScenario(String email, String title) throws Exception {

        // create the new scenario
        String scenarioId = UUID.randomUUID().toString();
        Scenario newScenario = new Scenario(email, 0, scenarioId, title);
        ddbService.putItem(Scenario.class, DDBTables.getScenarioTableName(), email, newScenario);
        log.info("Created new Scenario: " + newScenario.title);

        // get the active scenario
        Scenario activeScenario = getActiveScenario(email);
        log.info("Fetch existing active scenario " + activeScenario.title);

        // get the data for the active scenario
        List<Assets> baseAssets = assetProcessor.listAssets(email, activeScenario.scenarioId);
        List<Recurring> baseRecurrings = recurringProcessor.listRecurrings(email, activeScenario.scenarioId);
        Settings baseSettings = settingsProcessor.getSettings(email, activeScenario.scenarioId);
        log.info("Fetched Assets, Recurrings, Settings for active scenario ");

        // create copies of the base data for the new scenario
        for (Assets asset : baseAssets) {
            assetProcessor.addAsset(
                    email,
                    newScenario.scenarioId,
                    asset.ticker,
                    asset.quantity,
                    asset.hasIndexData);
        }
        log.info("Made copies of Assets for new scenario");

        for (Recurring recurring : baseRecurrings) {
            recurringProcessor.addRecurring(
                    email,
                    newScenario.scenarioId,
                    recurring.title,
                    recurring.startAge,
                    recurring.endAge,
                    recurring.chargeType,
                    recurring.amount);
        }
        log.info("Made copies of Recurrings for new scenario");

        settingsProcessor.addSettings(
                email,
                scenarioId,
                baseSettings.birthday,
                baseSettings.annualAssetReturnPercent,
                baseSettings.annualInflationPercent);
        log.info("Made copies of Settings for new scenario");

        return newScenario;
    }

    public void deleteScenario(String email, String scenarioId) throws Exception {
        String scenarioDataId = getScenarioDataId(email, scenarioId);

        // delete the scenario.
        ddbService.deleteItem(Scenario.class, email, scenarioDataId, scenarioId);

        // get the data for the active scenario
        List<Assets> baseAssets = assetProcessor.listAssets(email, scenarioId);
        List<Recurring> baseRecurrings = recurringProcessor.listRecurrings(email, scenarioId);
        Settings baseSettings = settingsProcessor.getSettings(email, scenarioId);
        log.info("Fetched Assets, Recurrings, Settings for scenario to delete");

        // create copies of the base data for the new scenario
        for (Assets asset : baseAssets) {
            assetProcessor.deleteAsset(email, scenarioDataId, asset.type);
        }
        log.info("deleted copies of Assets for deleted scenario");

        for (Recurring recurring : baseRecurrings) {
            recurringProcessor.deleteRecurring(email, scenarioDataId, recurring.type);
        }
        log.info("deleted copies of Recurrings for deleted scenario");

        settingsProcessor.deleteSettings(email, scenarioDataId, baseSettings.type);
        log.info("deleted copies of Settings for deleted scenario");
    
    }

}
