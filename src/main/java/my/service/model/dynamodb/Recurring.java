package my.service.model.dynamodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import my.service.model.ChargeType;
import my.service.model.DataType;
import my.service.model.LineItem;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import my.service.model.IDeserializable;

public class Recurring implements IDeserializable<Recurring> {

        public String scenarioDataId;
        public DataType type;
        public String id;
        public String title;
        public Integer startAge;
        public Integer endAge;
        public ChargeType chargeType;
        public List<LineItem> lineItems;

        public Recurring() {
                System.out.println("Recurring no args constructor");
        }

        public Recurring(String scenarioDataId,
                        DataType type,
                        String id,
                        String title,
                        Integer startAge,
                        Integer endAge,
                        ChargeType chargeType,
                        List<LineItem> lineItems) {
                this.scenarioDataId = scenarioDataId;
                this.type = type;
                this.id = id;
                this.title = title;
                this.startAge = startAge;
                this.endAge = endAge;
                this.chargeType = chargeType;
                this.lineItems = lineItems;
        }

        @Override
        public Recurring deserialize(final String email, final String scenarioId, Map<String, AttributeValue> item) {
                System.out.println("Recurring deserialize()");
                DataType dataType = DataType.valueOf(item.get("type").s().split("#")[0]);

                String scenarioDataId = email + "#" + scenarioId;
                String recurringId = item.get("id").s();
                String title = item.get("title").s();
                Integer startAge = Integer.parseInt(item.get("startAge").n());
                Integer endAge = Integer.parseInt(item.get("endAge").n());
                ChargeType chargeType = ("EXPENSE".equals(item.get("chargeType").s()))
                                ? ChargeType.EXPENSE
                                : ChargeType.INCOME;

                List<LineItem> lineItemsList = new ArrayList<>();
                for (AttributeValue lineItem : item.get("lineItems").l()) {
                        Map<String, AttributeValue> lineItemObj = lineItem.m();
                        String lineItemName = lineItemObj.get("title").s();
                        Double amount = Double.parseDouble(lineItemObj.get("amount").n());
                        LineItem line = new LineItem(lineItemName, amount);
                        lineItemsList.add(line);
                }

                return new Recurring(scenarioDataId, dataType, recurringId,
                                title, startAge, endAge, chargeType, lineItemsList);
        }

}