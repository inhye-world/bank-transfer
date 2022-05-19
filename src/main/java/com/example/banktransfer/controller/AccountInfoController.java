package com.example.banktransfer.controller;
import com.example.banktransfer.dto.AccountRequestDto;
import com.example.banktransfer.service.AccountInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1")
public class AccountInfoController {
    @PostMapping("/checkAccountName")
    Boolean getAccountName(@RequestBody AccountRequestDto accountRequestDto){
        return AccountInfoService.getAccountName(accountRequestDto);
    }

    @GetMapping("/checkBalance")
    Integer getAccountName(){
        return AccountInfoService.getBalance();
    }

    @GetMapping("/bankTransfer")
    Boolean transferAndCheckAccount(){
        return AccountInfoService.sendMoney();
    }
}
