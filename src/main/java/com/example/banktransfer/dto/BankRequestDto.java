package com.example.banktransfer.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class BankRequestDto {
    private String SECR_KEY;
    private String KEY;
    private String CHAR_SET;
    private List<AccountRequestDto> REQ_DATA;
}
