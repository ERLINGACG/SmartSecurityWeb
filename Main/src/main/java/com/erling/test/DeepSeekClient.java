package com.erling.test;

public class DeepSeekClient {
//    public static void main(String[] args) {
//        String apiKey = "sk-61ddc27614b64880b356ce9027a3de97";
//        String url = "https://api.deepseek.com/chat/completions";
//
//        WebClient client = WebClient.builder()
//                .baseUrl(url)
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
//                .build();
//
//        String requestBody = """
//                {
//                    "model": "deepseek-chat",
//                    "messages": [
//                        {"role": "system", "content": "You are a helpful assistant."},
//                        {"role": "user", "content": "Hello!"}
//                    ],
//                    "stream": false
//                }
//                """;
//
//        String response = client.post()
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//        System.out.println(response);
//    }
}
