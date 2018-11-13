package specificstep.com.onus.GlobalClasses;

/**
 * Created by ubuntu on 12/1/17.
 */

public class URL {
    //public static String base_url = "http://demo.ssuspl.com/webservices/"; //
    //public static String base_url = "http://chagtelecom.in/webservices/";   // test

   // public static String base_url = "http://www.onusnetworks.com/webservices/"; //live
    public static String base_url = "http://192.168.30.117:8026/webservices/"; //local


    public static String register = base_url + "register";
    public static String company = base_url + "company";
    public static String product = base_url + "product";
    public static String state = base_url + "state";
    public static String login = base_url + "login";
    public static String forgot_password = base_url + "forgotpassword";
    public static String recharge = base_url + "recharge";
    public static String number_tracer = base_url + "numbertracer";
    public static String search_recharge = base_url + "searchrecharge";
    public static String latest_recharge = base_url + "latestrecharge";
    public static String balance = base_url + "balance";
    public static String setting = base_url + "setting";

    public static String changePassword = base_url + "changepass";
    public static String cashBook = base_url + "cashbook";
    public static String accountLedger = base_url + "accounts";


    public static String complain = base_url + "complain";
    public static String complainList = base_url + "complainlist";

    // 2017_05_02 - get parent user details URL
    public static String GET_PARENT_USER_DETAILS = base_url + "getparent";

    // 2017_05_29 - Get browse plan type and it's type URL
    public static String GET_PLANS_TYPE = base_url+"Getplantype";
    public static String GET_PLANS = base_url +"Getplans";

  //  public static String GET_PLANS_TYPE = "http://qa.ssuspl.com/Getplantype";
   // public static String GET_PLANS = "http://qa.ssuspl.com/Getplans";

    //2018_10_30 - forgot password URL
    public static String GET_FORGOT_OTP = base_url + "forgototp";
    public static String GET_FORGOT_CHANGE_PASSWORD = base_url + "newpass";

    // - Get name using mobile number or dth number



}
