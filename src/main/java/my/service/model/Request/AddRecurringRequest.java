package my.service.model.Request;

public record AddRecurringRequest(
        String scenarioDataId,
        String title,
        Integer startAge,
        Integer endAge,
        String chargeType,
        Double amount) {
}