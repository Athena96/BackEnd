package my.service.model;

public record Assets(
                String id,
                String simulationId,
                String email,
                String ticker,
                Double quantity,
                Integer hasIndexData) {
}
