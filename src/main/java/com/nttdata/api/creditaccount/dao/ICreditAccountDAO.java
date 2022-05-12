package com.nttdata.api.creditaccount.dao;

import com.nttdata.api.creditaccount.document.CreditAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ICreditAccountDAO extends ReactiveMongoRepository<CreditAccount, String> {
	
	public Flux<CreditAccount> findByCodeClient(String codeClient);
	public Flux<CreditAccount> findByCodeClientAndTypeAccountId(String codeClient, Integer typeAccountId);
	public Mono<CreditAccount> findByCodeClientAndAccountNumber(String codeClient, String accountNumber);

}
