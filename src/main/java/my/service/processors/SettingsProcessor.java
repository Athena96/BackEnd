package my.service.processors;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import my.service.model.dynamodb.Settings;
import my.service.services.DDBTables;

public class SettingsProcessor extends BaseProcessor {
    private static final Logger log = LogManager.getLogger(SettingsProcessor.class);

    public Settings getSettings(String email, String scenarioId) throws Exception {
        List<Settings> listOfSettings = ddbService.queryTypesForUser(Settings.class, email, scenarioId);
        log.info("loaded " + listOfSettings.size() + " recurring from ddb");

        if (listOfSettings.size() != 1) {
            throw new Exception("expected 1 settings, got " + listOfSettings.size());
        }

        return listOfSettings.get(0);

    }

    public Settings addSettings(
        String email, 
        String scenarioId, 
        Date birthday,
        Double annualAssetReturnPercent,
        Double annualInflationPercent ) {
            String scenarioDataId = getScenarioDataId(email, scenarioId);
            String type = "Settings";
            Settings settings = new Settings(
                scenarioDataId,
                type,
                birthday,
                annualAssetReturnPercent,
                annualInflationPercent);
            ddbService.putItem(Settings.class, DDBTables.getDataTableName(), email, settings);
            log.info("added settings do DDB: " + settings);
            return settings;
    }

    public void deleteSettings(String email, String scenarioDataId, String type) {
        ddbService.deleteItem(Settings.class, email, scenarioDataId, type);
        log.info("deleted settings do DDB: " + scenarioDataId + type);
    }
}
