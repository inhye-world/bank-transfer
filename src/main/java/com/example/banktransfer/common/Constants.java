package com.example.banktransfer.common;

public class Constants {
    private Constants(){}

    public static final String ACCTNM_URL ="https://dev2.coocon.co.kr:8443/sol/gateway/acctnm_rcms_wapi.jsp?JSONData=";
    public static final String VAPG_URL ="https://dev2.coocon.co.kr:8443/sol/gateway/vapg_wapi.jsp?JSONData=";

    //acctnm parameter info
    public static final String ACCTNM_SECR_KEY = "ACCTTEST";
    public static final String ACCTNM_KEY = "ACCTNM_RCMS_WAPI";
    public static final String ACCTNM_CHAR_SET = "UTF-8";


    //vapg parameter info
    public static final String VAPG_SECR_KEY = "LYgZORKYJ9FtneV5XMwN";
    public static final String[] VAPG_KEY = {"6120", "6170", "6140"};
    public static final String TRT_INST_CD = "02042091";
    public static final String BANK_CD = "020";
    public static final String WDRW_ACCT_NO = "0000000000000000";
    public static final String WDRW_ACCT_NM = "해피투씨유";
}
