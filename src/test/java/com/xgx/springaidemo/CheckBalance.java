package com.xgx.springaidemo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckBalance {

    private static final String BALANCE_API_URL = "https://info.yesapikey.com";  // 假设余额查询API的URL
    private static final String API_KEY = "sk-dBHzeZWdYHs7upiy33B843Da56764cF986017343858c84F9"; // 输入你的网站发给你的API key

    public static void main(String[] args) {
        String balance = getBalance();
        System.out.println("API Key余额: " + balance);
    }

    public static String getBalance() {
        try {
            HttpURLConnection connection = createConnection();

            // 发送请求
            String response = getResponse(connection);
            System.out.println("返回内容: " + response);

            // 假设返回的 JSON 格式包含一个名为 'balance' 的字段
            // 你可以根据实际返回的字段解析余额
            return parseBalance(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "查询失败";
        }
    }

    private static HttpURLConnection createConnection() throws Exception {
        URL url = new URL(BALANCE_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
        connection.setDoOutput(true);
        return connection;
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

    private static String parseBalance(String response) {
        // 假设API的返回格式为 {"balance": 100}
        // 你可以根据实际的返回格式调整解析方式
        int balanceStart = response.indexOf("balance\":") + 9;
        int balanceEnd = response.indexOf("}", balanceStart);
        return response.substring(balanceStart, balanceEnd).trim();
    }
}
