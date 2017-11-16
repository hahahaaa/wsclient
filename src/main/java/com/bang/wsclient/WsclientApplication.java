package com.bang.wsclient;

import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import com.alibaba.fastjson.JSONObject;

@SpringBootApplication
public class WsclientApplication implements CommandLineRunner {

    final CountDownLatch messageLatch = new CountDownLatch(1);

    public static void main(String[] args) {
        new SpringApplicationBuilder(WsclientApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }


    @Override
    public void run(String... strings) throws Exception {
        start();
    }


    public void start() throws IOException, DeploymentException, InterruptedException {
//        try {
//            WebSocketContainer container = ContainerProvider.getWebSocketContainer(); // 获取WebSocket连接器，其中具体实现可以参照websocket-api.jar的源码,Class.forName("org.apache.tomcat.websocket.WsWebSocketContainer");
//            String uri = "ws://localhost:8084/websocket";
//            Session session = container.connectToServer(Client.class, new URI(uri)); // 连接会话
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        WebSocketContainer container = ContainerProvider.getWebSocketContainer(); // 获取WebSocket连接器，其中具体实现可以参照websocket-api.jar的源码,Class.forName("org.apache.tomcat.websocket.WsWebSocketContainer");

        ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().configurator(new ClientEndpointConfig.Configurator() {
            @Override
            public void beforeRequest(Map<String, List<String>> headers) {
                List<String> list = new ArrayList<>();
                list.add("NTEzNDhfNzI2NjE4MzRfMTUwNzUzODY3NzY2Mw==");
                headers.put("token", list);
            }
        }).build();

        container.connectToServer(
                new Endpoint() {
                    @Override
                    public void onOpen(Session session, EndpointConfig config) {
                        try {
                            session.addMessageHandler(new MessageHandler.Whole<String>() {
                                @Override
                                public void onMessage(String message) {
                                    System.out.println("# 客户端收到信息: " + message);
                                    messageLatch.countDown();
                                }
                            });
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("t","chat");
                            //礼物id
                            jsonObject.put("c","23");
                            //userToId
                            jsonObject.put("uid","584337");
                            jsonObject.put("rid","1104707");
                            //私聊类型
                            jsonObject.put("chattype","1");
                            System.out.println(jsonObject.toJSONString());
                            session.getBasicRemote().sendText(jsonObject.toJSONString());
//                            System.out.println("# client sent: in-memory echo!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // "inmemory" acts here as a hostname, will be removed in InMemoryClientContainer.
                    }
                }, cec, URI.create("wss://api-low.765qlw.com/websocket"));
//        URI.create("wss://api-svip.765qlw.com/websocket")
    }
}
