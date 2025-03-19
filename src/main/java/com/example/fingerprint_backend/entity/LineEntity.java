package com.example.fingerprint_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class LineEntity {

    @Id
    private String studentNumber;
    private String lineId;
    private Boolean receiveLineMessage;

}
