package my.service.model.Request;

public record AddRecurringRequest(
        String scenarioId,
        String title,
        Integer startAge,
        Integer endAge,
        String chargeType,
        Double amount) {
}