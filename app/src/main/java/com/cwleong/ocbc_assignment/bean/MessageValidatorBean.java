package com.cwleong.ocbc_assignment.bean;

import java.util.HashMap;

public class MessageValidatorBean {
    private Boolean isValid;
    private String command;
    private String from;
    private String to;
    private Double amount;
    private String msg;
    private HashMap<String, MessagePayloadBean.WalletBean> hm;

    public MessageValidatorBean(Boolean isValid, String command, String from, String to, Double amount, String msg, HashMap<String, MessagePayloadBean.WalletBean> hm) {
        this.isValid = isValid;
        this.command = command;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.msg = msg;
        this.hm = hm;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public HashMap<String, MessagePayloadBean.WalletBean> getHm() {
        return hm;
    }

    public void setHm(HashMap<String, MessagePayloadBean.WalletBean> hm) {
        this.hm = hm;
    }
}
