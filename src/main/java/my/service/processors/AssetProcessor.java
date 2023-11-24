package my.service.processors;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import my.service.model.dynamodb.Assets;
import my.service.services.DDBTables;

public class AssetProcessor extends BaseProcessor {
    private static final Logger log = LogManager.getLogger(AssetProcessor.class);

    
    public List<Assets> listAssets(String email, String scenarioId) throws Exception {
        List<Assets> listOfAssets = ddbService.queryTypesForUser(Assets.class, email, scenarioId);
        log.info("loaded " + listOfAssets.size() + " assets from ddb");
        return listOfAssets;
    }

    public void addAsset(
            String email,
            String scenarioId,
            String ticker,
            Double quantity,
            Integer hasIndexData) {
        String id = getId();
        String scenarioDataId = getScenarioDataId(email, scenarioId);
        String type = getTypeForType("Assets", id);
        Assets asset = new Assets(
                scenarioDataId,
                type,
                id,
                ticker,
                quantity,
                1.0,
                hasIndexData);
        ddbService.putItem(Assets.class, DDBTables.getDataTableName(), email, asset);
        log.info("added asset do DDB: " + asset);
    }

    public void deleteAsset(String email, String scenarioDataId, String type) {
        ddbService.deleteItem(Assets.class,
        email,
        scenarioDataId,
        type);
    }

}
