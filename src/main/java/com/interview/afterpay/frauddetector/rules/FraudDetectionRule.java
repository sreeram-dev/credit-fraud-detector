package com.interview.afterpay.frauddetector.rules;

import java.util.List;

public interface FraudDetectionRule<T> {
    List<T> validateDateSetAndGetAnomalies(List<T> dataSet);

    String getRuleName();
}
