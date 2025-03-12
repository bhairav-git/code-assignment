package com.db.awmd.challenge.web;

import java.math.BigDecimal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransfer;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.exception.InvalidAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.EmailNotificationService;
import com.db.awmd.challenge.service.TransferService;
import com.db.awmd.challenge.utilities.Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author bbagchi Controller class with /transfer mapping for amount transfer
 *         between account Uses Put Http method.
 */

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class TransferController {

	private final TransferService transferService;
	private final AccountsService accountService;
	private final EmailNotificationService emailNotificationService;

	@Autowired
	public TransferController(TransferService transferService, AccountsService accountsService) {
		this.transferService = transferService;
		this.accountService = accountsService;
		this.emailNotificationService = new EmailNotificationService();
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
		log.info("Creating account {}", account);

		try {
			this.accountsService.createAccount(account);
		} catch (DuplicateAccountIdException daie) {
			return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping(path = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> amountTransfer(@RequestBody @Valid AmountTransfer amountTransfer) {
		try {

			Account fromAccount = accountService.getAccount(amountTransfer.getAccountFrom());
			Account toAccount = accountService.getAccount(amountTransfer.getAccountTo());
			BigDecimal trasferAmount = amountTransfer.getTransferAmount();

			log.info(Constants.TransactionStart_Message, amountTransfer.getAccountFrom(),
					amountTransfer.getAccountTo());

			if (amountTransfer.getAccountFrom().equalsIgnoreCase(amountTransfer.getAccountTo())) {
				log.info(Constants.SimilarAccountException_Message, amountTransfer.getAccountFrom(),
						amountTransfer.getAccountTo());
				return new ResponseEntity<>(Constants.SimilarAccountException_Message, HttpStatus.BAD_REQUEST);

			} else {
				this.transferService.amountTransfer(amountTransfer.getAccountFrom(), amountTransfer.getAccountTo(),
						amountTransfer.getTransferAmount());

				// Notify the fromAccount holder about the transaction
				this.emailNotificationService.notifyAboutTransfer(fromAccount,
						trasferAmount + Constants.NotifyTo_Message + toAccount.getAccountId());
				// Notify the toAccount holder about the transaction
				this.emailNotificationService.notifyAboutTransfer(toAccount,
						trasferAmount + Constants.NotifyFrom_Message + fromAccount.getAccountId());
			}

			log.info(Constants.TransactionComplete_Message, amountTransfer.getAccountFrom(),
					amountTransfer.getAccountTo());

		} catch (InsufficientBalanceException e) {
			log.info(Constants.InsufficientBalanceException_Message, amountTransfer.getAccountFrom(),
					amountTransfer.getAccountTo());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

		catch (InvalidAccountIdException e) {
			log.info(Constants.InvalidAccountIdException_Message, amountTransfer.getAccountFrom(),
					amountTransfer.getAccountTo());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

		catch (Exception e) {
			log.info(Constants.GenericException_Message, amountTransfer.getAccountFrom(),
					amountTransfer.getAccountTo());
			return new ResponseEntity<>(Constants.InvalidAccountIdException_Message, HttpStatus.BAD_REQUEST);

		}
		return new ResponseEntity<>(Constants.TransactionSuccess_Message, HttpStatus.ACCEPTED);
	}

}
