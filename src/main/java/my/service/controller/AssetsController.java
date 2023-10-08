package my.service.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.DataType;
import my.service.model.Request.AddAssetRequest;
import my.service.model.Request.DeleteAssetRequest;
import my.service.model.Request.UpdateAssetRequest;
import my.service.model.Response.AddAssetResponse;
import my.service.model.Response.DeleteAssetResponse;
import my.service.model.Response.UpdateAssetResponse;
import my.service.model.dynamodb.Assets;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@EnableWebMvc
public class AssetsController extends BaseController {

    @RequestMapping(path = "/listAssets", method = RequestMethod.GET)
    public List<Assets> listAssets(@RequestHeader("Authorization") String token,
            @RequestParam(name = "scenarioId", required = true) String scenarioId) throws Exception {
        System.out.println("AssetsController.listAssets()");

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

    @RequestMapping(path = "/addAsset", method = RequestMethod.POST)
    public AddAssetResponse addAsset(@RequestHeader("Authorization") String token,
            @RequestParam(name = "scenarioId", required = true) String scenarioId,
            @RequestBody AddAssetRequest addAssetRequest) throws Exception {
        System.out.println("AssetsController.addAsset()");

        String email = getUserEmail(token);
        System.out.println("email: " + email);
        System.out.println("scenarioId: " + scenarioId);

        String id = UUID.randomUUID().toString();
        String scenarioDataId = email + "#" + scenarioId;
        Assets asset = new Assets(
                scenarioDataId,
                DataType.Assets,
                id,
                addAssetRequest.ticker(),
                addAssetRequest.quantity(),
                1.0,
                addAssetRequest.hasIndexData());

        try {
            ddbService.putItem(Assets.class, email, scenarioId, asset);
            return new AddAssetResponse(true);
        } catch (Exception e) {
            System.out.println("Error in DDBService.addItem");
            e.printStackTrace();
            return new AddAssetResponse(false);
        }
    }

    @RequestMapping(path = "/updateAsset", method = RequestMethod.PUT)
    public UpdateAssetResponse updateAsset(@RequestHeader("Authorization") String token,
            @RequestParam(name = "scenarioId", required = true) String scenarioId,
            @RequestBody UpdateAssetRequest updateAssetRequest) throws Exception {
        System.out.println("AssetsController.updateAsset()");

        String email = getUserEmail(token);
        System.out.println("email: " + email);
        System.out.println("scenarioId: " + scenarioId);

        Assets asset = new Assets(
                updateAssetRequest.scenarioDataId(),
                DataType.Assets,
                updateAssetRequest.typeId(),
                updateAssetRequest.ticker(),
                updateAssetRequest.quantity(),
                1.0,
                updateAssetRequest.hasIndexData());

        try {
            ddbService.putItem(Assets.class, email, scenarioId, asset);
            return new UpdateAssetResponse(true);
        } catch (Exception e) {
            System.out.println("Error in DDBService.putItem");
            e.printStackTrace();
            return new UpdateAssetResponse(false);
        }
    }

    @RequestMapping(path = "/deleteAsset", method = RequestMethod.DELETE)
    public DeleteAssetResponse deleteAsset(@RequestHeader("Authorization") String token,
            @RequestParam(name = "scenarioId", required = true) String scenarioId,
            @RequestBody DeleteAssetRequest deleteAssetRequest) throws Exception {
        System.out.println("AssetsController.deleteAsset()");

        String email = getUserEmail(token);
        System.out.println("deleteAssetRequest.scenarioDataId(): " + deleteAssetRequest.scenarioDataId());
        System.out.println("deleteAssetRequest.type(): " + deleteAssetRequest.type());

        try {
            ddbService.deleteItem(Assets.class, email, deleteAssetRequest.scenarioDataId(), deleteAssetRequest.type());
            return new DeleteAssetResponse(true);
        } catch (Exception e) {
            System.out.println("Error in DDBService.addItem");
            e.printStackTrace();
            return new DeleteAssetResponse(false);
        }
    }
}
