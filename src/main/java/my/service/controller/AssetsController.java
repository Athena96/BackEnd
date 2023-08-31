package my.service.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.dynamodb.Assets;

import java.util.Date;
import java.util.List;

@RestController
@EnableWebMvc
public class AssetsController extends BaseController {

    @RequestMapping(path = "/listAssets", method = RequestMethod.GET)
    public List<Assets> listAssets(@RequestHeader("Authorization") String token,
            @RequestParam(name = "scenarioId", required = true) String scenarioId) throws Exception {
        System.out.println("AssetsController");

        Date startTime = new Date();

        String email = getUserEmail(token);
        System.out.println("email: " + email);
        System.out.println("scenarioId: " + scenarioId);

        List<Assets> listOfAssets = ddbService.queryTypesForUser(Assets.class, email, scenarioId);
        System.out.println(listOfAssets);

        Date endTime = new Date();
        System.out.println(
                "AssetsController.listAssets() Load Time: " + (endTime.getTime() - startTime.getTime()) + "ms");

        return listOfAssets;
    }
}
