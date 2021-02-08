package com.interview.afterpay.frauddetector.rules;

import com.interview.afterpay.entities.CreditRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CannotExceedCreditWithdrawal implements FraudDetectionRule<CreditRecord> {

    public static final String NAME = "CannotExceedCreditWithdrawal";
    private final Integer thresholdAmount;
    private final Duration windowDuration;

    private static final Logger logger = LogManager.getLogger(
        CannotExceedCreditWithdrawal.class.getCanonicalName());

    public CannotExceedCreditWithdrawal(Integer amount, Duration duration) {
        this.thresholdAmount = amount;
        this.windowDuration = duration;
        logger.debug("thresholdAmount: " + amount + " windowDuration: " + duration.toHours() + " hours");
    }

    /**
     * Checks for the records which have exceeded the threshold amount
     * in a 24 hour sliding window from a file which is chronologically sorted.
     *
     * If the file is chronologically sorted - pop from the bottom
     * all records less than 24 hours from the present window.
     *
     * Time Complexity: O(N) Each record is scanned twice for insertion and removal
     * Space Complexity: O(N) twice for including values and removing them from the sliding window
     *
     * @param creditRecords data of records to check for fraudulent hashedcardIds
     * @return
     */
    @Override
    public List<CreditRecord> validateDateSetAndGetAnomalies(List<CreditRecord> creditRecords) {
        logger.info("Validating " + creditRecords.size()
            + " records against the constraint with threshold amount: "
            + this.thresholdAmount);
        List<CreditRecord> failedRecords = new ArrayList<>();

        Map<String, List<CreditRecord>> limits = new HashMap<>();
        Map<String, Integer> runningSum = new HashMap<>();

        for (CreditRecord record: creditRecords) {
            String id = record.getHashedCardId();
            LocalDateTime currentTime = record.getTransactionTime();
            // minus 24 hours
            LocalDateTime thresholdTime = currentTime.minusHours(this.windowDuration.toHours());
            logger.debug("Record: " + record.getHashedCardId() +
                ": transactionTime: " + currentTime +
                " thresholdTime: " + thresholdTime + " current sum: " + runningSum.getOrDefault(id, 0));

            while (limits.containsKey(id) && !limits.get(id).isEmpty()) {
                CreditRecord curRecord = limits.get(id).get(0);
                LocalDateTime curTime = curRecord.getTransactionTime();
                if (Duration.between(thresholdTime, curTime).isNegative()) {
                    // atomic condition to prevent race conditions
                    synchronized (this) {
                        limits.get(id).remove(0);
                        runningSum.put(id, runningSum.get(id) - curRecord.getAmountInCents());
                    }
                } else {
                    break;
                }
            }

            if (!limits.containsKey(id)) {
                // atomic condition to prevent race conditions
                synchronized (this) {
                    runningSum.put(id, record.getAmountInCents());
                    limits.put(id, new ArrayList<>());
                    limits.get(id).add(record);
                }
            } else {
                synchronized (this) {
                    runningSum.put(id, runningSum.get(id) + record.getAmountInCents());
                    limits.get(id).add(record);
                }
            }


            // Check if the sum in the last 24 hours exceeds the threshold amount
            if (runningSum.get(id) > this.thresholdAmount) {
                logger.debug("FAILING id: " + id + " amount: " + runningSum.get(id) + " threshold: " + this.thresholdAmount);
                failedRecords.add(record);
            }
        }

        logger.info("Found " + failedRecords.size() + " hashedIds failing the constraint: " + getRuleName());
        return failedRecords;
    }

    @Override
    public String getRuleName() {
        return NAME;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nRuleName: " + NAME + "\n");
        builder.append("Parameter#1 Threshold Amount: " + thresholdAmount + "\n");
        builder.append("Parameter#2 WindowDuration: " + windowDuration.toString() + "\n");
        return builder.toString();
    }
}
