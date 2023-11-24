package my.service.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.Request.AddScenarioRequest;
import my.service.model.Request.ChangeActiveScenarioRequest;
import my.service.model.Request.DeleteScenarioRequest;
import my.service.model.Request.UpdateScenarioRequest;
import my.service.model.dynamodb.Scenario;
import my.service.processors.ScenarioProcessor;
import my.service.services.DDBTables;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@EnableWebMvc
public class ScenariosController extends BaseController {

    private static final Logger log = LogManager.getLogger(ScenariosController.class);

    private final ScenarioProcessor scenarioProcessor = new ScenarioProcessor();

    @RequestMapping(path = "/listScenarios", method = RequestMethod.GET)
    public List<Scenario> listScenarios(@RequestHeader("Authorization") String token) throws Exception {

        log.info("ScenariosController");

        String email = getUserEmail(token);

        List<Scenario> listOfScenarios = scenarioProcessor.listScenarios(email);

        if (listOfScenarios.size() == 0) {
            log.info("No scenarios found for user: " + email);
            log.info("Setting up initial scenario");
            listOfScenarios.add(setUpInitialScenario(email));
        }
        return listOfScenarios;
    }

    @RequestMapping(path = "/addScenario", method = RequestMethod.POST)
    public Scenario addScenario(@RequestHeader("Authorization") String token,
            @RequestBody AddScenarioRequest addScenarioRequest) throws Exception {
        log.info("ScenariosController.addScenario()");
        String email = getUserEmail(token);
        String title = addScenarioRequest.title();
        Scenario scenario = scenarioProcessor.addScenario(email, title);
        return scenario;
    }

    @RequestMapping(path = "/changeActiveScenario", method = RequestMethod.PUT)
    public void changeActiveScenario(@RequestHeader("Authorization") String token,
            @RequestBody ChangeActiveScenarioRequest changeActiveScenarioRequest) throws Exception {
        try {
            log.info("ScenariosController.changeActiveScenario()");
            String email = getUserEmail(token);
            scenarioProcessor.changeActiveScenario(email, changeActiveScenarioRequest.idOfNewActiveScenario());
        } catch (Exception e) {
            log.info("Error in DDBService.addItem");
            e.printStackTrace();
            throw e;
        }
    }

    @RequestMapping(path = "/updateScenario", method = RequestMethod.PUT)
    public void updateScenario(@RequestHeader("Authorization") String token,
            @RequestBody UpdateScenarioRequest updateScenarioRequest) throws Exception {
        try {
            log.info("ScenariosController.updateScenario()");
            String email = getUserEmail(token);
            scenarioProcessor.updateScenario(email, updateScenarioRequest.scenarioId(),
                    updateScenarioRequest.title(),
                    updateScenarioRequest.active());
        } catch (Exception e) {
            log.info("Error in DDBService.addItem");
            e.printStackTrace();
            throw e;
        }
    }

    @RequestMapping(path = "/deleteScenario", method = RequestMethod.DELETE)
    public void deleteScenario(@RequestHeader("Authorization") String token,
            @RequestBody DeleteScenarioRequest deleteScenarioRequest) throws Exception {
        try {
            log.info("ScenariosController.deleteScenario()");
            String email = getUserEmail(token);
            scenarioProcessor.deleteScenario(email, deleteScenarioRequest.scenarioId());
        } catch (Exception e) {
            log.info("Error in DDBService.addItem");
            e.printStackTrace();
            throw e;
        }
    }

    private Scenario setUpInitialScenario(String email) throws Exception {
        try {
            String scenarioId = UUID.randomUUID().toString();
            Scenario newScenario = new Scenario(email, 1, scenarioId, "Default Scenario");
            ddbService.putItem(Scenario.class, DDBTables.getScenarioTableName(), email, newScenario);
            log.info("Created new Scenario: " + newScenario.title);

            return newScenario;
        } catch (Exception e) {
            log.info("Error in DDBService.addItem");
            e.printStackTrace();
            throw e;
        }
    }
}
