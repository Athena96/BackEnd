
package my.service.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.dynamodb.OneTime;

import java.util.Date;
import java.util.List;

@RestController
@EnableWebMvc
public class OneTimeController extends BaseController {

    @RequestMapping(path = "/listOneTime", method = RequestMethod.GET)
    public List<OneTime> listOneTime(@RequestHeader("Authorization") String token,
            @RequestParam(name = "scenarioId", required = true) String scenarioId) throws Exception {
        System.out.println("OneTimeController");

        Date startTime = new Date();

        String email = getUserEmail(token);
        System.out.println("email: " + email);
        System.out.println("scenarioId: " + scenarioId);

        List<OneTime> listOfOneTime = ddbService.queryTypesForUser(OneTime.class, email, scenarioId);
        System.out.println(listOfOneTime);

        Date endTime = new Date();
        System.out.println(
                "OneTimeController.listOneTime() Load Time: " + (endTime.getTime() - startTime.getTime()) + "ms");

        return listOfOneTime;
    }
}
