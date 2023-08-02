package my.service.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.HashMap;
import java.util.Map;

@RestController
@EnableWebMvc
public class PingController {

    @RequestMapping(path = "/ping", method = RequestMethod.POST)
    public Map<String, String> ping(@RequestBody Ping ping) {
        System.out.println("PingController");
        System.out.println(ping);
        System.out.println(ping.toString());
        System.out.println("num1" + ping.num1());
        System.out.println("num2" + ping.num2());

        Integer sum = ping.num1() + ping.num2();
        String strSum = "" + sum;
        System.out.println("sum = " + sum);

        Map<String, String> pong = new HashMap<>();
        pong.put("result", strSum);

        System.out.println(pong.toString());

        return pong;
    }
}
