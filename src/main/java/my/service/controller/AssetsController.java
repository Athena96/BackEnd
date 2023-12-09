package my.service.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.Request.AddAssetRequest;
import my.service.model.Request.DeleteAssetRequest;
import my.service.model.Request.UpdateAssetRequest;
import my.service.model.Response.AddAssetResponse;
import my.service.model.Response.DeleteAssetResponse;
import my.service.model.Response.UpdateAssetResponse;
import my.service.model.dynamodb.Assets;
import my.service.processors.AssetProcessor;
import my.service.services.DDBTables;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@EnableWebMvc
public class AssetsController extends BaseController {
    private static final Logger log = LogManager.getLogger(AssetsController.class);

    private final AssetProcessor assetProcessor = new AssetProcessor(ddbService);

    @RequestMapping(path = "/listAssets", method = RequestMethod.GET)
    public List<Assets> listAssets(@RequestHeader("Authorization") String accessToken,
     @RequestHeader("idtoken") String token,
            @RequestParam(name = "scenarioId", required = true) String scenarioId) throws Exception {
        try {
            log.info("AssetsController.listAssets()");
            String email = getUserEmail(token);
            return assetProcessor.listAssets(email, scenarioId);
        } catch (Exception e) {
            log.info("Error in DDBService.queryTypesForUser");
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(path = "/addAsset", method = RequestMethod.POST)
    public AddAssetResponse addAsset(@RequestHeader("Authorization") String accessToken,
     @RequestHeader("idtoken") String token,
            @RequestBody AddAssetRequest addAssetRequest) throws Exception {
        try {
            log.info("AssetsController.addAsset()");
            String email = getUserEmail(token);
            assetProcessor.addAsset(email,
                    addAssetRequest.scenarioId(),
                    addAssetRequest.ticker(),
                    addAssetRequest.quantity(),
                    addAssetRequest.hasIndexData());
            return new AddAssetResponse(true);
        } catch (Exception e) {
            log.info("Error in DDBService.addItem");
            e.printStackTrace();
            return new AddAssetResponse(false);
        }
    }

    @RequestMapping(path = "/updateAsset", method = RequestMethod.PUT)
    public UpdateAssetResponse updateAsset(@RequestHeader("Authorization") String accessToken,
     @RequestHeader("idtoken") String token,
            @RequestBody UpdateAssetRequest updateAssetRequest) throws Exception {
        log.info("AssetsController.updateAsset()");
        String email = getUserEmail(token);
        String id = updateAssetRequest.type().split("#")[1];
        Assets asset = new Assets(
                updateAssetRequest.scenarioDataId(),
                updateAssetRequest.type(),
                id,
                updateAssetRequest.ticker(),
                updateAssetRequest.quantity(),
                1.0,
                updateAssetRequest.hasIndexData());
        try {
            ddbService.putItem(Assets.class, DDBTables.getDataTableName(), email, asset);
            return new UpdateAssetResponse(true);
        } catch (Exception e) {
            log.info("Error in DDBService.putItem");
            e.printStackTrace();
            return new UpdateAssetResponse(false);
        }
    }

    @RequestMapping(path = "/deleteAsset", method = RequestMethod.DELETE)
    public DeleteAssetResponse deleteAsset(@RequestHeader("Authorization") String accessToken, 
    @RequestHeader("idtoken") String token,
            @RequestBody DeleteAssetRequest deleteAssetRequest) throws Exception {

        try {
            log.info("AssetsController.deleteAsset()");

            String email = getUserEmail(token);
            assetProcessor.deleteAsset(email,
                    deleteAssetRequest.scenarioDataId(),
                    deleteAssetRequest.type());
            return new DeleteAssetResponse(true);
        } catch (Exception e) {
            log.info("Error in DDBService.addItem");
            e.printStackTrace();
            return new DeleteAssetResponse(false);
        }
    }
}
