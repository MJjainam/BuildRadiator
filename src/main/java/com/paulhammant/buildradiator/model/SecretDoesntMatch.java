package com.paulhammant.buildradiator.model;

public class SecretDoesntMatch extends BuildRadiatorException {
    public SecretDoesntMatch() {
        super("secret doesnt match");
    }
}
