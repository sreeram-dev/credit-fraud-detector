package com.interview.afterpay.cfd.frauddetector.rules;

import com.interview.afterpay.cfd.record.CreditRecord;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CannotExceedCreditWithdrawal implements FraudDetectionRule<CreditRecord> {
    private Integer thresholdAmount;
    private Duration windowDuration;

    public CannotExceedCreditWithdrawal(Integer amount, Duration duration) {
        this.thresholdAmount = amount;
        this.windowDuration = duration;
    }

    @Override
    public List<CreditRecord> validateDateSetAndGetAnomalies(List<CreditRecord> creditRecords) {

        return new ArrayList<>();
    }

}
