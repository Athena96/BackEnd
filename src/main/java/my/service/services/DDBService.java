package my.service.services;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.service.model.IDeserializable;

public class DDBService {

    private final DynamoDbClient dynamoDbClient;

    public DDBService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public <T extends IDeserializable<T>> List<T> queryTypesForUser(Class<T> clazz, String email, String scenarioId)
            throws IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

        String type = clazz.getSimpleName();
        System.out.println("Query for type: " + type);
        String scenarioDataId = email + "#" + scenarioId;

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":emailScenarioIdValue", AttributeValue.builder().s(scenarioDataId).build());
        expressionAttributeValues.put(":typeValue", AttributeValue.builder().s(type).build());

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(System.getenv("DATA_TABLE"))
                .keyConditionExpression(
                        "scenarioDataId = :emailScenarioIdValue and begins_with(#type, :typeValue)")
                .expressionAttributeNames(Map.of("#type", "type"))
                .expressionAttributeValues(
                        expressionAttributeValues)
                .build();

        QueryResponse scenarioqueryResponse = dynamoDbClient.query(queryRequest);

        List<T> results = new ArrayList<>();

        try {
            for (Map<String, AttributeValue> item : scenarioqueryResponse.items()) {
                T instance = clazz.getDeclaredConstructor().newInstance(); // Create an instance of type T
                T obj = instance.deserialize(email, scenarioId, item); // Deserialize the item map into an object of
                                                                       // type T
                results.add(obj); // Add the object to the results
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace(); // Handle exceptions
        }

        return results;
    }
}
