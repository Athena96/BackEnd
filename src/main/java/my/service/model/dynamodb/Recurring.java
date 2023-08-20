package my.service.model.dynamodb;

import java.util.List;

import my.service.model.ChargeType;
import my.service.model.DataType;
import my.service.model.LineItem;


public record Recurring(
        String scenarioDataId,
        DataType type,
        String id,
        String title,
        Integer startAge,
        Integer endAge,
        ChargeType chargeType,
        List<LineItem> lineItems) {
}

