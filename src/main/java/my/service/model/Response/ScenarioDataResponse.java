package my.service.model.Response;

import java.util.List;

import my.service.model.dynamodb.Assets;
import my.service.model.dynamodb.Recurring;
import my.service.model.dynamodb.Settings;

public record ScenarioDataResponse(
        Settings settings,
        List<Assets> assets,
        List<Recurring> recurrings
) {
}
