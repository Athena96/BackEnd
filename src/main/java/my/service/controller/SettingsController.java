
package my.service.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.Request.UpdateSettingsRequest;
import my.service.model.Response.UpdateSettingsResponse;
import my.service.model.dynamodb.Settings;
import my.service.services.DDBTables;

import java.util.Date;
import java.util.List;

@RestController
@EnableWebMvc
public class SettingsController extends BaseController {

    @RequestMapping(path = "/getSettings", method = RequestMethod.GET)
    public Settings getSettings(@RequestHeader("Authorization") String token,
            @RequestParam(name = "scenarioId", required = true) String scenarioId) throws Exception {
        System.out.println("SettingsController");
        System.out.println("token: " + token);

        Date startTime = new Date();

        String email = getUserEmail(token);
        System.out.println("email: " + email);
        System.out.println("scenarioId: " + scenarioId);

        List<Settings> listOfSettings = ddbService.queryTypesForUser(Settings.class, email, scenarioId);
        System.out.println(listOfSettings);

        Date endTime = new Date();
        System.out.println(
                "SettingsController.listSettings() Load Time: " + (endTime.getTime() - startTime.getTime()) + "ms");

        return listOfSettings.get(0);
    }

    @RequestMapping(path = "/updateSettings", method = RequestMethod.PUT)
    public UpdateSettingsResponse updateSettings(@RequestHeader("Authorization") String token,
            @RequestBody UpdateSettingsRequest updateSettingsRequest) throws Exception {
        System.out.println("SettingsController.updateSettings()");

        String email = getUserEmail(token);
        System.out.println("email: " + email);
        System.out.println("scenarioDataId: " + updateSettingsRequest.scenarioDataId());

        Settings settings = new Settings(
                updateSettingsRequest.scenarioDataId(),
                updateSettingsRequest.type(),
                updateSettingsRequest.birthday(),
                updateSettingsRequest.annualAssetReturnPercent(),
                updateSettingsRequest.annualInflationPercent());

        try {
            ddbService.putItem(Settings.class, DDBTables.getDataTableName(), email, settings);
            return new UpdateSettingsResponse(true);
        } catch (Exception e) {
            System.out.println("Error in DDBService.putItem");
            e.printStackTrace();
            return new UpdateSettingsResponse(false);
        }
    }

}
