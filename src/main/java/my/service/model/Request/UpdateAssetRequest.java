package my.service.model.Request;

public record UpdateAssetRequest(
                String scenarioDataId,
                String type,
                String ticker,
                Double quantity,
                Integer hasIndexData) {

}
