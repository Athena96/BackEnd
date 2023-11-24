
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
import my.service.processors.SettingsProcessor;
import my.service.services.DDBTables;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@EnableWebMvc
public class SettingsController extends BaseController {

    private static final Logger log = LogManager.getLogger(SettingsController.class);

    private final SettingsProcessor settingsProcessor = new SettingsProcessor();

    @RequestMapping(path = "/getSettings", method = RequestMethod.GET)
    public Settings getSettings(@RequestHeader("Authorization") String token,
            @RequestParam(name = "scenarioId", required = true) String scenarioId) throws Exception {
        try {
            log.info("SettingsController.getSettings()");
            String email = getUserEmail(token);
            return settingsProcessor.getSettings(email, scenarioId);
        } catch (Exception e) {
            log.info("Error in DDBService.queryTypesForUser");
            e.printStackTrace();
            return null;
        }
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
