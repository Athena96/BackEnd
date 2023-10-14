package my.service.utils;

import my.service.model.ChargeType;
import my.service.model.DataType;

 public  class Utils  {


    public static DataType getDataTypeFromString(String dataTyopeString) {
        switch (dataTyopeString) {
            case "OneTime":
                return DataType.OneTime;
            case "Recurring":
                return DataType.Recurring;
            case "Assets":
                return DataType.Assets;
            case "Settings":
                return DataType.Settings;
            default:
                return null;
        }
    }

    public static ChargeType getChargeTypeFromString(String chargeTypeString) {
        switch (chargeTypeString) {
            case "EXPENSE":
                return ChargeType.EXPENSE;
            case "INCOME":
                return ChargeType.INCOME;
            default:
                return null;
        }
    }

    
}
