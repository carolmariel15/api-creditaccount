package com.nttdata.api.creditaccount.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "credit_account")
public class CreditAccount {
	
	@Id
	private String accountNumber;
	private String codeClient;
	private TypeCredit typeCredit;
	private Currency currency;
	private Date membershipDate;
	private double balance;
	private double creditLimit;
	private CreditCard creditCard;

}
