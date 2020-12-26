package com.example.im;

import com.example.im.server.ChatServer;
import com.example.im.server.session.SessionManger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration //自动加载配置
@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ServerApplication.class,args);
		SessionManger sessionManger = context.getBean(SessionManger.class);
		sessionManger.setSingleInstance(sessionManger);
		ChatServer nettyServer = context.getBean(ChatServer.class);
		nettyServer.run();
	}

}
