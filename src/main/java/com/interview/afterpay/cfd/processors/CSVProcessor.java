package com.interview.afterpay.cfd.processors;

import com.interview.afterpay.cfd.record.CreditRecord;

import javax.swing.undo.CannotRedoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVProcessor implements Processor<CreditRecord> {

    public List<CreditRecord> processFile(String filepath) throws IOException {
        validateFilePath(filepath);
        return new ArrayList<>();
    }

    /**
     * Check if the file exists and
     * @param filepath
     * @throws IOException
     */
    public void validateFilePath(String filepath) throws IOException {

    }


}
