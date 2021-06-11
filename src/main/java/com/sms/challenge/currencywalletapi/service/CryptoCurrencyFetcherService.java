package com.sms.challenge.currencywalletapi.service;

import com.sms.challenge.currencywalletapi.config.AppConfig;
import com.sms.challenge.currencywalletapi.exception.ExternalServiceException;
import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * Fetch map width Cache-Aside for throughput.
     *
     * @param currenciesFrom the currencies from
     * @param currenciesTo   the currencies to
     * @return the map
     */
    public Map<String, Map<String, Number>> fetch(List<String> currenciesFrom, List<String> currenciesTo) {
        String from = currenciesFrom.stream().collect(Collectors.joining(","));
        String to = currenciesTo.stream().collect(Collectors.joining(","));

        Map<String, Map<String, Number>> data = webClient.get()
                .uri(String.join(
                        "",
                        appConfig.getCryptoCompareApiBaseUrl(),
                        "/pricemulti?fsyms=",
                        from,
                        "&tsyms=",
                        to
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .doOnError(error -> LOG.error("An error has occurred {}", error.getMessage()))
                .onErrorResume(error -> Mono.just(new HashMap<>()))
                .block();
        if (data.get("Response") != null) {
            String response = String.valueOf(data.get("Response"));
            if (response.equals("Error")) {
                String message = String.valueOf(data.get("Message"));
                if (message.contains("market does not exist for this coin pair")) {
                    throw new NotFoundException("Currency symbol not found");
                }
                throw new ExternalServiceException(message);
            }
        }

        return data;
    }

    /**
     * Fetch map.
     *
     * @param currencyFrom the currency from
     * @param currencyTo   the currency to
     * @return the map
     */
    public Map<String, Number> fetch(String currencyFrom, String currencyTo) {
        Map<String, Number> data = webClient.get()
                .uri(String.join(
                        "",
                        appConfig.getCryptoCompareApiBaseUrl(),
                        "/price?fsym=",
                        currencyFrom,
                        "&tsyms=",
                        currencyTo
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .doOnError(error -> LOG.error("An error has occurred {}", error.getMessage()))
                .onErrorResume(error -> Mono.just(new HashMap<>()))
                .block();
        if (data.get("Response") != null) {
            String response = String.valueOf(data.get("Response"));
            if (response.equals("Error")) {
                String message = String.valueOf(data.get("Message"));
                if (message.contains("market does not exist for this coin pair")) {
                    throw new NotFoundException("Currency symbol not found");
                }
                throw new ExternalServiceException(message);
            }
        }

        return data;
    }

}
