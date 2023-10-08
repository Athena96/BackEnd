package my.service.model.dynamodb;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import my.service.model.DataType;
import my.service.model.IDeserializable;
import my.service.model.ISerializable;
import my.service.services.StockService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Assets implements IDeserializable<Assets>, ISerializable<Assets> {

        public String scenarioDataId;
        public DataType type;
        public String id;
        public String ticker;
        public Double quantity;
        public Double price;
        public Integer hasIndexData;

        public Assets() {
                System.out.println("Assets no args constructor");
        }

        public Assets(String scenarioDataId,
                        DataType type,
                        String id,
                        String ticker,
                        Double quantity,
                        Double price,
                        Integer hasIndexData) {
                this.scenarioDataId = scenarioDataId;
                this.type = type;
                this.id = id;
                this.ticker = ticker;
                this.quantity = quantity;
                this.price = price;
                this.hasIndexData = hasIndexData;
        }

        @Override
        public Assets deserialize(final String email, final String scenarioId, Map<String, AttributeValue> item) {
                System.out.println("Assets deserialize()");

                String scenarioDataId = email + "#" + scenarioId;

                String assetId = item.get("id").s();
                String ticker = item.get("ticker").s();
                Double quantity = Double.parseDouble(item.get("quantity").n());

                Integer hasIndexData = item.get("hasIndexData").n() == null ? null
                                : Integer.parseInt(item.get("hasIndexData").n());

                Double price = 0.0;
                if (hasIndexData == 1) {
                        Date startTime_stock = new Date();
                        price = StockService.getPriceForStock(ticker);
                        Date endTime_stock = new Date();
                        System.out.println("StockService Load Time: "
                                        + (endTime_stock.getTime() - startTime_stock.getTime())
                                        + "ms");
                } else {
                        System.out.println("price quantity -> " + price);

                        price = Double.parseDouble(item.get("quantity").n());
                }

                DataType dataType = DataType.valueOf(item.get("type").s().split("#")[0]);

                return new Assets(scenarioDataId, dataType, assetId,
                                ticker, quantity, price, hasIndexData);
        }

        @Override
        public Map<String, AttributeValue> serializable(String email, String scenario, Assets item) {
                Map<String, AttributeValue> serializeditem = new HashMap<>();

                serializeditem.put("scenarioDataId", AttributeValue.builder().s(item.scenarioDataId).build());
                serializeditem.put("type", AttributeValue.builder().s(item.type.toString() + "#" + item.id).build());
                serializeditem.put("id", AttributeValue.builder().s(item.id).build());
                serializeditem.put("ticker", AttributeValue.builder().s(item.ticker).build());
                serializeditem.put("quantity", AttributeValue.builder().n(item.quantity.toString()).build());
                serializeditem.put("price", AttributeValue.builder().n(item.price.toString()).build());
                serializeditem.put("hasIndexData", AttributeValue.builder().n(item.hasIndexData.toString()).build());

                return serializeditem;

        }
}
