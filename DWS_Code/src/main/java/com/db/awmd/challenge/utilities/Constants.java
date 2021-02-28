package com.db.awmd.challenge.utilities;

/**
 * @author bbagchi Constants class containing constant used in project
 */

public final class Constants {

	private Constants() {

	}

	public static final String InsufficientBalanceException_Message = "The available balance is insufficient for the transfer";
	public static final String InvalidAccountIdException_Message = "The account id provided in transaction is non existent";
	public static final String GenericException_Message = "Exception occured during transfer";
	public static final String SimilarAccountException_Message = "The accountfrom and accountTo are same";
	public static final String TransactionStart_Message = "Money transfer initiated from account:{} to account:{}";
	public static final String TransactionComplete_Message = "Money transfer completed from account:{} to account:{}";
	public static final String TransactionSuccess_Message = "Transfer Completed";
	public static final String NotifyTo_Message = "amount transfered to";
	public static final String NotifyFrom_Message = "amount received from ";

}
