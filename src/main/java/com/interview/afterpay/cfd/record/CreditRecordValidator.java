package com.interview.afterpay.cfd.record;

import java.time.LocalDateTime;

/**
 * Validates the credit records from the statement
 */
public class CreditRecordValidator {

    public void validate(String hashedCardId, LocalDateTime time, Integer amount) throws IllegalArgumentException {
        validateHashedCardId(hashedCardId);
        validateTimeStamp(time);
        validateAmount(amount);
    }

    private void validateHashedCardId(String hashedCardId) throws IllegalArgumentException {

    }

    private void validateTimeStamp(LocalDateTime time) throws IllegalArgumentException {

    }

    private void validateAmount(Integer amount) throws IllegalArgumentException {

    }
}
