package com.xgx.springaidemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiDemoApplication.class, args);
    }

    // @PostConstruct
    // public void setProxy() {
    //     // HTTP 代理
    //     System.setProperty("http.proxyHost", "127.0.0.1");
    //     System.setProperty("http.proxyPort", "10900");
    //
    //     // HTTPS 代理
    //     System.setProperty("https.proxyHost", "127.0.0.1");
    //     System.setProperty("https.proxyPort", "10900");
    //
    //     // 如果需要 SOCKS 代理
    //     System.setProperty("socksProxyHost", "127.0.0.1");
    //     System.setProperty("socksProxyPort", "10900");
    //
    //     System.out.println("Proxy configured successfully!");
    // }

}
