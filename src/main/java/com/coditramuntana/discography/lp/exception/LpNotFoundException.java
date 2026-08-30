package com.coditramuntana.discography.lp.exception;

import com.coditramuntana.discography.shared.error.ResourceNotFoundException;

public class LpNotFoundException extends ResourceNotFoundException {
    public LpNotFoundException(Long id) {
        super("Lp",id);
    }
}
