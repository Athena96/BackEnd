package my.service.model;

import java.util.Map;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public interface IDeserializable<T> {
    T deserialize(final String email, final String scenario, Map<String, AttributeValue> item);
}