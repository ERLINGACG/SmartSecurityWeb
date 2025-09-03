package com.erling.service.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {
    @Value("${spring.mqtt.broker}")
    private  String broker; // 服务器地址


    @Value("${spring.mqtt.client-id}")
    private String clientId; // 客户端ID

    @Value("${spring.mqtt.password}")
    private String password; // 密码

    @Value("${spring.mqtt.username}")
    private  String username; // 用户名

    @Value("${spring.mqtt.default-topic}")
    private  String topic;  // 默认主题

    /**
     * 配置mqtt客户端工厂
     * @return MqttPahoClientFactory

     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {                            //  配置mqtt客户端工厂
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();//  创建mqtt客户端工厂
        MqttConnectOptions options = new MqttConnectOptions();                    //  创建mqtt连接选项
        options.setServerURIs(new String[]{broker});                              //  设置mqtt服务器地址
        options.setUserName(username);                                            //  设置mqtt用户名
        options.setPassword(password.toCharArray());                              //  设置mqtt密 码
        options.setConnectionTimeout(10);                                         //  设置连接超时时间
        options.setKeepAliveInterval(20);                                         //  设置心跳时间
        factory.setConnectionOptions(options);                                    //  设置mqtt连接选项
        return factory;
    }
    /**
     * 配置mqtt消息处理器
     * 出站通道：mqttOutboundChannel
     * @return MessageHandler
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(clientId, mqttClientFactory());
        handler.setAsync(true);
        handler.setDefaultTopic(topic);
        return handler;
    }
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * 配置mqtt消息处理器
     * 入站通道：mqttInputChannel
     * @return MessageHandler
     */
    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId + "_inbound", mqttClientFactory(), topic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }



}
