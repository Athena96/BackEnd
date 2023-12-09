package my.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import my.service.controller.AssetsController;
import my.service.controller.BaseController;
import my.service.controller.PingController;
import my.service.controller.RecurringController;
import my.service.controller.ScenariosController;
import my.service.controller.ScenariosDataController;
import my.service.controller.SettingsController;

@SpringBootApplication
// We use direct @Import instead of @ComponentScan to speed up cold starts
// @ComponentScan(basePackages = "my.service.controller")
@Import({ PingController.class, AssetsController.class, BaseController.class, RecurringController.class,
        ScenariosController.class, ScenariosDataController.class, SettingsController.class })
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}