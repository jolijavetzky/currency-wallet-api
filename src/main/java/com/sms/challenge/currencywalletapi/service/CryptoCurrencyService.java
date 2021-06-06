package com.sms.challenge.currencywalletapi.service;

import com.sms.challenge.currencywalletapi.exception.ExternalServiceException;
import com.sms.challenge.currencywalletapi.persistence.entity.CryptoCurrency;
import com.sms.challenge.currencywalletapi.persistence.entity.CryptoCurrencyPrice;
import com.sms.challenge.currencywalletapi.persistence.entity.Currency;
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

/**
 * The type Crypto currency service.
 */
@Service
public class CryptoCurrencyService {

    private static final Logger LOG = LoggerFactory.getLogger(CryptoCurrencyService.class);

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private WebClient webClient;

    /**
     * Find all list.
     *
     * @return the list
     */
    public List<CryptoCurrency> findAll() {
        List<CryptoCurrency> result = new ArrayList<>();
        List<Currency> currenciesByCrypto = this.currencyService.findAllByCrypto();
        List<Currency> currenciesByNotCrypto = this.currencyService.findAllByNotCrypto();
        String cryptoSymbols = currenciesByCrypto.stream().map(Currency::getSymbol).collect(Collectors.joining(","));
        String notCryptoSymbols = currenciesByNotCrypto.stream().map(Currency::getSymbol).collect(Collectors.joining(","));
        Map<String, Map<String, Double>> data = webClient.get()
                .uri(String.join("", "https://min-api.cryptocompare.com/data/pricemulti", "?fsyms=", cryptoSymbols, "&tsyms=", notCryptoSymbols))
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
        currenciesByCrypto.stream().forEach(item -> {
            if (data.get(item.getSymbol()) != null) {
                CryptoCurrency cryptoCurrency = new CryptoCurrency(item.getSymbol());
                cryptoCurrency.setPrices(new ArrayList<>());
                currenciesByNotCrypto.stream().forEach(item2 -> {
                    cryptoCurrency.getPrices().add(new CryptoCurrencyPrice(item2.getSymbol(), data.get(item.getSymbol()).get(item2.getSymbol())));
                });
                result.add(cryptoCurrency);
            }
        });
        return result;
    }
}
