package com.example.banktransfer.service;
import com.example.banktransfer.common.Constants;
import com.example.banktransfer.dto.AccountRequestDto;
import com.example.banktransfer.dto.BankRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
@Slf4j
@Service
public class AccountInfoService {
    static String url = Constants.ACCTNM_URL;
    static String vapgUrl = Constants.VAPG_URL;
    static String ACCTNM_SECR_KEY = Constants.ACCTNM_SECR_KEY;
    static String ACCTNM_KEY = Constants.ACCTNM_KEY;
    static String ACCTNM_CHAR_SET = Constants.ACCTNM_CHAR_SET;

    static String VAPG_SECR_KEY = Constants.VAPG_SECR_KEY;

    static String[] VAPG_KEY;
    static String TRT_INST_CD = Constants.TRT_INST_CD;
    static  String BANK_CD = Constants.BANK_CD;
    public static JsonNode setConnection(String domain) {

        try {
            URL url = new URL(domain);
            HttpsURLConnection http = (HttpsURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            System.out.println(http.getResponseCode() + " " + http.getResponseMessage());

            JsonNode result = getReturnNode(http);
            log.info("jsonNode ... {} ", result);
            return result;
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException");
        } catch (IOException e) {
        }
        return null;
    }


    public static JsonNode getReturnNode(HttpsURLConnection connection) throws IOException {
        log.info("contentType... {}", connection.getContentType());

        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), "UTF-8"));
        StringBuffer buffer = new StringBuffer();
        String decodedString;
        while ((decodedString = in.readLine()) != null) {
            buffer.append(decodedString);
        }
        in.close();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(buffer.toString());
        return jsonNode;
    }


    public static String setTimeSeqNo() {
        SimpleDateFormat sd = new SimpleDateFormat("HHmmss");
        String sdFormat = sd.format(System.currentTimeMillis());
        sdFormat += "0";

        log.info("sdFormat..... {}", sdFormat);

        return sdFormat;
    }


    public static Boolean getAccountName(AccountRequestDto accountRequestDto){
        AccountRequestDto dto = AccountRequestDto.builder()
                .bank_cd(getBankCode(accountRequestDto.getBank_cd()))
                .search_acct_no(accountRequestDto.getSearch_acct_no())
                .acnm_no(accountRequestDto.getAcnm_no())
                .iche_amt(accountRequestDto.getIche_amt())
                .trsc_seq_no(setTimeSeqNo())
                .name(accountRequestDto.getName())
                .build();
        ArrayList<AccountRequestDto> tmp = new ArrayList<>();
        tmp.add(dto);
        BankRequestDto bankRequestDto = BankRequestDto.builder()
                .key(ACCTNM_KEY)
                .secr_key(ACCTNM_SECR_KEY)
                .char_set(ACCTNM_CHAR_SET)
                .req_data(tmp)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode finalDto = mapper.convertValue(bankRequestDto, JsonNode.class);

        String req = finalDto.toString();
        String domain = url + req;
        JsonNode result = setConnection(domain);

        String code = result.get("RSLT_CD").toString().substring(1, result.get("RSLT_CD").toString().length() - 1);
        String completed = "000";
        String accountName = null;
        if (completed.equals(code)) {
            JsonNode jsonNode = result.get("RESP_DATA").get(0);
            accountName = jsonNode.get("ACCT_NM").toString().substring(1, jsonNode.get("ACCT_NM").toString().length() - 1);
        }
        if (dto.getName().equals(accountName)) {
            log.info("계좌 조회에 성공했습니다.");
            return true;
        }
        log.info("계좌 조회에 실패했습니다. 계좌 정보를 다시 확인해주세요.");
        return false;
    }


    public static String getBankCode(String bank_cd) {
        HashMap<String, String> bankList = new HashMap<String, String>() {
            {
                put("산업은행", "002");
                put("기업은행", "003");
                put("국민은행", "004");
                put("외환은행", "005");
                put("수협은행", "007");
                put("농협은행", "011");
                put("우리은행", "020");
                put("제일은행", "023");
                put("씨티은행", "027");
                put("대구은행", "031");
                put("부산은행", "032");
                put("광주은행", "034");
                put("제주은행", "035");
                put("전북은행", "037");
                put("경남은행", "039");
                put("새마을금고", "045");
                put("신협은행", "048");
                put("우체국은행", "071");
                put("KEB 하나은행", "081");
                put("신한은행", "088");
                put("삼성증권", "240");
            }
        };
        return bankList.get(bank_cd);
    }

    public static Integer getBalance() {
        String TRSC_SEQ_NO;

        String req = "{\"SECR_KEY\":\""+VAPG_SECR_KEY+"\",\"KEY\":\""+VAPG_KEY[2]+"\",\"TRT_INST_CD\":\""+TRT_INST_CD+"\",\"BANK_CD\":\""+BANK_CD+"\",\"TRSC_SEQ_NO\":\"114145669120\"}";
        vapgUrl += req;
        log.info("vapgUrl... getBalance {}", vapgUrl);

        String domain = url + req;
        String WDRW_CAN_AMT = null;

        JsonNode result = setConnection(domain);
        String code = result.get("RESP_CD").toString().substring(1, result.get("RESP_CD").toString().length() - 1);
        String completed = "0000";
        if (completed.equals(code)) {
            WDRW_CAN_AMT = result.get("WDRW_CAN_AMT").toString().substring(1, result.get("WDRW_CAN_AMT").toString().length() - 1);
            return Integer.parseInt(WDRW_CAN_AMT);
        }
        return 0;
    }

    public static boolean sendMoney() {
        String WDRW_ACCT_NO = Constants.WDRW_ACCT_NO;
        String WDRW_ACCT_NM = Constants.WDRW_ACCT_NM;

        vapgUrl += "{\"SECR_KEY\":\""+VAPG_SECR_KEY+"\",\"KEY\":\""+VAPG_KEY[0]+"\",\"TRT_INST_CD\":\""+TRT_INST_CD+"\",\"BANK_CD\":\""+BANK_CD+"\",\"TRSC_SEQ_NO\":\"693653753245\",\"RCV_BNK_CD\":\"004\",\"RCV_ACCT_NO\":\"87050100045847\",\"WDRW_ACCT_NO\":\""+WDRW_ACCT_NO+"\",\"TRSC_AMT\":\"10\",\"WDRW_ACCT_NM\":\""+WDRW_ACCT_NM+"\"}";

        String domain = vapgUrl;
        JsonNode result = setConnection(domain);
        log.info("tranferMoney {}", domain);

        String code = result.get("RESP_CD").toString().substring(1, result.get("RESP_CD").toString().length() - 1);
        String msg = result.get("RESP_MSG").toString().substring(1, result.get("RESP_MSG").toString().length() - 1);
        log.info("RESP_CD : {}, RESP_MSG : {}", code, msg);
        String completed = "0000";
        if (completed.equals(code)) {
            String rcvAccount = result.get("RCV_ACCT_NO").toString().substring(1, result.get("RCV_ACCT_NO").toString().length() - 1);
            String acctName = result.get("RCV_ACCT_NM").toString().substring(1, result.get("RCV_ACCT_NM").toString().length() - 1);
            String trscAmt = result.get("TRST_AMT").toString().substring(1, result.get("TRST_AMT").toString().length() - 1);
            String balAmt = result.get("BAL_AMT").toString().substring(1, result.get("BAL_AMT").toString().length() - 1);
            log.info("{}님의 {} 계좌로 {}원 입급 완료되었습니다. 잔액 {}원", acctName, rcvAccount, trscAmt, balAmt);
            return true;
        }
        return false;
    }
}