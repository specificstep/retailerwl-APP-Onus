package specificstep.com.onus.Models;

/**
 * Created by ubuntu on 19/1/17.
 */

public class Recharge {

    String client_trans_id;
    String mo_no;
    String amount;
    String compnay_name;
    String product_name;
    String trans_date_time;
    String status;
    String recharge_status;
    String operator_trans_id;

    public String getClient_trans_id() {
        return client_trans_id;
    }

    public void setClient_trans_id(String client_trans_id) {
        this.client_trans_id = client_trans_id;
    }

    public String getMo_no() {
        return mo_no;
    }

    public void setMo_no(String mo_no) {
        this.mo_no = mo_no;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCompnay_name() {
        return compnay_name;
    }

    public void setCompnay_name(String compnay_name) {
        this.compnay_name = compnay_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getTrans_date_time() {
        return trans_date_time;
    }

    public void setTrans_date_time(String trans_date_time) {
        this.trans_date_time = trans_date_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecharge_status() {
        return recharge_status;
    }

    public void setRecharge_status(String recharge_status) {
        this.recharge_status = recharge_status;
    }

    public String getOperator_trans_id() {
        return operator_trans_id;
    }

    public void setOperator_trans_id(String operator_trans_id) {
        this.operator_trans_id = operator_trans_id;
    }
}
