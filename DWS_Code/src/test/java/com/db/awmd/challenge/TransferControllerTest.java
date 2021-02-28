package com.db.awmd.challenge;


import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransferControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private AccountsService accountsService;
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void prepareMockMvc() {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

		// Reset the existing accounts before each test.
		accountsService.getAccountsRepository().clearAccounts();
	}

	@Test
	public void amountTransfer() throws Exception {
		String accountIdFrom = "Id-360";
		Account accountFrom = new Account(accountIdFrom, new BigDecimal("123.45"));
		this.accountsService.createAccount(accountFrom);
		String accountIdTo = "Id-361";
		Account accountTo = new Account(accountIdTo, new BigDecimal("123.45"));
		this.accountsService.createAccount(accountTo);

		this.mockMvc
				.perform(put("/v1/accounts/transfer/").contentType(MediaType.APPLICATION_JSON)
						.content("{\"accountFrom\":\"Id-360\",\"accountTo\":\"Id-361\",\"transferAmount\":100}"))
				.andExpect(status().isAccepted());
	}
	
	@Test
	public void insufficientBalanceTransfer() throws Exception {
		String accountIdFrom = "Bh-001";
		Account accountFrom = new Account(accountIdFrom, new BigDecimal("123.45"));
		this.accountsService.createAccount(accountFrom);
		String accountIdTo = "Bh-002";
		Account accountTo = new Account(accountIdTo, new BigDecimal("125.45"));
		this.accountsService.createAccount(accountTo);

		this.mockMvc
				.perform(put("/v1/accounts/transfer/").contentType(MediaType.APPLICATION_JSON)
						.content("{\"accountFrom\":\"Bh-001\",\"accountTo\":\"Bh-002\",\"transferAmount\":500}"))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void invalidAccountTransfer() throws Exception {
		String accountIdFrom = "Bh-001";
		Account accountFrom = new Account(accountIdFrom, new BigDecimal("123.45"));
		this.accountsService.createAccount(accountFrom);
		String accountIdTo = "Bh-002";
		Account accountTo = new Account(accountIdTo, new BigDecimal("125.45"));
		this.accountsService.createAccount(accountTo);

		this.mockMvc
				.perform(put("/v1/accounts/transfer/").contentType(MediaType.APPLICATION_JSON)
						.content("{\"accountFrom\":\"Bh-001\",\"accountTo\":\"Bh-007\",\"transferAmount\":500}"))
				.andExpect(status().isBadRequest());
	}
	
	
}
