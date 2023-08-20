package my.service.model.dynamodb;

import my.service.model.ChargeType;
import my.service.model.DataType;

public record OneTime(
        String scenarioDataId,
        DataType type,
        String id,
        String title,
        Integer age,
        ChargeType chargeType,
        Double amount) {
}
