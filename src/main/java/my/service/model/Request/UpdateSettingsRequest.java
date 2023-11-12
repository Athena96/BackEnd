package my.service.model.Request;

import java.util.Date;

public record UpdateSettingsRequest(
        Date birthday,
        Double annualAssetReturnPercent,
        Double annualInflationPercent) {

}
