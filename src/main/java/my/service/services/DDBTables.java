package my.service.services;

// static class for ddb table names
public class DDBTables {

   
    public static String getDataTableName() {
        return System.getenv("DATA_TABLE");
    }

    public static String getScenarioTableName() {
        return System.getenv("SCENARIO_TABLE");
    }
}