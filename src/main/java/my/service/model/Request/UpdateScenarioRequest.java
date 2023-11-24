package my.service.model.Request;

public record UpdateScenarioRequest(
        String title,
        String scenarioId,
        Integer active) {
}