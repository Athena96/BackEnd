package my.service.model.dynamodb;

import java.util.Map;

import my.service.model.IDeserializable;
import my.service.model.ISerializable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Scenario implements IDeserializable<Scenario>, ISerializable<Scenario> {

    public String email;
    public Integer active;
    public String scenarioId;
    public String title;

    public Scenario() {
        System.out.println("Scenario no args constructor");
    }

    public Scenario(String email, Integer active, String scenarioId, String title) {
        this.email = email;
        this.active = active;
        this.scenarioId = scenarioId;
        this.title = title;
    }

    @Override
    public Map<String, AttributeValue> serializable(String email, String scenario, Scenario item) {
        Map<String, AttributeValue> map = Map.of(
            "email", AttributeValue.builder().s(item.email).build(),
            "active", AttributeValue.builder().n(item.active.toString()).build(),
            "scenarioId", AttributeValue.builder().s(item.scenarioId).build(),
            "title", AttributeValue.builder().s(item.title).build()
        );
        return map;
    }

    @Override
    public Scenario deserialize(String email, String scenario, Map<String, AttributeValue> item) {
        System.out.println("Scenario deserialize()");
        String scenarioId = item.get("scenarioId").s();
        String title = item.get("title").s();
        String userEmail = item.get("email").s();
        Integer active = item.get("active").n() == null ? 0
                : Integer.parseInt(item.get("active").n());
        Scenario scenarioObj = new Scenario(userEmail, active, scenarioId, title);
        return scenarioObj;
    }

}
