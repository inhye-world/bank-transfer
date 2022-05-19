package com.example.banktransfer.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import java.util.List;
@Builder
@Getter
public class BankRequestDto {
    @JsonProperty("SECR_KEY")
    private String secr_key;
    @JsonProperty ("KEY")
    private String key;
    @JsonProperty ("CHAR_SET")
    private String char_set;
    @JsonProperty ("REQ_DATA")
    private List<AccountRequestDto> req_data;
}
