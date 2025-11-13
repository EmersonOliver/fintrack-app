package br.com.fintrack.transaction.service;

import br.com.fintrack.transaction.domain.TransactionEntity;
import br.com.fintrack.transaction.resources.request.TransactionRequest;
import br.com.fintrack.transaction.resources.response.TransactionResponse;

public interface TransactionService {

    TransactionResponse create(TransactionRequest request);

}
