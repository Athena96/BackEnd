package my.service.model;

import java.util.Map;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public interface ISerializable<T> {
    Map<String, AttributeValue> serializable(final String email, T item);
}