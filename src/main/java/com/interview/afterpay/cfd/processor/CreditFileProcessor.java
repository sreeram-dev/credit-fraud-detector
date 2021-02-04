package com.interview.afterpay.cfd.processor;

import com.interview.afterpay.cfd.record.CreditRecord;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreditFileProcessor implements Processor<CreditRecord> {

    public List<CreditRecord> processFile(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        CSVParser parser = new CSVParserBuilder()
            .withSeparator(',')
            .build();
        CSVReader reader = new CSVReaderBuilder(fileReader)
            .withCSVParser(parser)
            .withSkipLines(0)
            .build();



        return new ArrayList<>();
    }
}
