package com.nttdata.api.creditaccount.controller;

import com.nttdata.api.creditaccount.document.Card;
import com.nttdata.api.creditaccount.document.CreditAccount;
import com.nttdata.api.creditaccount.service.ICreditAccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/creditaccount")
public class CreditAccountController {
	
	private static final Logger LOGGER = LogManager.getLogger(CreditAccountController.class);

	@Autowired
	private ICreditAccountService creditAccountService;

	@GetMapping
	public Mono<ResponseEntity<Flux<CreditAccount>>> findAll() {
		return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(creditAccountService.findAll()));
	}
	
	
	@GetMapping("client/{codeClient}")
	public Mono<ResponseEntity<Flux<CreditAccount>>> findByCodeClien(@PathVariable String codeClient) {
		return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(creditAccountService.findByCodeClient(codeClient)));
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<CreditAccount>> findById(@PathVariable String id) {
		return creditAccountService.findById(id).map(ca -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(ca))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PostMapping
	public Mono<ResponseEntity<Map<String, Object>>> addCreditCard(@Valid @RequestBody Mono<CreditAccount> monoCreditAccount) {
		Map<String, Object> response = new HashMap<>();

		return monoCreditAccount.flatMap(creditAccount -> {
			creditAccount.setMembershipDate(new Date());
			return creditAccountService.save(creditAccount).map(ca -> {
				response.put("CreditAccount", ca.getObj());
				response.put("message", ca.getMessage());
				response.put("timestamp", new Date());

				return ResponseEntity.created(URI.create("/creditaccount/".concat(creditAccount.getAccountNumber())))
						.contentType(MediaType.APPLICATION_JSON).body(response);
			});
		}).onErrorResume(t -> {
			return Mono.just(t).cast(WebExchangeBindException.class).flatMap(e -> Mono.just(e.getFieldErrors()))
					.flatMapMany(Flux::fromIterable)
					.map(fieldError -> "Field: " + fieldError.getField() + " " + fieldError.getDefaultMessage())
					.collectList().flatMap(list -> {
						response.put("errors", list);
						response.put("timestamp", new Date());
						response.put("status", HttpStatus.BAD_REQUEST.value());

						return Mono.just(ResponseEntity.badRequest().body(response));
					});
		});
	}

	@PutMapping("/{id}")
	public Mono<ResponseEntity<Object>> editCreditCard(@RequestBody CreditAccount creditAccount, @PathVariable String id) {
		return creditAccountService.findById(id).flatMap(ca -> {
			ca.setBalance(creditAccount.getBalance());
			return creditAccountService.save(ca);
		}).map(ca -> ResponseEntity.created(URI.create("/creditaccount/".concat(creditAccount.getAccountNumber())))
				.contentType(MediaType.APPLICATION_JSON).body(ca.getObj())).defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> deleteCreditCard(@PathVariable String id) {
		return creditAccountService.findById(id).flatMap(ca -> {
			return creditAccountService.delete(ca).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));

		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/client/{codeClient}/{typeAccount}")
	public Mono<ResponseEntity<Flux<CreditAccount>>> findByCodeClientAndTypeAccount(
			@PathVariable String codeClient,  @PathVariable Integer typeAccount ) {
		return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
				.body(creditAccountService.findByCodeClientAndTypeAccountId(codeClient,typeAccount)));
	}


	@PutMapping("/client/{codeClient}/{accountNumber}")
	public Mono<ResponseEntity<CreditAccount>> editCard(@RequestBody Card card, @PathVariable String codeClient, @PathVariable String accountNumber) {
		return creditAccountService.findByCodeClientAndAccountNumber(codeClient,accountNumber)
				.flatMap(ca->{
							ca.setCard(card);
							return creditAccountService.saveCard(ca);
						}).map(ba -> ResponseEntity.created(URI.create("/creditaccount".concat("/client/").concat(codeClient).concat("/").concat(accountNumber)))
						.contentType(MediaType.APPLICATION_JSON)
						.body(ba))
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}

}
