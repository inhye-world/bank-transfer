package com.example.banktransfer.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
@Builder
@Getter
public class AccountRequestDto {
    @JsonProperty ("BANK_CD")
    private String bank_cd;
    @JsonProperty ("SEARCH_ACCT_NO")
    private String search_acct_no;
    @JsonProperty ("ACNM_NO")
    private String acnm_no;
    @JsonProperty ("ICHE_AMT")
    private String iche_amt;
    @JsonProperty ("TRSC_SEQ_NO")
    private String trsc_seq_no;
    @JsonProperty ("NAME")
    private String name;
}
