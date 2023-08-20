package my.service.model.dynamodb;

import java.util.Date;

import my.service.model.DataType;

public record Settings(
        String scenarioDataId,
        DataType type,
        Date birthday,
        Double annualAssetReturnPercent,
        Double annualInflationPercent) {
}