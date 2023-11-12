package my.service.model.Request;

public record UpdateRecurringRequest(
        String scenarioDataId,
        String type,
        String title,
        Integer startAge,
        Integer endAge,
        String chargeType,
        Double amount) {
}