package my.service.model.dynamodb;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import my.service.model.IDeserializable;
import my.service.model.ISerializable;
import my.service.services.StockService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Assets implements IDeserializable<Assets>, ISerializable<Assets> {

        private static final Logger log = LogManager.getLogger(Assets.class);

        public String scenarioDataId;
        public String type;
        public String id;
        public String ticker;
        public Double quantity;
        public Double price;
        public Integer hasIndexData;

        public Assets() {
                log.info("Assets no args constructor");
        }

        public Assets(String scenarioDataId,
                        String type,
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
        public Assets deserialize(
                        final String email,
                        final String scenarioId,
                        Map<String, AttributeValue> item) {
                log.info("Assets deserialize()");

                String scenarioDataId = item.get("scenarioDataId").s();
                String type = item.get("type").s();

                String id = item.get("id").s();
                String ticker = item.get("ticker").s();
                Double quantity = Double.parseDouble(item.get("quantity").n());

                Integer hasIndexData = item.get("hasIndexData").n() == null ? null
                                : Integer.parseInt(item.get("hasIndexData").n());

                Double price = 0.0;
                if (hasIndexData == 1) {
                        Date startTimeStock = new Date();
                        price = StockService.getPriceForStock(ticker);
                        Date endTimeStock = new Date();
                        log.info("StockService Load Time: "
                                        + (endTimeStock.getTime() - startTimeStock.getTime())
                                        + "ms");
                } else {
                        log.info("price quantity -> " + price);
                        price = Double.parseDouble(item.get("quantity").n());
                }

                return new Assets(scenarioDataId, type, id,
                                ticker, quantity, price, hasIndexData);
        }

        @Override
        public Map<String, AttributeValue> serializable(String email, Assets item) {
                Map<String, AttributeValue> serializeditem = new HashMap<>();

                serializeditem.put("scenarioDataId",
                                AttributeValue.builder().s(item.scenarioDataId).build());
                serializeditem.put("type",
                                AttributeValue.builder().s(item.type.toString()).build());
                serializeditem.put("id",
                                AttributeValue.builder().s(item.id).build());
                serializeditem.put("ticker",
                                AttributeValue.builder().s(item.ticker).build());
                serializeditem.put("quantity",
                                AttributeValue.builder().n(item.quantity.toString()).build());
                serializeditem.put("price",
                                AttributeValue.builder().n(item.price.toString()).build());
                serializeditem.put("hasIndexData",
                                AttributeValue.builder().n(item.hasIndexData.toString()).build());

                return serializeditem;

        }
}
