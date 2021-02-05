package com.interview.afterpay.cfd.frauddetector.rules;

import com.interview.afterpay.cfd.entities.CreditRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CannotExceedCreditWithdrawal implements FraudDetectionRule<CreditRecord> {

    public static final String NAME = "CannotExceedCreditWithdrawal";
    private Integer thresholdAmount;
    private Duration windowDuration;

    private static final Logger logger = LogManager.getLogger(
        CannotExceedCreditWithdrawal.class.getCanonicalName());

    public CannotExceedCreditWithdrawal(Integer amount, Duration duration) {
        this.thresholdAmount = amount;
        this.windowDuration = duration;
        //logger.info("\nInitializing \n amount: " + amount + " duration: " + windowDuration + " \n");
    }

    @Override
    public List<CreditRecord> validateDateSetAndGetAnomalies(List<CreditRecord> creditRecords) {

        return new ArrayList<>();
    }

    @Override
    public String getRuleName() {
        return NAME;
    }
}
