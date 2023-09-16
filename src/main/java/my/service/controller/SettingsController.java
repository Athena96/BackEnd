
package my.service.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.dynamodb.Settings;

import java.util.Date;
import java.util.List;

@RestController
@EnableWebMvc
public class SettingsController extends BaseController {

    @RequestMapping(path = "/getSettings", method = RequestMethod.GET)
    public Settings getSettings(@RequestHeader("Authorization") String token,
            @RequestParam(name = "scenarioId", required = true) String scenarioId) throws Exception {
        System.out.println("SettingsController");

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
}
