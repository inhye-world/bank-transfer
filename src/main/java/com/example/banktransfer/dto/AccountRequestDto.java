package com.example.banktransfer.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AccountRequestDto {
    private String BANK_CD;
    private String SEARCH_ACCT_NO;
    private String ACNM_NO;
    private String ICHE_AMT;
    private String TRSC_SEQ_NO;
}
