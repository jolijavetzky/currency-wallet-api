package com.sms.challenge.currencywalletapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The type Home controller.
 */
@ApiIgnore
@Controller
public class HomeController {

    /**
     * Redirect to swagger string.
     *
     * @return the string
     */
    @GetMapping("/")
    public String redirectToSwagger() {
        return "redirect:/swagger-ui.html";
    }
}
