
package my.service.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.ChargeType;

import my.service.model.Request.AddRecurringRequest;
import my.service.model.Request.DeleteRecurringRequest;
import my.service.model.Request.UpdateRecurringRequest;
import my.service.model.Response.MTAPIResponse;

import my.service.model.dynamodb.Recurring;
import my.service.services.DDBTables;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    @RequestMapping(path = "/addRecurring", method = RequestMethod.POST)
    public MTAPIResponse addRecurring(@RequestHeader("Authorization") String token,
            @RequestBody AddRecurringRequest addRecurringRequest) throws Exception {
        System.out.println("RecurringController.addRecurring()");

        String email = getUserEmail(token);
        System.out.println("email: " + email);
        System.out.println("scenarioDataId: " + addRecurringRequest.scenarioDataId());

        String id = UUID.randomUUID().toString();
        String scenarioDataId = addRecurringRequest.scenarioDataId();
        String type = "Recurring" + "#" + id;

        Recurring recurring = new Recurring(
                scenarioDataId,
                type,
                id,
                addRecurringRequest.title(),
                addRecurringRequest.startAge(),
                addRecurringRequest.endAge(),
                "EXPENSE".equals(addRecurringRequest.chargeType()) ? ChargeType.EXPENSE : ChargeType.INCOME,
                addRecurringRequest.amount());

        try {
            ddbService.putItem(Recurring.class, DDBTables.getDataTableName(), email, recurring);
            return new MTAPIResponse(true);
        } catch (Exception e) {
            System.out.println("Error in DDBService.addItem");
            e.printStackTrace();
            return new MTAPIResponse(false);
        }
    }

    @RequestMapping(path = "/updateRecurring", method = RequestMethod.PUT)
    public MTAPIResponse updateRecurring(@RequestHeader("Authorization") String token,
            @RequestBody UpdateRecurringRequest updateRecurringRequest) throws Exception {
        System.out.println("RecurringController.updateRecurring()");

        String email = getUserEmail(token);
        System.out.println("email: " + email);
        System.out.println("scenarioDataId: " + updateRecurringRequest.scenarioDataId());

        String id = updateRecurringRequest.type().split("#")[1];

        Recurring recurring = new Recurring(
                updateRecurringRequest.scenarioDataId(),
                updateRecurringRequest.type(),
                id,
                updateRecurringRequest.title(),
                updateRecurringRequest.startAge(),
                updateRecurringRequest.endAge(),
                "EXPENSE".equals(updateRecurringRequest.chargeType()) ? ChargeType.EXPENSE : ChargeType.INCOME,
                updateRecurringRequest.amount());

        try {
            ddbService.putItem(Recurring.class, DDBTables.getDataTableName(), email, recurring);
            return new MTAPIResponse(true);
        } catch (Exception e) {
            System.out.println("Error in DDBService.addItem");
            e.printStackTrace();
            return new MTAPIResponse(false);
        }
    }

    @RequestMapping(path = "/deleteRecurring", method = RequestMethod.DELETE)
    public MTAPIResponse deleteRecurring(@RequestHeader("Authorization") String token,
            @RequestBody DeleteRecurringRequest deleteRecurringRequest) throws Exception {
        System.out.println("RecurringController.deleteRecurring()");

        String email = getUserEmail(token);
        System.out.println("deleteRecurringRequest.scenarioDataId(): " + deleteRecurringRequest.scenarioDataId());
        System.out.println("deleteRecurringRequest.type(): " + deleteRecurringRequest.type());

        try {
            ddbService.deleteItem(Recurring.class, email, deleteRecurringRequest.scenarioDataId(),
                    deleteRecurringRequest.type());
            return new MTAPIResponse(true);
        } catch (Exception e) {
            System.out.println("Error in DDBService.addItem");
            e.printStackTrace();
            return new MTAPIResponse(false);
        }
    }

}
