package com.restu.fintech;

import com.restu.fintech.auth_users.entity.User;
import com.restu.fintech.enums.NotificationType;
import com.restu.fintech.notification.dtos.NotificationDTO;
import com.restu.fintech.notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
public class FintechApplication {

    private final NotificationService notificationService;

	public static void main(String[] args) {
		SpringApplication.run(FintechApplication.class, args);
	}

}
