package com.nttdata.api.creditaccount.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Unwrapped.Nullable;

import java.util.Date;

@Getter
@Setter
@Document(collection = "creditaccount")
public class CreditAccount {
	
	@Id
	private String accountNumber;
	private String codeClient;
	private TypeAccount typeAccount;
	private Currency currency;
	@Nullable
	private Date membershipDate;
	private Integer payDays;
	private Integer feeDue;
	private double balance;
	private double creditLimit;
	private Card card;

}
