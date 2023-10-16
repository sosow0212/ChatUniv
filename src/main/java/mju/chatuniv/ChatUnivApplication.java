package mju.chatuniv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChatUnivApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatUnivApplication.class, args);
    }
}
