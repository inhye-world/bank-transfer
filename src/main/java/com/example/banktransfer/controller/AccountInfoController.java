package com.example.banktransfer.controller;
import com.example.banktransfer.dto.AccountRequestDto;
import com.example.banktransfer.service.AccountInfoService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1")
public class AccountInfoController {
    @PostMapping("/checkAccountName")
    Boolean getAccountName(@RequestBody AccountRequestDto accountRequestDto){
        return AccountInfoService.getAccountName(accountRequestDto);
    }
}
