package com.github.jan_cin.FinnhubKafkaConnector.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHandler {

    public static String getResponse(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        InputStream responseStream;
        if (connection.getResponseCode() == 200) {
            responseStream = connection.getInputStream();
        } else {
            responseStream = connection.getErrorStream();
        }

        StringBuilder sb;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream))) {
            sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
        }
        return sb.toString();
    }
}