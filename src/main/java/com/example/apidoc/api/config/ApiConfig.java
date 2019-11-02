package com.example.apidoc.api.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Data
@Slf4j
@Component
public class ApiConfig implements ApplicationListener<WebServerInitializedEvent> {

    @Value("${api.projectName}")
    private String projectName;

    private int serverPort;

    private String address;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        // ip
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String hostAddress = address.getHostAddress();
        this.address = hostAddress;
        // port
        this.serverPort = event.getWebServer().getPort();

        log.info(getUrl() + "/api");
    }

    public String getUrl() {
        return "http://" + this.address + ":" + this.serverPort;
    }
}
