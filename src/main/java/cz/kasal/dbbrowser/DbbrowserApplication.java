package cz.kasal.dbbrowser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "cz.kasal.dbbrowser")
public class DbbrowserApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbbrowserApplication.class, args);
    }

}
