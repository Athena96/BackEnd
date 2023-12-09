
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
import my.service.processors.RecurringProcessor;
import my.service.services.DDBTables;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@EnableWebMvc
public class RecurringController extends BaseController {

    private static final Logger log = LogManager.getLogger(RecurringController.class);

    private final RecurringProcessor recurringProcessor = new RecurringProcessor(this.ddbService);

    @RequestMapping(path = "/listRecurring", method = RequestMethod.GET)
    public List<Recurring> listRecurring(@RequestHeader("Authorization") String accessToken, 
    @RequestHeader("idtoken") String token,
            @RequestParam(name = "scenarioId", required = true) String scenarioId) throws Exception {
        try {
            log.info("RecurringController.listRecurring()");
            String email = getUserEmail(token);
            return recurringProcessor.listRecurrings(email, scenarioId);
        } catch (Exception e) {
            log.info("Error in DDBService.queryTypesForUser");
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(path = "/addRecurring", method = RequestMethod.POST)
    public MTAPIResponse addRecurring(@RequestHeader("Authorization") String accessToken, 
    @RequestHeader("idtoken") String token,
            @RequestBody AddRecurringRequest addRecurringRequest) throws Exception {
        try {
            log.info("RecurringController.addRecurring()");
            String email = getUserEmail(token);
            recurringProcessor.addRecurring(email,
                    addRecurringRequest.scenarioId(),
                    addRecurringRequest.title(),
                    addRecurringRequest.startAge(),
                    addRecurringRequest.endAge(),
                    "EXPENSE".equals(addRecurringRequest.chargeType()) ? ChargeType.EXPENSE : ChargeType.INCOME,
                    addRecurringRequest.amount());
            return new MTAPIResponse(true);
        } catch (Exception e) {
            log.info("Error in DDBService.addItem");
            e.printStackTrace();
            return new MTAPIResponse(false);
        }
    }

    @RequestMapping(path = "/updateRecurring", method = RequestMethod.PUT)
    public MTAPIResponse updateRecurring(@RequestHeader("Authorization") String accessToken, 
    @RequestHeader("idtoken") String token,
            @RequestBody UpdateRecurringRequest updateRecurringRequest) throws Exception {
        log.info("RecurringController.updateRecurring()");

        String email = getUserEmail(token);
        log.info("email: " + email);
        log.info("scenarioDataId: " + updateRecurringRequest.scenarioDataId());

        String id = updateRecurringRequest.type().split("#")[1];

        Recurring recurring = new Recurring(
                updateRecurringRequest.scenarioDataId(),
                updateRecurringRequest.type(),
                id,
                updateRecurringRequest.title(),
                updateRecurringRequest.startAge(),
                updateRecurringRequest.endAge(),
                "EXPENSE".equals(
                        updateRecurringRequest.chargeType()) ? ChargeType.EXPENSE : ChargeType.INCOME,
                updateRecurringRequest.amount());

        try {
            ddbService.putItem(Recurring.class, DDBTables.getDataTableName(), email, recurring);
            return new MTAPIResponse(true);
        } catch (Exception e) {
            log.info("Error in DDBService.addItem");
            e.printStackTrace();
            return new MTAPIResponse(false);
        }
    }

    @RequestMapping(path = "/deleteRecurring", method = RequestMethod.DELETE)
    public MTAPIResponse deleteRecurring(@RequestHeader("Authorization") String accessToken, 
    @RequestHeader("idtoken") String token,
            @RequestBody DeleteRecurringRequest deleteRecurringRequest) throws Exception {

        try {
            log.info("RecurringController.deleteRecurring()");

            String email = getUserEmail(token);
            recurringProcessor.deleteRecurring(email,
                    deleteRecurringRequest.scenarioDataId(),
                    deleteRecurringRequest.type());
            return new MTAPIResponse(true);
        } catch (Exception e) {
            log.info("Error in deleteRecurring()");
            e.printStackTrace();
            return new MTAPIResponse(false);
        }
    }

}
