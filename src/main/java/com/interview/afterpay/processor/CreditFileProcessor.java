package com.interview.afterpay.processor;

import com.interview.afterpay.entities.CreditRecord;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CreditFileProcessor implements Processor<CreditRecord> {

    private static final Logger logger = LogManager.getLogger(CreditFileProcessor.class.getCanonicalName());

    /**
     * Processes a single csv file that is passed to the records.
     * @param file
     * @return
     * @throws IOException
     * @throws CsvValidationException
     */
    public List<CreditRecord> processFile(File file) throws IOException, CsvValidationException {
        FileReader fileReader = new FileReader(file);
        CSVParser parser = new CSVParserBuilder()
            .withSeparator(',')
            .build();

        CSVReader reader = new CSVReaderBuilder(fileReader)
            .withCSVParser(parser)
            .withSkipLines(0)
            .build();

        List<CreditRecord> records = new ArrayList<>();

        String[] line;

        while ((line = reader.readNext()) != null) {
            records.add(processLineAndGetRecord(line));
        }

        // Sort them in non-decreasing order
        Collections.sort(records, new Comparator<CreditRecord>() {
            @Override
            public int compare(CreditRecord rec1, CreditRecord rec2) {
                Duration diff = Duration.between(rec1.getTransactionTime(), rec2.getTransactionTime());
                if (diff.isNegative()) {
                    return 1;
                }

                if (diff.isZero()) {
                    return 0;
                }

                return -1;
            }
        });

        return records;
    }

    private CreditRecord processLineAndGetRecord(String[] line) {
        String hashedId = line[0].trim();

        //  https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
        // Pattern in the file corresponds to LocalDateTime
        LocalDateTime transactionTime = LocalDateTime.parse(line[1].trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Integer amount = convertDollarToCents(line[2].trim());

        CreditRecord record = new CreditRecord(hashedId, transactionTime, amount);

        logger.info("record" + record.toString());

        return record;
    }

    private Integer convertDollarToCents(String line) {
        Double amount = Double.parseDouble(line);
        return (int) (amount * 100);
    }

}
