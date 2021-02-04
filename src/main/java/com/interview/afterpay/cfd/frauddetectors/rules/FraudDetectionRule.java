package com.interview.afterpay.cfd.frauddetectors.rules;

import com.interview.afterpay.cfd.record.CreditRecord;

import java.util.List;

public interface FraudDetectionRule<T> {
    List<T> validateDateSetAndGetAnomalies(List<T> dataSet);
}
