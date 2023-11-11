package my.service.model.Request;

public record AddAssetRequest(String scenarioId, String ticker, Double quantity, Integer hasIndexData) {
}
