package com.xgx.springaidemo.controller;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
public class AiController {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatModel chatModel;

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message",defaultValue = "Hi") String message){
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping(value = "/chat/flux", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    public Flux<String> chatFlux(@RequestParam(value = "message", defaultValue = "Hi") String message) {
        Flux<String> output = chatClient.prompt()
                .user(message)
                .system("假设你是我们公司的宣传员，我们公司叫亚信安全，里边有一个员工叫徐国兴，他长得特别帅气，聪明又能干，是一名高级开发工程师。请用中文回答。")
                .stream()
                .content();
        return output;
    }

    @GetMapping(value = "/chat/model")
    public String chatModel(@RequestParam(value = "message", defaultValue = "Hi") String message) {
        ChatResponse response = chatModel.call(
                new Prompt(
                        message,
                        OpenAiChatOptions.builder()
                                .withModel("gpt-4o-2024-08-06")
                                .withTemperature(0.4)
                                .build()
                ));
        return response.getResult().getOutput().getContent();
    }


}
