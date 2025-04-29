package com.example.fingerprint_backend.domain.fingerprint.dto;

import com.example.fingerprint_backend.domain.fingerprint.util.FormatPolicy;
import lombok.Getter;

@Getter
public class RankEntityDto {

    private String studentNumber;
    private String givenName;
    private String familyName;
    private String profileImageUrl;
    private int rank;
    private String formattedValue;

    public RankEntityDto(String studentNumber, String givenName, String familyName, String profileImageUrl, int rank, Long ms) {
        this.studentNumber = studentNumber;
        this.givenName = givenName;
        this.familyName = familyName;
        this.profileImageUrl = profileImageUrl;
        this.rank = rank;

        this.formattedValue = FormatPolicy.formatTime(ms);
    }
}
