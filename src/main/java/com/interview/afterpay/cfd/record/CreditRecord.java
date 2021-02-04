package com.interview.afterpay.cfd.record;

import java.time.LocalDateTime;

public class CreditRecord {

    public String hashedCardId;

    public LocalDateTime transactedAt;

    public Integer amount;

    public CreditRecord(String hashedCardId, LocalDateTime transactionTime, Integer amount) {

    }
}
