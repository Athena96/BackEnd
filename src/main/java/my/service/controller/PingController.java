package my.service.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import my.service.model.Ping;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@EnableWebMvc
public class PingController {

    private static final Logger log = LogManager.getLogger(PingController.class);

    @RequestMapping(path = "/ping", method = RequestMethod.POST)
public Map<String, String> ping(@RequestBody Ping ping) {

                log.info("PingjffranzoController");

        log.info(ping);
        log.info(ping.toString());
        log.info("num1" + ping.num1());
        log.info("num2" + ping.num2());

        Integer sum = ping.num1() + ping.num2();
        String strSum = "" + sum;
        log.info("sum = " + sum);

        Map<String, String> pong = new HashMap<>();
        pong.put("result", strSum);

        log.info(pong.toString());

        return pong;
    }
}
