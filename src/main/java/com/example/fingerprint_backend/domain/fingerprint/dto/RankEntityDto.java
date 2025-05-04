package com.example.fingerprint_backend.domain.fingerprint.dto;

import com.example.fingerprint_backend.domain.fingerprint.util.FormatPolicy;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class RankEntityDto {

    private String studentNumber;
    private String givenName;
    private String familyName;
    private String profileImageUrl;
    private int rank;
    private String formattedValue;
    private int attendCount;

    /**
     * 체류 시간
     */
    public RankEntityDto(String studentNumber, String givenName, String familyName, String profileImageUrl, int rank,
            Long ms, int attendCount) {
        this.studentNumber = studentNumber;
        this.givenName = givenName;
        this.familyName = familyName;
        this.profileImageUrl = profileImageUrl;
        this.rank = rank;

        this.formattedValue = FormatPolicy.formatTime(ms);
        this.attendCount = attendCount;
    }

    /**
     * 등교 시간
     */
    public RankEntityDto(String studentNumber, String givenName, String familyName, String profileImageUrl, int rank,
            LocalTime time, int attendCount) {
        this.studentNumber = studentNumber;
        this.givenName = givenName;
        this.familyName = familyName;
        this.profileImageUrl = profileImageUrl;
        this.rank = rank;

        this.formattedValue = FormatPolicy.formatTime(time);
        this.attendCount = attendCount;
    }
}
