package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class App implements RequestHandler<Map<String, Object>, Map<String, Object>> {
  @Override
  public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
    context.getLogger().log("Input: " + input);
    System.out.println("Hello from handleRequest");
    System.out.println("Htestttquest");
    String body = (String) input.get("body");
    Map<String, String> bodyMap = new HashMap<>();
    try {
      bodyMap = new ObjectMapper().readValue(body, Map.class);
    } catch (Exception e) {
      context.getLogger().log("Error parsing body: " + e.getMessage());
    }

    String key1 = bodyMap.get("key1");
    String key2 = bodyMap.get("key2");
    System.out.println("key1, " + key1);
    System.out.println("key2, " + key2);

    int num1 = Integer.parseInt(key1);
    int num2 = Integer.parseInt(key2);

    int sum = num1 + num2;
    System.out.println("sum, " + sum);


    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "text/plain");
    headers.put("Access-Control-Allow-Origin", "*"); // Allow all origins
    headers.put("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"); // Allow these methods
    headers.put("Access-Control-Allow-Headers", "*"); // Allow all headers

    Map<String, Object> response = new HashMap<>();
    response.put("isBase64Encoded", false);
    response.put("statusCode", 200);
    response.put("headers", headers);
//    response.put("result", sum);
    response.put("body", sum);
    System.out.println("response, " + response.toString());

    return response;

  }
}
