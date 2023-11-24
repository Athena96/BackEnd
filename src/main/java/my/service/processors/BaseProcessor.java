package my.service.processors;

import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import my.service.services.DDBService;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class BaseProcessor {
    private static final Logger log = LogManager.getLogger(BaseProcessor.class);

    protected final DynamoDbClient dynamoDbClient;
    protected final DDBService ddbService;

    public BaseProcessor() {
        log.info("BaseProcessor");
        Date startTime = new Date();

        dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();
    
        ddbService = new DDBService(dynamoDbClient);
        Date endTime = new Date();
        log.info("BaseProcessor Load Time: " + (endTime.getTime() - startTime.getTime()) + "ms");
    }

    public static String getId() {
        return UUID.randomUUID().toString();
    }
    public static String getScenarioDataId(String email, String scenarioId) {
        String scenarioDataId = email + "#" + scenarioId;
        return scenarioDataId;
    }
    public static String getTypeForType(String type, String id) {
        String typeForType = type + "#" + id;
        return typeForType;
    }
}
