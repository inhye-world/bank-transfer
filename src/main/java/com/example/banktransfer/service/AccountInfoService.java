package com.example.banktransfer.service;
import com.example.banktransfer.dto.AccountRequestDto;
import com.example.banktransfer.dto.BankRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import com.fasterxml.jackson.databind.JsonNode;

@Slf4j
@Service
public class AccountInfoService {
    public static Boolean getAccountName(AccountRequestDto accountRequestDto) {

        String url = "https://dev2.coocon.co.kr:8443/sol/gateway/acctnm_rcms_wapi.jsp?JSONData=";

        AccountRequestDto dto = AccountRequestDto.builder()
                .BANK_CD(getBankCode(accountRequestDto.getBANK_CD()))
                .SEARCH_ACCT_NO(accountRequestDto.getSEARCH_ACCT_NO())
                .ACNM_NO(accountRequestDto.getACNM_NO())
                .ICHE_AMT(accountRequestDto.getICHE_AMT())
                .TRSC_SEQ_NO(makeTimeSeqNo())
                .NAME(accountRequestDto.getNAME())
                .build();

        ArrayList <AccountRequestDto> tmp = new ArrayList<>();
        tmp.add(dto);

        BankRequestDto bankRequestDto = BankRequestDto.builder()
                .KEY("ACCTTEST")
                .SECR_KEY("ACCTNM_RCMS_WAPI")
                .CHAR_SET("UTF-8")
                .REQ_DATA(tmp)
                .build();


        String req = bankRequestDto.toString();

        String domain = url + req;
        String accountName = getAccountInfo(domain);
        if (dto.getNAME().equals(accountName)){
            log.info("계좌 조회에 성공했습니다.");
            return true;
        }
        log.info("계좌 조회에 실패했습니다. 계좌 정보를 다시 확인해주세요.");
        return false;
    }

    public static String getAccountInfo(String domain){
        try {
            URL url = new URL(domain);
            HttpsURLConnection http = (HttpsURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            System.out.println(http.getResponseCode()+" "+http.getResponseMessage());

            JsonNode jsonNode = getReturnNode(http);
            System.out.println(jsonNode);

            String code = jsonNode.get("RSLT_CD").toString().substring(1,jsonNode.get("RSLT_CD").toString().length()-1);
            String completed = "000";

            if (completed.equals(code)){
                JsonNode jsonNode1 = jsonNode.get("RESP_DATA").get(0);
                return jsonNode1.get("ACCT_NM").toString().substring(1,jsonNode1.get("ACCT_NM").toString().length()-1);
            }
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException");
        } catch (IOException e) {
        }
        return null;
    }

    public static JsonNode getReturnNode( HttpsURLConnection connection ) throws IOException {
        BufferedReader in = new BufferedReader( new InputStreamReader(
                connection.getInputStream(), "EUC-KR" ) );
        StringBuffer buffer = new StringBuffer();
        String decodedString;
        while( ( decodedString = in.readLine() ) != null ) {
            buffer.append( decodedString );
        }
        in.close();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(buffer.toString());
        return jsonNode;
    }

    public static String makeTimeSeqNo(){
        SimpleDateFormat sd = new SimpleDateFormat("HHmmss");
        String sdFormat = sd.format(System.currentTimeMillis());
        sdFormat += "0";

        log.info("sdFormat..... {}", sdFormat);

        return sdFormat;
    }

    public static String getBankCode(String bank_cd){
        HashMap<String,String> bankList = new HashMap<String, String>() {
            {
                put("산업", "002");put("기업", "003");put("국민", "004");put("외환", "005");put("수협", "007");
                put("농협", "011");put("우리", "020");put("제일", "023");put("씨티", "027");put("대구", "031");
                put("부산", "032");put("광주", "034");put("제주", "035");put("전북", "037");put("경남", "039");
                put("금고", "045");put("신협", "048");put("우체국", "071");put("KEB 하나", "081");put("신한", "088");
                put("삼성증권","240");
            }
        };
        return bankList.get(bank_cd);
    }
}
