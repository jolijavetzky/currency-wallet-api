package com.sms.challenge.currencywalletapi.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.challenge.currencywalletapi.controller.WalletController;
import com.sms.challenge.currencywalletapi.domain.CreateWalletDTO;
import com.sms.challenge.currencywalletapi.domain.CurrencyAmountDTO;
import com.sms.challenge.currencywalletapi.domain.UpdateWalletDTO;
import com.sms.challenge.currencywalletapi.entity.CurrencyAmount;
import com.sms.challenge.currencywalletapi.entity.Wallet;
import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import com.sms.challenge.currencywalletapi.exception.ValidationException;
import com.sms.challenge.currencywalletapi.service.WalletService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The type Wallet controller test.
 */
@WebMvcTest(WalletController.class)
public class WalletControllerTest {

    private static final String CURRENCY_SYMBOL_FROM = "BTC";
    private static final String CURRENCY_SYMBOL_TO = "USD";
    private static final Double CURRENCY_INITIAL_AMOUNT = 2.5;

    /**
     * The Service.
     */
    @MockBean
    WalletService service;

    /**
     * The Mock mvc.
     */
    @Autowired
    MockMvc mockMvc;

    /**
     * Test find.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFind() throws Exception {
        CurrencyAmount currencyAmount1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        CurrencyAmount currencyAmount2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT);
        final long id = 1L;
        Wallet wallet = new Wallet(id, "MyWallet", Stream.of(currencyAmount1, currencyAmount2).collect(Collectors.toSet()));
        when(this.service.find(Mockito.anyLong())).thenReturn(wallet);

        mockMvc.perform(get("/wallets/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    /**
     * Test find not found.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFindNotFound() throws Exception {
        when(this.service.find(Mockito.anyLong())).thenThrow(NotFoundException.class);
        mockMvc.perform(get("/wallets/".concat("1"))).andExpect(status().isNotFound());
    }

    /**
     * Test find validate.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFindValidate() throws Exception {
        final long id = 1L;
        when(this.service.find(Mockito.anyLong())).thenThrow(ValidationException.class);
        mockMvc.perform(get("/wallets/{id}", id)).andExpect(status().isBadRequest());
    }

    /**
     * Test create.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCreate() throws Exception {
        CurrencyAmount currencyAmount1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        CurrencyAmount currencyAmount2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT);
        final long id = 1L;
        Wallet wallet = new Wallet(id, "MyWallet", Stream.of(currencyAmount1, currencyAmount2).collect(Collectors.toSet()));
        when(this.service.create(Mockito.any())).thenReturn(wallet);

        CreateWalletDTO dto = new CreateWalletDTO();
        dto.setName(wallet.getName());
        dto.setCurrencyAmounts(wallet.getCurrencyAmounts().stream().map(item -> new CurrencyAmountDTO(item.getCurrency(), item.getAmount())).collect(Collectors.toList()));

        mockMvc.perform(post("/wallets")
                .content(asJsonString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    /**
     * Test create validate.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCreateValidate() throws Exception {
        when(this.service.create(Mockito.any())).thenThrow(ValidationException.class);
        mockMvc.perform(post("/wallets")
                .content(asJsonString(new CreateWalletDTO()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test update.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUpdate() throws Exception {
        CurrencyAmount currencyAmount1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        CurrencyAmount currencyAmount2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT);
        final long id = 1L;
        Wallet wallet = new Wallet(id, "MyWallet", Stream.of(currencyAmount1, currencyAmount2).collect(Collectors.toSet()));
        when(this.service.find(Mockito.any())).thenReturn(wallet);
        when(this.service.update(Mockito.any())).thenReturn(wallet);
        UpdateWalletDTO dto = new UpdateWalletDTO();
        final String newName = "New name";
        dto.setName(newName);

        mockMvc.perform(put("/wallets/{id}", id)
                .content(asJsonString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName));
    }

    /**
     * Test update not found.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUpdateNotFound() throws Exception {
        CurrencyAmount currencyAmount1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        CurrencyAmount currencyAmount2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT);
        final long id = 1L;
        Wallet wallet = new Wallet(id, "MyWallet", Stream.of(currencyAmount1, currencyAmount2).collect(Collectors.toSet()));
        when(this.service.update(Mockito.any())).thenReturn(wallet);
        when(this.service.find(Mockito.any())).thenThrow(NotFoundException.class);
        UpdateWalletDTO dto = new UpdateWalletDTO();
        final String newName = "New name";
        dto.setName(newName);
        mockMvc.perform(put("/wallets/{id}", 1)
                .content(asJsonString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Test update validate.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUpdateValidate() throws Exception {
        CurrencyAmount currencyAmount1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        CurrencyAmount currencyAmount2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT);
        final long id = 1L;
        Wallet wallet = new Wallet(id, "MyWallet", Stream.of(currencyAmount1, currencyAmount2).collect(Collectors.toSet()));
        when(this.service.find(Mockito.any())).thenReturn(wallet);
        when(this.service.update(Mockito.any())).thenThrow(ValidationException.class);
        UpdateWalletDTO dto = new UpdateWalletDTO();
        final String newName = "New name";
        dto.setName(newName);
        mockMvc.perform(put("/wallets/{id}", 1)
                .content(asJsonString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test delete.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/wallets/{id}", 1))
                .andExpect(status().isNoContent());
    }

    /**
     * Test delete validate.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDeleteValidate() throws Exception {
        doThrow(ValidationException.class).when(this.service).delete(Mockito.anyLong());
        mockMvc.perform(delete("/wallets/{id}", 1))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test delete not found.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDeleteNotFound() throws Exception {
        doThrow(NotFoundException.class).when(this.service).delete(Mockito.anyLong());
        mockMvc.perform(delete("/wallets/{id}", 1))
                .andExpect(status().isNotFound());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
