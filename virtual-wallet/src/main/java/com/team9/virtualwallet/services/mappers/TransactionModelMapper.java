package com.team9.virtualwallet.services.mappers;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.ExternalTransactionDto;
import com.team9.virtualwallet.models.dtos.MoveToWalletTransactionDto;
import com.team9.virtualwallet.models.dtos.TransactionDto;
import com.team9.virtualwallet.models.enums.TransactionType;
import com.team9.virtualwallet.repositories.contracts.PaymentMethodRepository;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class TransactionModelMapper {

    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;


    @Autowired
    public TransactionModelMapper(UserRepository userRepository, PaymentMethodRepository paymentMethodRepository) {
        this.userRepository = userRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public Transaction fromDto(User user, TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        LocalDateTime localDateTime = LocalDateTime.now();
        User recipient = userRepository.getById(transactionDto.getRecipientId());

        transaction.setSender(user);
        transaction.setRecipient(recipient);
        transaction.setSenderPaymentMethod(paymentMethodRepository.getById(transactionDto.getSelectedWalletId(), "Wallet"));
        transaction.setRecipientPaymentMethod(paymentMethodRepository.getById(recipient.getDefaultWallet().getId(), "Wallet"));
        transaction.setAmount(transactionDto.getAmount());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setTimestamp(Timestamp.valueOf(localDateTime));

        return transaction;
    }

    public Transaction fromDtoMoveToWallet(User user, MoveToWalletTransactionDto moveToWalletTransactionDto) {
        Transaction transaction = new Transaction();
        LocalDateTime localDateTime = LocalDateTime.now();

        transaction.setSender(user);
        transaction.setRecipient(user);
        transaction.setSenderPaymentMethod(paymentMethodRepository.getById(moveToWalletTransactionDto.getSelectedWalletId(), "Wallet"));
        transaction.setRecipientPaymentMethod(paymentMethodRepository.getById(moveToWalletTransactionDto.getWalletToMoveToId(), "Wallet"));
        transaction.setAmount(moveToWalletTransactionDto.getAmount());
        transaction.setDescription(moveToWalletTransactionDto.getDescription());
        transaction.setTimestamp(Timestamp.valueOf(localDateTime));

        return transaction;
    }

    public Transaction fromExternalDepositDto(User user, ExternalTransactionDto externalTransactionDto) {
        Transaction transaction = new Transaction();
        LocalDateTime localDateTime = LocalDateTime.now();

        transaction.setRecipient(user);
        transaction.setSender(user);
        transaction.setSenderPaymentMethod(paymentMethodRepository.getById(externalTransactionDto.getSelectedCardId(), "Card"));
        transaction.setRecipientPaymentMethod(paymentMethodRepository.getById(externalTransactionDto.getSelectedWalletId(), "Wallet"));
        transaction.setAmount(externalTransactionDto.getAmount());
        transaction.setDescription(externalTransactionDto.getDescription());
        transaction.setTimestamp(Timestamp.valueOf(localDateTime));
        transaction.setTransactionType(TransactionType.CARD_TO_WALLET);

        return transaction;
    }

    public Transaction fromExternalWithdrawDto(User user, ExternalTransactionDto externalTransactionDto) {
        Transaction transaction = new Transaction();
        LocalDateTime localDateTime = LocalDateTime.now();

        transaction.setRecipient(user);
        transaction.setSender(user);
        transaction.setSenderPaymentMethod(paymentMethodRepository.getById(externalTransactionDto.getSelectedWalletId(), "Wallet"));
        transaction.setRecipientPaymentMethod(paymentMethodRepository.getById(externalTransactionDto.getSelectedCardId(), "Card"));
        transaction.setAmount(externalTransactionDto.getAmount());
        transaction.setDescription(externalTransactionDto.getDescription());
        transaction.setTimestamp(Timestamp.valueOf(localDateTime));
        transaction.setTransactionType(TransactionType.WALLET_TO_CARD);

        return transaction;
    }

}
