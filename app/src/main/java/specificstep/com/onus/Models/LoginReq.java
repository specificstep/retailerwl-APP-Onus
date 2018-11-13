package specificstep.com.onus.Models;


public class LoginReq {
    private String androidID;
    private String fcmid;
    private String icc_id;
    private String imei;
    private String key;
    private Object lat;
    private Object long1;
    private String mac;
    private String mpin;
    private String msg;
    private Object pgwid;
    private String reqtype;
    private String src;
    private String trid;
    private String version;
    private String waid;

    public String getIcc_id() {
        return this.icc_id;
    }

    public void setIcc_id(String icc_id) {
        this.icc_id = icc_id;
    }

    public String getFcmid() {
        return this.fcmid;
    }

    public void setFcmid(String fcmid) {
        this.fcmid = fcmid;
    }

    public String getAndroidID() {
        return this.androidID;
    }

    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReqtype() {
        return this.reqtype;
    }

    public void setReqtype(String reqtype) {
        this.reqtype = reqtype;
    }

    public String getSrc() {
        return this.src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTrid() {
        return this.trid;
    }

    public void setTrid(String trid) {
        this.trid = trid;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getImei() {
        return this.imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Object getLat() {
        return this.lat;
    }

    public void setLat(Object lat) {
        this.lat = lat;
    }

    public Object getLong1() {
        return this.long1;
    }

    public void setLong1(Object long1) {
        this.long1 = long1;
    }

    public String getWaid() {
        return this.waid;
    }

    public void setWaid(String waid) {
        this.waid = waid;
    }

    public Object getPgwid() {
        return this.pgwid;
    }

    public void setPgwid(Object pgwid) {
        this.pgwid = pgwid;
    }

    public String getMpin() {
        return this.mpin;
    }

    public void setMpin(String mpin) {
        this.mpin = mpin;
    }
}
