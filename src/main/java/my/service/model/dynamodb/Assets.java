package my.service.model.dynamodb;

import my.service.model.DataType;

public record Assets(
        String scenarioDataId,
        DataType type,
        String id,
        String ticker,
        Double quantity,
        Double price,
        Integer hasIndexData) {
}
