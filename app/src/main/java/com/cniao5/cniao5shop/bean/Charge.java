package com.cniao5.cniao5shop.bean;

import java.util.Map;

public class Charge {
    private String id;
    private String object;
    private Long created;
    private Boolean livemode;
    private Boolean paid;
    private Boolean refunded;
    private Object app;
    private String channel;
    private String order_no;
    private String client_ip;
    private Integer amount;
    private Integer amount_settle;
    private String currency;
    private String subject;
    private String body;
    private Long time_paid;
    private Long time_expire;
    private Long time_settle;
    private String transaction_no;
    private ChargeRefundCollection refunds;
    private Integer amount_refunded;
    private String failure_code;
    private String failure_msg;
    private Map<String, String> metadata;
    private Map<String, Object> credential;
    private Map<String, String> extra;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Boolean getLivemode() {
        return livemode;
    }

    public void setLivemode(Boolean livemode) {
        this.livemode = livemode;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Boolean getRefunded() {
        return refunded;
    }

    public void setRefunded(Boolean refunded) {
        this.refunded = refunded;
    }

    public Object getApp() {
        return app;
    }

    public void setApp(Object app) {
        this.app = app;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getClient_ip() {
        return client_ip;
    }

    public void setClient_ip(String client_ip) {
        this.client_ip = client_ip;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount_settle() {
        return amount_settle;
    }

    public void setAmount_settle(Integer amount_settle) {
        this.amount_settle = amount_settle;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getTime_paid() {
        return time_paid;
    }

    public void setTime_paid(Long time_paid) {
        this.time_paid = time_paid;
    }

    public Long getTime_expire() {
        return time_expire;
    }

    public void setTime_expire(Long time_expire) {
        this.time_expire = time_expire;
    }

    public Long getTime_settle() {
        return time_settle;
    }

    public void setTime_settle(Long time_settle) {
        this.time_settle = time_settle;
    }

    public String getTransaction_no() {
        return transaction_no;
    }

    public void setTransaction_no(String transaction_no) {
        this.transaction_no = transaction_no;
    }

    public ChargeRefundCollection getRefunds() {
        return refunds;
    }

    public void setRefunds(ChargeRefundCollection refunds) {
        this.refunds = refunds;
    }

    public Integer getAmount_refunded() {
        return amount_refunded;
    }

    public void setAmount_refunded(Integer amount_refunded) {
        this.amount_refunded = amount_refunded;
    }

    public String getFailure_code() {
        return failure_code;
    }

    public void setFailure_code(String failure_code) {
        this.failure_code = failure_code;
    }

    public String getFailure_msg() {
        return failure_msg;
    }

    public void setFailure_msg(String failure_msg) {
        this.failure_msg = failure_msg;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Map<String, Object> getCredential() {
        return credential;
    }

    public void setCredential(Map<String, Object> credential) {
        this.credential = credential;
    }

    public Map<String, String> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, String> extra) {
        this.extra = extra;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
