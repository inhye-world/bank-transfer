package com.example.banktransfer.service;
import com.example.banktransfer.common.Constants;
import com.example.banktransfer.dto.AccountRequestDto;
import com.example.banktransfer.dto.BankRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    //static String url = "https://dev2.coocon.co.kr:8443/sol/gateway/acctnm_rcms_wapi.jsp?JSONData=";
    static String url = Constants.ACCTNM_URL;
    static String vapgUrl = Constants.VAPG_URL;

    static String SECR_KEY = Constants.VAPG_SECR_KEY;
    static String TRT_INST_CD = Constants.TRT_INST_CD;
    static  String BANK_CD = Constants.BANK_CD;

    public static Boolean getAccountName(AccountRequestDto accountRequestDto) throws JsonProcessingException {


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
                .KEY("ACCTNM_RCMS_WAPI")
                .SECR_KEY("ACCTTEST")
                .CHAR_SET("UTF-8")
                .REQ_DATA(tmp)
                .build();


        ObjectMapper mapper = new ObjectMapper();
        JsonNode finalDto = mapper.convertValue(bankRequestDto,JsonNode.class);
        ObjectNode object = (ObjectNode) finalDto;

        object.set("SECR_KEY", finalDto.get("secr_KEY"));   object.remove("secr_KEY");
        object.set("KEY", finalDto.get("key"));             object.remove("key");
        object.set("CHAR_SET", finalDto.get("char_SET"));   object.remove("char_SET");
        object.set("REQ_DATA", finalDto.get("req_DATA"));   object.remove("req_DATA");

        JsonNode accountDto = object.get("REQ_DATA").get(0);
        object = (ObjectNode) accountDto;
        object.set("TRSC_SEQ_NO", accountDto.get("trsc_SEQ_NO")); object.remove("trsc_SEQ_NO");
        object.set("BANK_CD", accountDto.get("bank_CD"));         object.remove("bank_CD");
        object.set("SEARCH_ACCT_NO", accountDto.get("search_ACCT_NO")); object.remove("search_ACCT_NO");
        object.set("ACNM_NO", accountDto.get("acnm_NO"));         object.remove("acnm_NO");
        object.set("ICHE_AMT", accountDto.get("iche_AMT")); object.remove("iche_AMT");
        object.set("NAME", accountDto.get("name"));         object.remove("name");

        String req = mapper.writeValueAsString(finalDto);
        //String req = mapper.writeValueAsString(bankRequestDto).toUpperCase();
        String domain = url + req;
        log.info(domain);

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
            log.info("jsonNode ... {} ", jsonNode);

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
        log.info("contentType... {}" , connection.getContentType());

        BufferedReader in = new BufferedReader( new InputStreamReader(
                connection.getInputStream(), "UTF-8" ) );
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

    public static Integer getBalance() {
        String KEY = Constants.VAPG_KEY[2];
        String TRSC_SEQ_NO;

        String req = "{\"SECR_KEY\":\""+SECR_KEY+"\",\"KEY\":\""+KEY+"\",\"TRT_INST_CD\":\""+TRT_INST_CD+"\",\"BANK_CD\":\""+BANK_CD+"\",\"TRSC_SEQ_NO\":\"114145669120\"}";
        vapgUrl += req;
        log.info("vapgUrl... getBalance {}", vapgUrl);

        String WDRW_CAN_AMT = null;
        try {
            URL url = new URL(vapgUrl);
            HttpsURLConnection http = (HttpsURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            System.out.println(http.getResponseCode() + " " + http.getResponseMessage());

            JsonNode jsonNode = getReturnNode(http);
            System.out.println(jsonNode);

            String code = jsonNode.get("RESP_CD").toString().substring(1, jsonNode.get("RESP_CD").toString().length() - 1);
            String completed = "0000";

            if (completed.equals(code)) {
                WDRW_CAN_AMT = jsonNode.get("WDRW_CAN_AMT").toString().substring(1, jsonNode.get("WDRW_CAN_AMT").toString().length() - 1);
                Integer.parseInt(WDRW_CAN_AMT);
            }
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException");
        } catch (IOException e) {
        }
        return Integer.parseInt(WDRW_CAN_AMT);
    }

    public static void transferMoney() {
        String KEY = Constants.VAPG_KEY[0];
        String WDRW_ACCT_NO = Constants.WDRW_ACCT_NO;
        String WDRW_ACCT_NM = Constants.WDRW_ACCT_NM;

        vapgUrl += "{\"SECR_KEY\":\""+SECR_KEY+"\",\"KEY\":\""+KEY+"\",\"TRT_INST_CD\":\""+TRT_INST_CD+"\",\"BANK_CD\":\""+BANK_CD+"\",\"TRSC_SEQ_NO\":\"693653753245\",\"RCV_BNK_CD\":\"004\",\"RCV_ACCT_NO\":\"87050100045847\",\"WDRW_ACCT_NO\":\""+WDRW_ACCT_NO+"\",\"TRSC_AMT\":\"10\",\"WDRW_ACCT_NM\":\""+WDRW_ACCT_NM+"\"}";
        log.info("vapgUrl...tranferMoney {}", vapgUrl);

        boolean transferRes = getTranferInfo(vapgUrl);
    }

    public static boolean getTranferInfo(String domain){
        boolean result = true;

        try {
            URL url = new URL(domain);
            HttpsURLConnection http = (HttpsURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            log.info(http.getResponseCode()+" "+http.getResponseMessage());

            JsonNode jsonNode = getReturnNode(http);
            log.info("transfer response ... {} ", jsonNode);

            String code = jsonNode.get("RESP_CD").toString().substring(1,jsonNode.get("RESP_CD").toString().length()-1);
            String msg = jsonNode.get("RESP_MSG").toString().substring(1,jsonNode.get("RESP_MSG").toString().length()-1);

            log.info("RESP_CD : {}, RESP_MSG : {}", code, msg);
            String completed = "0000";

            if (completed.equals(code)){
                String rcvAccount = jsonNode.get("RCV_ACCT_NO").toString().substring(1,jsonNode.get("RCV_ACCT_NO").toString().length()-1);
                String acctName = jsonNode.get("RCV_ACCT_NM").toString().substring(1,jsonNode.get("RCV_ACCT_NM").toString().length()-1);
                String trscAmt = jsonNode.get("TRST_AMT").toString().substring(1,jsonNode.get("TRST_AMT").toString().length()-1);
                String balAmt = jsonNode.get("BAL_AMT").toString().substring(1,jsonNode.get("BAL_AMT").toString().length()-1);
                log.info("{}님의 {} 계좌로 {}원 입급 완료되었습니다. 잔액 {}원", acctName, rcvAccount, trscAmt, balAmt);
                result = true;
            }else{
                result = false;
            }
        } catch (MalformedURLException e) {
            log.info("MalformedURLException");
        } catch (IOException e) {
        }
        return result;
    }
}