package com.chatty.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import static lombok.AccessLevel.PRIVATE;

@Profile("default")
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TestContainerConfig {

    ElasticsearchProperties elasticsearchProperties;

    @Bean
    @SneakyThrows
    public Client client(ElasticsearchContainer elasticsearchContainer) {

        int port = elasticsearchContainer.getTcpHost().getPort();

        return new PreBuiltTransportClient(
                Settings.builder()
                        .put("client.transport.sniff", false)
                        .put("cluster.name", elasticsearchContainer.getEnvMap().get("cluster.name"))
                        .build())
                .addTransportAddress(new TransportAddress(InetAddress.getByName(
                        elasticsearchContainer.getTcpHost().getHostName()), port));
    }

    @Bean
    public ElasticsearchContainer elasticsearchContainer() {
        ElasticsearchContainer container = new ElasticsearchContainer(
                "docker.elastic.co/elasticsearch/elasticsearch:6.7.2");
        container.addEnv("cluster.name", elasticsearchProperties.getClusterName());
        container.start();

        InetSocketAddress tcpHost = container.getTcpHost();
        String clusterNodes = tcpHost.getHostName() + ":" + tcpHost.getPort();
        elasticsearchProperties.setClusterNodes(clusterNodes);

        return container;
    }


}
