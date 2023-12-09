package my.service.processors;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import my.service.model.ChargeType;
import my.service.model.dynamodb.Recurring;
import my.service.services.DDBService;
import my.service.services.DDBTables;

public class RecurringProcessor extends BaseProcessor {
    private static final Logger log = LogManager.getLogger(RecurringProcessor.class);

    private final DDBService ddbService;
    public RecurringProcessor( DDBService ddbService) {
        log.info("RecurringProcessor");
        this.ddbService = ddbService;
    }
    public List<Recurring> listRecurrings(String email, String scenarioId) throws Exception {
        List<Recurring> listOfRecurrings = ddbService.queryTypesForUser(
            Recurring.class, 
            email, 
            scenarioId);
        log.info("loaded " + listOfRecurrings.size() + " recurring from ddb");
        return listOfRecurrings;
    }

    public void addRecurring(
            String email,
            String scenarioId,
            String title,
            Integer startAge,
            Integer endAge,
            ChargeType chargeType,
            Double amount) {
        String id = getId();
        String scenarioDataId = getScenarioDataId(email, scenarioId);
        String type = getTypeForType("Recurring", id);
        Recurring recurring = new Recurring(
                scenarioDataId,
                type,
                id,
                title,
                startAge,
                endAge,
                chargeType,
                amount);
        ddbService.putItem(Recurring.class, DDBTables.getDataTableName(), email, recurring);
        log.info("added recurring do DDB: " + recurring);
    }

    public void deleteRecurring(String email, String scenarioDataId, String type) {
            ddbService.deleteItem(Recurring.class, email, scenarioDataId,
            type);
            log.info("deleted recurring do DDB: " + scenarioDataId + type);

    }
}
