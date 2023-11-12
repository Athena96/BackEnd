package my.service.model.Request;

import java.util.Date;

public record UpdateSettingsRequest(
                String scenarioDataId,
                String type,
                Date birthday,
                Double annualAssetReturnPercent,
                Double annualInflationPercent) {

}
