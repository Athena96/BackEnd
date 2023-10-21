package my.service.model.dynamodb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import my.service.model.IDeserializable;
import my.service.model.ISerializable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import my.service.model.DataType;

public class Settings implements IDeserializable<Settings>, ISerializable<Settings> {

        public String scenarioDataId;
        public DataType type;
        public Date birthday;
        public Double annualAssetReturnPercent;
        public Double annualInflationPercent;

        public Settings() {
                System.out.println("Settings no args constructor");
        }

        public Settings(
                        String scenarioDataId,
                        DataType type,
                        Date birthday,
                        Double annualAssetReturnPercent,
                        Double annualInflationPercent) {
                this.scenarioDataId = scenarioDataId;
                this.type = type;
                this.birthday = birthday;
                this.annualAssetReturnPercent = annualAssetReturnPercent;
                this.annualInflationPercent = annualInflationPercent;
        }

        @Override
        public Settings deserialize(final String email, final String scenarioId, Map<String, AttributeValue> item) {
                try {
                        System.out.println("Settings deserialize()");

                        String scenarioDataId = email + "#" + scenarioId;

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date birthday = formatter.parse(item.get("birthday").s());

                        Double annualAssetReturnPercent = item.get("annualAssetReturnPercent")
                                        .n() == null
                                                        ? null
                                                        : Double.parseDouble(item
                                                                        .get("annualAssetReturnPercent")
                                                                        .n());
                        Double quanannualInflationPercenttity = item.get("annualInflationPercent")
                                        .n() == null
                                                        ? null
                                                        : Double.parseDouble(item
                                                                        .get("annualInflationPercent")
                                                                        .n());
                        DataType dataType = DataType.valueOf(item.get("type").s().split("#")[0]);

                        return new Settings(scenarioDataId, dataType, birthday,
                                        annualAssetReturnPercent, quanannualInflationPercenttity);
                } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                }

        }

        @Override
        public Map<String, AttributeValue> serializable(String email, String scenario, Settings item) {
                Map<String, AttributeValue> serializeditem = new HashMap<>();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String birthday = formatter.format(item.birthday);

                serializeditem.put("scenarioDataId", AttributeValue.builder().s(item.scenarioDataId).build());
                serializeditem.put("type", AttributeValue.builder().s(item.type.toString()).build());
                serializeditem.put("birthday", AttributeValue.builder().s(birthday).build());
                serializeditem.put("annualAssetReturnPercent",
                                AttributeValue.builder().n(item.annualAssetReturnPercent.toString()).build());
                serializeditem.put("annualInflationPercent",
                                AttributeValue.builder().n(item.annualInflationPercent.toString()).build());

                return serializeditem;

        }

}