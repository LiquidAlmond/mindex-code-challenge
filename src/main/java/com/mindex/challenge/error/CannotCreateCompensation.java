package com.mindex.challenge.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CannotCreateCompensation extends RuntimeException {
    public CannotCreateCompensation(String id) {
        super("Invalid employeeId: " + id);
    }
}
