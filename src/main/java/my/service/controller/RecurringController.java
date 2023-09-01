
package my.service.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.dynamodb.Recurring;

import java.util.Date;
import java.util.List;

@RestController
@EnableWebMvc
public class RecurringController extends BaseController {

    @RequestMapping(path = "/listRecurring", method = RequestMethod.GET)
    public List<Recurring> listRecurring(@RequestHeader("Authorization") String token,
            @RequestParam(name = "scenarioId", required = true) String scenarioId) throws Exception {
        System.out.println("RecurringController");

        Date startTime = new Date();

        String email = getUserEmail(token);
        System.out.println("email: " + email);
        System.out.println("scenarioId: " + scenarioId);

        List<Recurring> listOfRecurrings = ddbService.queryTypesForUser(Recurring.class, email, scenarioId);
        System.out.println(listOfRecurrings);

        Date endTime = new Date();
        System.out.println(
                "RecurringController.listRecurring() Load Time: " + (endTime.getTime() - startTime.getTime()) + "ms");

        return listOfRecurrings;
    }
}
