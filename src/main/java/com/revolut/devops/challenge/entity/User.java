package com.revolut.devops.challenge.entity;

import java.util.Date;

public class User {
    private Date dateOfBirth = new Date();

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
