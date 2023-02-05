package com.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dto.currency.ExchangeRateDTO;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Component
public class ExchangeClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/usd.json")
            .build();
    private final ObjectMapper mapper;

    public ExchangeClient(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ExchangeRateDTO getExchangeRates() {

        return webClient
                .get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> jsonNode.get("usd"))
                .map(s->{
                    try {
                        return mapper.readValue(s.traverse(), new TypeReference<>() {} );
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new ExchangeRateDTO();
                    }
                })
                .block();
    }
}
