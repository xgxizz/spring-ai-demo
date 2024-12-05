package com.xgx.springaidemo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatGPTClient {
    
    private static final String API_URL = "https://api.yesapikey.com/v1/chat/completions";
    private static final String API_KEY = "sk-dBHzeZWdYHs7upiy33B843Da56764cF986017343858c84F9"; // 输入网站发给你的转发key
    
    public static void main(String[] args) {
        String prompt = "你好";
        String response = getChatGptResponse(prompt);
        System.out.println(response);
    }

    public static String getChatGptResponse(String prompt) {
        while (true) {
            try {
                HttpURLConnection connection = createConnection();
                JSONObject data = new JSONObject();
                data.put("model", "gpt-4o");
                JSONArray messages = new JSONArray();
                JSONObject message = new JSONObject();
                message.put("role", "user");
                message.put("content", prompt);
                messages.put(message);
                data.put("messages", messages);

                // 发送请求
                sendRequest(connection, data);

                // 获取响应
                String response = getResponse(connection);
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.has("choices") && jsonResponse.getJSONArray("choices").length() > 0) {
                    JSONObject choice = jsonResponse.getJSONArray("choices").getJSONObject(0);
                    if (choice.has("message")) {
                        String content = choice.getJSONObject("message").getString("content");
                        System.out.println(response); // 完整返回值
                        System.out.println(content);  // 补全内容
                        return content;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // 打印错误信息
            }

            try {
                // 等待2秒
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static HttpURLConnection createConnection() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
        connection.setDoOutput(true);
        return connection;
    }

    private static void sendRequest(HttpURLConnection connection, JSONObject data) throws Exception {
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = data.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
    }

    private static String getResponse(HttpURLConnection connection) throws Exception {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }
    }
}
