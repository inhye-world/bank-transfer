package com.example.banktransfer.controller;
import com.example.banktransfer.dto.AccountRequestDto;
import com.example.banktransfer.service.AccountInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1")
public class AccountInfoController {
    @PostMapping("/checkAccountName")
    Boolean getAccountName(@RequestBody AccountRequestDto accountRequestDto) throws JsonProcessingException {
        Boolean isEverythingOk = false;

        Boolean availableAccountUserName = AccountInfoService.getAccountName(accountRequestDto);
        if (availableAccountUserName.booleanValue() == true){
            Integer availableAccountBalance = AccountInfoService.getBalance();
            if (availableAccountBalance > 100000){
                isEverythingOk = true;
            }
        }
        return isEverythingOk;
    }
}
