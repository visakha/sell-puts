package com.putsellersim;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.spring.SpringFxWeaver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PutSellerSimApplication extends Application {

    private ConfigurableApplicationContext context;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() {
        context = SpringApplication.run(PutSellerSimApplication.class);
    }

    @Override
    public void start(Stage stage) {
        FxWeaver fxWeaver = context.getBean(FxWeaver.class);
        javafx.scene.Parent root = fxWeaver.loadView(com.putsellersim.ui.MainView.class);
        stage.setScene(new javafx.scene.Scene(root, 1100, 600)); // wider and shorter
        stage.setTitle("Put Seller Sim");
        stage.show();
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }
}
