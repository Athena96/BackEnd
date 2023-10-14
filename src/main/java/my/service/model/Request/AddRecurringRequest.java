


package my.service.model.Request;

import java.util.List;

import my.service.model.LineItem;

public record AddRecurringRequest(String title, Integer startAge, Integer endAge, String chargeType, List<LineItem> lineItems) {
}
