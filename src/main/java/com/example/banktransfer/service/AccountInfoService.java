package com.example.banktransfer.service;

import com.example.banktransfer.dto.AccountRequestDto;
import com.example.banktransfer.dto.BankRequestDto;
import org.springframework.stereotype.Service;

@Service
public class AccountInfoService {
    public static BankRequestDto getAccountInfo(BankRequestDto bankRequestDto) {
        AccountRequestDto accountRequestDto = AccountRequestDto.builder()
                .BANK_CD("088")
                .SEARCH_ACCT_NO("110486040548")
                .ACNM_NO("990628")
                .ICHE_AMT("100")
                .TRSC_SEQ_NO("7580200")
                .build();
        bankRequestDto.getREQ_DATA().add(accountRequestDto);


        BankRequestDto dto =  BankRequestDto.builder()
                .SECR_KEY(bankRequestDto.getSECR_KEY())
                .KEY(bankRequestDto.getKEY())
                .CHAR_SET(bankRequestDto.getKEY())
                .REQ_DATA(bankRequestDto.getREQ_DATA())
                .build();

        return dto;
    }
}
