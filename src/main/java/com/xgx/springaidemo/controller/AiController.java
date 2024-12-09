package com.xgx.springaidemo.controller;


import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
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
    @Autowired
    private ImageModel imageModel;

    @Autowired
    private OpenAiAudioSpeechModel speechModel;
    @Autowired
    private OpenAiAudioTranscriptionModel openAiTranscriptionModel;

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "Hi") String message) {
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


    @GetMapping(value = "/chat/image", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    public String imageModel(@RequestParam(value = "message", defaultValue = "draw a cat") String message) {
        ImageResponse response = imageModel.call(
                new ImagePrompt(message,
                        OpenAiImageOptions.builder()
                                .withQuality("hd")
                                .withN(1)
                                .withHeight(1024)
                                .withWidth(1024).build())
        );

        // 获取图片 URL
        String imageUrl = response.getResult().getOutput().getUrl();

        // 返回包含图片的 HTML 字符串
        return "<html>" +
                "<head><title>Generated Image</title></head>" +
                "<body>" +
                "<h1>Generated Image</h1>" +
                "<img src=\"" + imageUrl + "\" alt=\"Generated Image\"/>" +
                "</body>" +
                "</html>";
    }


    @GetMapping(value = "/chat/text2audio", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    public ResponseEntity<ByteArrayResource> text2audio(@RequestParam(value = "message", defaultValue = "测试一段文生语音") String message) {
        // 配置语音合成选项
        OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
                .withModel("tts-1")
                .withVoice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                .withResponseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .withSpeed(1.0f)
                .build();

        SpeechPrompt speechPrompt = new SpeechPrompt(message, speechOptions);

        // 调用模型生成语音
        byte[] audio = speechModel.call(speechPrompt).getResult().getOutput();

        // 创建一个 ByteArrayResource 来包装音频二进制数据
        ByteArrayResource resource = new ByteArrayResource(audio);

        // 设置 HTTP 响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"audio.mp3\"");

        // 返回文件流
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @GetMapping(value = "/chat/audio2text", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    public String audio2text() {

        OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                // .withLanguage("en")
                // .withPrompt("Ask not this, but ask that")
                .withTemperature(0f)
                .withResponseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .build();
        String audioFile = "/output_audio.mp3";
        AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(new FileSystemResource(audioFile), transcriptionOptions);
        AudioTranscriptionResponse response = openAiTranscriptionModel.call(transcriptionRequest);
        return response.getResult().getOutput();
    }

    @GetMapping(value = "/chat/multi", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    public String multi(@RequestParam(value = "message", defaultValue = "用中文描述图片内容") String message) {
        var imageResource = new ClassPathResource("/spring.jpg");

        var userMessage = new UserMessage(
                message,
                new Media(MimeTypeUtils.IMAGE_JPEG, imageResource));

        ChatResponse response = chatModel.call(new Prompt(userMessage));
        return response.getResult().getOutput().getContent();
    }

    @GetMapping(value = "/chat/func-call")
    public String funcCall(@RequestParam(value = "message", defaultValue = "What's the weather like in San Francisco, Tokyo, and Paris?") String message) {
        ChatResponse response = this.chatClient.prompt(message)
                .functions("CurrentWeather") // Enable the function
                .call().
                chatResponse();
        return response.getResult().getOutput().getContent();
    }
}
