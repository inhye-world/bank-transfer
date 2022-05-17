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
    public static Boolean getAccountName(BankRequestDto bankRequestDto) {

        String url = "https://dev2.coocon.co.kr:8443/sol/gateway/acctnm_rcms_wapi.jsp?JSONData=";

        String key = bankRequestDto.getKEY();
        String secr_key = bankRequestDto.getSECR_KEY();
        String char_set = bankRequestDto.getCHAR_SET();
        List<AccountRequestDto> dto2 = bankRequestDto.getREQ_DATA();
        String bank_cd = dto2.get(0).getBANK_CD();
        String search_acct_no = dto2.get(0).getSEARCH_ACCT_NO();
        String acnm_no = dto2.get(0).getACNM_NO();
        String iche_amt = dto2.get(0).getICHE_AMT();
        String trsc_seq_no = randomCode(7);
        String name = dto2.get(0).getNAME();

        bank_cd = getBankCode(bank_cd);

        String req = "{\"SECR_KEY\":\""+secr_key+"\",\"KEY\":\""+key+"\"," +
                "\"DOMN\":\"https://dev2.coocon.co.kr:8443/sample_acctnm_rcms_kib.jsp\",\"TRG\":\"\",\"SORT\":\"\"," + "\"PG_PER_CNT\":\"\",\"PG_NO\":\"\"," +
                "\"REQ_DATA\":[{\"BANK_CD\":\""+bank_cd+"\"," +
                "\"SEARCH_ACCT_NO\":\""+search_acct_no+"\",\"ACNM_NO\":\""+acnm_no+"\"," +
                "\"ICHE_AMT\":\""+iche_amt+"\",\"TRSC_SEQ_NO\":\""+trsc_seq_no+"\"}]}\n";

        String domain = url + req;
        String accountName = getAccountInfo(domain);
        if (name.equals(accountName)){
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

    public static String randomCode(int length){
        String rdStr = "";

        StringBuffer sb = new StringBuffer();
        Random rn = new Random();

        for(int i = 0; i < length; i++){
            sb.append(rn.nextInt(10));
            rdStr = sb.toString();
        }
        return rdStr;
    }

    public static String getBankCode(String bank_cd){
        HashMap<String,String> bankList = new HashMap<String, String>() {
            {
                put("산업", "002");put("기업", "003");put("국민", "004");put("외환", "005");put("수협", "007");
                put("농협", "011");put("우리", "020");put("제일", "023");put("씨티", "027");put("대구", "031");
                put("부산", "032");put("광주", "034");put("제주", "035");put("전북", "037");put("경남", "039");
                put("금고", "045");put("신협", "048");put("우체국", "071");put("하나", "081");put("신한", "088");
            }
        };
        return bankList.get(bank_cd);
    }
}
