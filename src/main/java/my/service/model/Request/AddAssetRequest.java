package my.service.model.Request;

public record AddAssetRequest(String ticker, Double quantity, Integer hasIndexData) {
}
