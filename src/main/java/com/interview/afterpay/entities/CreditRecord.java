package com.interview.afterpay.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CreditRecord implements Serializable {

    private String hashedCardId;

    private LocalDateTime transactionTime;

    private Integer amount;

    // Empty constructor for beans
    public CreditRecord() {
    }

    public CreditRecord(String hashedCardId, LocalDateTime transactionTime, Integer amount) {
        this.hashedCardId = hashedCardId;
        this.transactionTime = transactionTime;
        this.amount = amount;
    }

    @Override
    public String toString() {
        String builder = "\nRecord: HashedCardId: " + getHashedCardId() + "\n" +
            "Transaction Time: " + getTransactionTime() + "\n" +
            "Amount: " + getAmountInDollars() + " dollars \n";
        return builder;
    }

    public String getHashedCardId() {
        return hashedCardId;
    }

    public void setHashedCardId(String hashedCardId) {
        this.hashedCardId = hashedCardId;
    }

    public void setTransactionTime(LocalDateTime time) {
        this.transactionTime = time;
    }

    public LocalDateTime getTransactionTime() {
        return this.transactionTime;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return this.amount;
    }

    public Integer getAmountInCents() {
        return this.getAmount();
    }

    public Double getAmountInDollars() {
        return this.getAmount() / 100.00;
    }
}
