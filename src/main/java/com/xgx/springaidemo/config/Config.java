package com.xgx.springaidemo.config;

import com.xgx.springaidemo.func.MockWeatherService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class Config {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

	@Bean
	public FunctionCallback weatherFunctionInfo() {

		return FunctionCallback.builder()
				.function("CurrentWeather", new MockWeatherService()) // (1) function name and instance
				.description("Get the weather in location") // (2) function description
				.inputType(MockWeatherService.Request.class) // (3) input type to build the JSON schema
				.build();
	}

}