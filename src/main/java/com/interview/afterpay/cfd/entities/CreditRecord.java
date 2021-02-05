package com.interview.afterpay.cfd.entities;

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
        StringBuilder builder = new StringBuilder();
        builder.append("\nRecord: HashedCardId: " + getHashedCardId() + "\n");
        builder.append("Transaction Time: " + getTransactionTime() + "\n");
        builder.append("Amount: " + (getAmount() / 100.00) + " dollars \n");

        return builder.toString();
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
}
