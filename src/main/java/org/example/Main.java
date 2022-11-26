// https://github.com/netology-code/jd-homeworks/tree/master/http/task2
package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URL;

public class Main {
    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=aiGG9TZxBNzmHKSpRpStCTWrma9QR2pmdVVGD236";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
        CloseableHttpResponse response = httpClient.execute(request);

        NasaData nasaData = mapper.readValue(
                response.getEntity().getContent(),
                new TypeReference<>() {
                }
        );

        URL url = new URL(nasaData.getUrl());
        String fileName = nasaData.getUrl().substring(nasaData.getUrl().lastIndexOf('/') + 1);

        try (
                InputStream input = url.openStream();
                FileOutputStream fos = new FileOutputStream(fileName);
        ) {
            byte[] buffer = input.readAllBytes();
            fos.write(buffer);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        response.close();
        httpClient.close();
    }
}
