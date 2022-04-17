package com.nttdata.api.creditaccount.document;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditCard {
	
	private String cardNumber;
	private Date expiryDate;
	private TypeCard typeCard;
	
}
