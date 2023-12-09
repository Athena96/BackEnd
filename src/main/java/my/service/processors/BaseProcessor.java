package my.service.processors;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseProcessor {
    private static final Logger log = LogManager.getLogger(BaseProcessor.class);

    public BaseProcessor() {
        log.info("BaseProcessor");
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
