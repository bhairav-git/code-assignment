package com.db.awmd.challenge;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.TransferService;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransferServiceTest {
	@Autowired
	private TransferService transferService;
	@Autowired
	private AccountsService accountsService;

	@Test
	public void amountTransfer_TransactionCommit() throws Exception {
		Account accountFrom = new Account("Id-341");
		accountFrom.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(accountFrom);
		Account accountTo = new Account("Id-342");
		accountTo.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(accountTo);
		this.transferService.amountTransfer("Id-341", "Id-342", new BigDecimal(1000));
		assertThat(this.accountsService.getAccount("Id-341").getBalance()).isEqualTo(BigDecimal.ZERO);
		assertThat(this.accountsService.getAccount("Id-342").getBalance()).isEqualTo(new BigDecimal(2000));

	}

	@Test
	public void amountTransfer_TransactionRollBack() throws Exception {
		Account accountFrom = new Account("Id-350");
		accountFrom.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(accountFrom);
		Account accountTo = new Account("Id-351");
		accountTo.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(accountTo);
		this.transferService.amountTransfer("Id-350", "Id-351", new BigDecimal(1000));

		try {
			// make transfer when balance insufficient
			this.transferService.amountTransfer("Id-350", "Id-351", new BigDecimal(500));
		} catch (Exception e) {
			assertThat(e.getMessage()).isEqualTo("Insufficient balance in account for transfer");
		}
		// Transaction will be rollBack and no account will be updated
		assertThat(this.accountsService.getAccount("Id-350").getBalance()).isEqualTo(BigDecimal.ZERO);
		assertThat(this.accountsService.getAccount("Id-351").getBalance()).isEqualTo(new BigDecimal(2000));

	}

	@Test
	public void amountTransfer_TransactionRollBackOnNonExistingAccount() throws Exception {
		// make transfer To an Account which do not exist
		Account accountFrom = new Account("Id-360");
		accountFrom.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(accountFrom);
		try {
			this.transferService.amountTransfer("Id-360", "Id-361", new BigDecimal(500));
		} catch (Exception e) {
			assertThat(this.accountsService.getAccount("Id-360").getBalance()).isEqualTo(new BigDecimal(1000));
		}

	}

}
