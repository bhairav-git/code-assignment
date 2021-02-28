package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.exception.InvalidAccountIdException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.transaction.AccountTransactionManager;

import lombok.Getter;

@Service
public class TransferService {
	
	 @Getter
	  private final AccountsRepository accountsRepository;
	  
	  private AccountTransactionManager transactionManager;
	  
	  @Autowired
	  public TransferService(AccountsRepository accountsRepository) {
	    this.accountsRepository = accountsRepository;
	    this.transactionManager = new AccountTransactionManager(accountsRepository);
	  }

	
	  public synchronized void amountTransfer(final String fromAccount,	
			  final String toAccount, final BigDecimal transferAmount) throws InvalidAccountIdException,InsufficientBalanceException{
		  
		  transactionManager.doInTransaction(() ->{
			  
			  this.debit(fromAccount, transferAmount);
			  this.credit(toAccount, transferAmount);
		  });
		  
		  transactionManager.commit();
		  
	  }
	  
		private Account debit(String accountId, BigDecimal amount) throws InvalidAccountIdException, InsufficientBalanceException{
	  		// take repository from transaction manager in order to manage transactions and rollBack.
	  		//But, This method will only be transactional only if this is called within "transactionManager.doInTransaction()
	  		// OR method annotated with @AccountTransaction.
			final Account account = transactionManager.getRepoProxy().getAccount(accountId);
			if(account == null) {
				throw new InvalidAccountIdException("Account id provided in request is non existent");
			}
			if(account.getBalance().compareTo(amount) == -1) {
				throw new InsufficientBalanceException("Insufficient balance in account for transfer");
			}
			BigDecimal bal = account.getBalance().subtract(amount);
			account.setBalance(bal);
			return account;
		}
		
		private Account credit(String accountId, BigDecimal amount) throws InvalidAccountIdException, InsufficientBalanceException{
			// take repository from transaction manager in order to manage transactions and rollBack.
	  		//But, This method will only be transactional only if this is called within "transactionManager.doInTransaction()
	  		// OR method annotated with @AccountTransaction.
			final Account account = transactionManager.getRepoProxy().getAccount(accountId);
			if(account == null) {
				throw new InvalidAccountIdException("Account id provided in request is non existent");
			}
			BigDecimal bal = account.getBalance().add(amount);
			account.setBalance(bal);
			return account;
		}
}
