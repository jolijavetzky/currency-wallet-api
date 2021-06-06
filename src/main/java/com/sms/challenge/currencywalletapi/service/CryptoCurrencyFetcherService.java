package com.sms.challenge.currencywalletapi.service;

import com.sms.challenge.currencywalletapi.config.AppConfig;
import com.sms.challenge.currencywalletapi.exception.ExternalServiceException;
import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import com.sms.challenge.currencywalletapi.persistence.entity.CryptoCurrency;
import com.sms.challenge.currencywalletapi.persistence.entity.CryptoCurrencyPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Crypto currency fetcher service.
 */
@Service
public class CryptoCurrencyFetcherService {

    private static final Logger LOG = LoggerFactory.getLogger(CryptoCurrencyFetcherService.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private WebClient webClient;

    public Map<String, Map<String, Double>> fetch(List<String> currenciesFrom, List<String> currenciesTo) {
        String from = currenciesFrom.stream().collect(Collectors.joining(","));
        String to = currenciesTo.stream().collect(Collectors.joining(","));
        Map<String, Map<String, Double>> data = webClient.get()
                .uri(String.join("", appConfig.getCryptoCompareApiBaseUrl(), "?fsyms=", from, "&tsyms=", to))
                .retrieve()
                .bodyToMono(Map.class)
                .doOnError(error -> LOG.error("An error has occurred {}", error.getMessage()))
                .onErrorResume(error -> Mono.just(new HashMap<>()))
                .block();
        if (data.get("Response") != null) {
            String response = String.valueOf(data.get("Response"));
            if (response.equals("Error")) {
                throw new ExternalServiceException(String.valueOf(data.get("Message")));
            }
        }

        return data;
    }
}
