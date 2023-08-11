package my.service.model;

import java.util.Date;

public record Settings(
                String id,
                String simulationId,
                String email,
                Date birthday,
                Double annualAssetReturnPercent,
                Double annualInflationPercent) {
}
