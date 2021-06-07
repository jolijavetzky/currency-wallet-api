package com.sms.challenge.currencywalletapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CurrencyWalletApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyWalletApiApplication.class, args);
	}

}
