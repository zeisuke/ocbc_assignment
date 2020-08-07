package com.cwleong.ocbc_assignment.bean;

import java.util.ArrayList;
import java.util.HashMap;

public class MessagePayloadBean {
    private ArrayList<MessageBean> list;
    private HashMap<String, WalletBean> hm;

    public MessagePayloadBean(ArrayList<MessageBean> list) {
        this.list = list;
    }

    public ArrayList<MessageBean> getList() {
        return list;
    }

    public void setList(ArrayList<MessageBean> list) {
        this.list = list;
    }

    public HashMap<String, WalletBean> getHm() {
        return hm;
    }

    public void setHm(HashMap<String, WalletBean> hm) {
        this.hm = hm;
    }

    public static class MessageBean {
        private String sender;
        private String message;

        public MessageBean(String sender, String message) {
            this.sender = sender;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

    }

    public static class WalletBean {
        private String user;
        private Double amount;

        private HashMap <String, Double> hmOwnFrom = new HashMap<>();
        private HashMap <String, Double> hmOwnTo = new HashMap<>();

        public WalletBean(String user, Double amount) {
            this.user = user;
            this.amount = amount;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public HashMap<String, Double> getHmOwnFrom() {
            return hmOwnFrom;
        }

        public void setHmOwnFrom(HashMap<String, Double> hmOwnFrom) {
            this.hmOwnFrom = hmOwnFrom;
        }

        public HashMap<String, Double> getHmOwnTo() {
            return hmOwnTo;
        }

        public void setHmOwnTo(HashMap<String, Double> hmOwnTo) {
            this.hmOwnTo = hmOwnTo;
        }
    }
}

