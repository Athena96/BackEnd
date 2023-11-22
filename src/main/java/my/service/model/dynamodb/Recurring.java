package my.service.model.dynamodb;

import java.util.HashMap;
import java.util.Map;

import my.service.model.ChargeType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import my.service.model.IDeserializable;
import my.service.model.ISerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Recurring implements IDeserializable<Recurring>, ISerializable<Recurring> {

        private static final Logger log = LogManager.getLogger(Recurring.class);

        public String scenarioDataId;
        public String type;
        public String id;
        public String title;
        public Integer startAge;
        public Integer endAge;
        public ChargeType chargeType;
        public Double amount;

        public Recurring() {
                log.info("Recurring no args constructor");
        }

        public Recurring(String scenarioDataId,
                        String type,
                        String id,
                        String title,
                        Integer startAge,
                        Integer endAge,
                        ChargeType chargeType,
                        Double amount) {
                this.scenarioDataId = scenarioDataId;
                this.type = type;
                this.id = id;
                this.title = title;
                this.startAge = startAge;
                this.endAge = endAge;
                this.chargeType = chargeType;
                this.amount = amount;
        }

        @Override
        public Recurring deserialize(
                final String email, 
                final String scenarioId, 
                Map<String, AttributeValue> item) {
                log.info("Recurring deserialize()");
                String scenarioDataId = item.get("scenarioDataId").s();
                String type = item.get("type").s();
                String recurringId = item.get("id").s();
                String title = item.get("title").s();
                Integer startAge = Integer.parseInt(item.get("startAge").n());
                Integer endAge = Integer.parseInt(item.get("endAge").n());
                ChargeType chargeType = ("EXPENSE".equals(item.get("chargeType").s()))
                                ? ChargeType.EXPENSE
                                : ChargeType.INCOME;
                Double amount = Double.parseDouble(item.get("amount").n());
                return new Recurring(scenarioDataId, type, recurringId,
                                title, startAge, endAge, chargeType, amount);
        }

        @Override
        public Map<String, AttributeValue> serializable(String email, Recurring item) {
                Map<String, AttributeValue> serializeditem = new HashMap<>();
                serializeditem.put("scenarioDataId", AttributeValue.builder().s(item.scenarioDataId).build());
                serializeditem.put("type", AttributeValue.builder().s(item.type).build());
                serializeditem.put("id", AttributeValue.builder().s(item.id).build());
                serializeditem.put("title", AttributeValue.builder().s(item.title).build());
                serializeditem.put("startAge", AttributeValue.builder().n(item.startAge.toString()).build());
                serializeditem.put("endAge", AttributeValue.builder().n(item.endAge.toString()).build());
                serializeditem.put("chargeType", 
                        AttributeValue.builder().s(item.chargeType.toString()).build());
                serializeditem.put("amount", AttributeValue.builder().n(item.amount.toString()).build());
                return serializeditem;
        }

}