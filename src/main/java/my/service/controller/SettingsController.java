
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@EnableWebMvc
public class SettingsController extends BaseController {

    private static final Logger log = LogManager.getLogger(SettingsController.class);

    @RequestMapping(path = "/getSettings", method = RequestMethod.GET)
    public Settings getSettings(@RequestHeader("Authorization") String token,
            @RequestParam(name = "scenarioId", required = true) String scenarioId) throws Exception {
        log.info("SettingsController");
        log.info("token: " + token);

        Date startTime = new Date();

        String email = getUserEmail(token);
        log.info("email: " + email);
        log.info("scenarioId: " + scenarioId);

        List<Settings> listOfSettings = ddbService.queryTypesForUser(Settings.class, email, scenarioId);
        log.info(listOfSettings);

        Date endTime = new Date();
        log.info(
                "SettingsController.listSettings() Load Time: " + 
                (endTime.getTime() - startTime.getTime()) + "ms");

        return listOfSettings.get(0);
    }

    @RequestMapping(path = "/updateSettings", method = RequestMethod.PUT)
    public UpdateSettingsResponse updateSettings(@RequestHeader("Authorization") String token,
            @RequestBody UpdateSettingsRequest updateSettingsRequest) throws Exception {
        log.info("SettingsController.updateSettings()");

        String email = getUserEmail(token);
        log.info("email: " + email);
        log.info("scenarioDataId: " + updateSettingsRequest.scenarioDataId());

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
            log.info("Error in DDBService.putItem");
            e.printStackTrace();
            return new UpdateSettingsResponse(false);
        }
    }

}
