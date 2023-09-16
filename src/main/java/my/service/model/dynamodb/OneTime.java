package my.service.model.dynamodb;

import java.util.Map;

import my.service.model.ChargeType;
import my.service.model.DataType;
import my.service.model.IDeserializable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class OneTime implements IDeserializable<OneTime> {

        public String scenarioDataId;
        public DataType type;
        public String id;
        public String title;
        public Integer age;
        public ChargeType chargeType;
        public Double amount;

        public OneTime() {
                System.out.println("OneTime no args constructor");
        }

        public OneTime(String scenarioDataId,
                        DataType type,
                        String id,
                        String title,
                        Integer age,
                        ChargeType chargeType,
                        Double amount) {
                this.scenarioDataId = scenarioDataId;
                this.type = type;
                this.id = id;
                this.title = title;
                this.age = age;
                this.chargeType = chargeType;
                this.amount = amount;
        }

        @Override
        public OneTime deserialize(final String email, final String scenarioId, Map<String, AttributeValue> item) {
                System.out.println("OneTime deserialize()");
                DataType dataType = DataType.valueOf(item.get("type").s().split("#")[0]);

                String scenarioDataId = email + "#" + scenarioId;
                String oneTimeId = item.get("id").s();
                String title = item.get("title").s();

                Integer oneTimeAge = Integer.parseInt(item.get("age").n());
                ChargeType oneTimeChargeType = ("EXPENSE".equals(item.get("chargeType").s()))
                                ? ChargeType.EXPENSE
                                : ChargeType.INCOME;
                Double amount = Double.parseDouble(item.get("amount").n());

                return new OneTime(scenarioDataId, dataType, oneTimeId, title,
                                oneTimeAge, oneTimeChargeType,
                                amount);
        }

}