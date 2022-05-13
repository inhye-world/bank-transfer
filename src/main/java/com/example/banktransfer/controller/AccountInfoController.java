package com.example.banktransfer.controller;

import com.example.banktransfer.dto.BankRequestDto;
import com.example.banktransfer.service.AccountInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AccountInfoController {
    @PostMapping("/checkAccountName")
    ResponseEntity <BankRequestDto> getAccountName(@RequestBody BankRequestDto bankRequestDto){
        BankRequestDto dto = AccountInfoService.getAccountInfo(bankRequestDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

//    @PostMapping("/checkAccount")
//    ResponseEntity <AccountRequestDto> getAccount(@RequestBody AccountRequestDto accountRequestDto){
//        String tr_sq_n = "938091";
//        AccountRequestDto dto = accountRequestDto.builder()
//                .BANK_CD(accountRequestDto.getBANK_CD())
//                .SEARCH_ACCT_NO(accountRequestDto.getSEARCH_ACCT_NO())
//                .ACNM_NO(accountRequestDto.getACNM_NO())
//                .ICHE_AMT(accountRequestDto.getICHE_AMT())
//                .TRSC_SEQ_NO(tr_sq_n)
//                .build();
//        return new ResponseEntity<>(dto, HttpStatus.OK);
//    }
}
