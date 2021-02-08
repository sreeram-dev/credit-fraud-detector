package com.interview.afterpay.processor;

import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreditFileProcessorTest {

    @Test
    public void testParseFile_throwsCSVValidationException() {
        CreditFileProcessor processor = new CreditFileProcessor();
        File file = new File("src/test/resources/testcase_invalid.csv");
        assertThrows(CsvValidationException.class, () -> processor.processFile(file));
    }

}
