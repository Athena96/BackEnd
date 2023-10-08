package my.service.model.Request;

public record UpdateAssetRequest(String scenarioDataId, String typeId, String ticker, Double quantity,
        Integer hasIndexData) {

}
