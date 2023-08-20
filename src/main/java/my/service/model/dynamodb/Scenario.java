package my.service.model.dynamodb;

public record Scenario(
                String email,
                Integer active,
                String scenarioId,
                String title) {
}
