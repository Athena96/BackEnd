package my.service.model;

import java.util.List;


public record Recurring(
        String id,
        String simulationId,
        String title,
        String email,
        Integer startAge,
        Integer endAge,
        ChargeType chargeType,
        List<LineItem> lineItems) {
}

