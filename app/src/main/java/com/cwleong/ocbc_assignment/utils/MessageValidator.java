package com.cwleong.ocbc_assignment.utils;

import com.cwleong.ocbc_assignment.bean.MessagePayloadBean;
import com.cwleong.ocbc_assignment.bean.MessageValidatorBean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MessageValidator {

    public static final String COMMAND_LOGIN = "login";
    public static final String COMMAND_TOPUP = "topup";
    public static final String COMMAND_PAY = "pay";

    private static final String MSG_COMMAND_INVALID = "Invalid command";
    private static final String MSG_INVALID_TOPUP = "Invalid topup commond e.g.(topup <amount>)";
    private static final String MSG_INVALID_PAY_USER_NOT_EXIST = "Transfer User Account (%1$s) not exist";
    private static final String MSG_USER_AUTHENTICATION_FAILED = "Authentication failed, please login first.";

    private static final String MSG_LOGIN_GREETING = "Hello, %1$s!";
    private static final String MSG_BALANCE = "Your balance is %1$s.";
    private static final String MSG_TRANSFER = "Transfered %1$s to %2$s";
    private static final String MSG_OWN_FROM = "Owning %1$s from %2$s";
    private static final String MSG_OWN_TO = "Owning %1$s to %2$s";

    static String[] commands = {COMMAND_LOGIN, COMMAND_TOPUP, COMMAND_PAY};

    public static MessageValidatorBean processValidate(String message, String currentUser, HashMap<String, MessagePayloadBean.WalletBean> hm){

        String[] msgArr = message.split(" ");

        String command = msgArr[0];
        if (!Arrays.stream(commands).anyMatch(command::equals)){
            return new MessageValidatorBean(false, null,null,null,null, MSG_COMMAND_INVALID, hm);
        }
        String[] nameArr;
        String from, to;
        double amount;
        MessagePayloadBean.WalletBean walletBean;
        StringBuffer msg = new StringBuffer();
        switch (command){
            case COMMAND_LOGIN:
                if (!(msgArr.length >= 2)) {
                    return new MessageValidatorBean(false, COMMAND_LOGIN,null,null,null, MSG_COMMAND_INVALID, hm);
                }
                nameArr = Arrays.copyOfRange(msgArr, 1, msgArr.length);
                from = String.join(" ", nameArr);
                walletBean = hm.get(from.toUpperCase());
                if (walletBean == null){
                    amount = 0.0;
                    walletBean = new MessagePayloadBean.WalletBean(from, amount);
                    hm.put(from.toUpperCase(), walletBean);
                }else{
                    amount = walletBean.getAmount() == null ? 0.0 : walletBean.getAmount();
                }
                msg.append(String.format(MSG_LOGIN_GREETING, from));
                msg.append(getMessageOwnFrom(walletBean));
                msg.append("\n").append(String.format(MSG_BALANCE, String.valueOf(amount)));
                msg.append(getMessageOwnTo(walletBean));

                return new MessageValidatorBean(true, COMMAND_LOGIN, from, null, null, msg.toString(), hm);
            case COMMAND_TOPUP:
                if (!(msgArr.length >= 2)) {
                    return new MessageValidatorBean(false, COMMAND_TOPUP,null,null,null, MSG_COMMAND_INVALID, hm);
                }

                if (currentUser == null){
                    return new MessageValidatorBean(false, COMMAND_TOPUP,null,null,null, MSG_USER_AUTHENTICATION_FAILED, hm);
                }

                if (msgArr.length != 2){
                    return new MessageValidatorBean(false, COMMAND_TOPUP,null,null,null, MSG_COMMAND_INVALID, hm);
                }
                return processTopUp(msgArr, currentUser, hm);
            case COMMAND_PAY:
                if (!(msgArr.length >= 3)) {
                    return new MessageValidatorBean(false, COMMAND_PAY,null,null,null, MSG_COMMAND_INVALID, hm);
                }

                if (currentUser == null){
                    return new MessageValidatorBean(false, COMMAND_PAY,null,null,null, MSG_USER_AUTHENTICATION_FAILED, hm);
                }

                return processPay(msgArr, currentUser, hm);

            default:
                return new MessageValidatorBean(false, null,null,null,null, MSG_COMMAND_INVALID, hm);

        }

    }

    private static MessageValidatorBean processTopUp(String[] msgArr, String currentUser, HashMap<String, MessagePayloadBean.WalletBean> hm){
        String[] nameArr;
        double amount;
        MessagePayloadBean.WalletBean walletBean;
        StringBuffer msg = new StringBuffer();
        try{
            amount = Double.parseDouble(msgArr[msgArr.length - 1]);
            if (amount == 0){
                return new MessageValidatorBean(true, COMMAND_TOPUP,null,null,null, String.format(MSG_BALANCE, String.valueOf(0.0)), hm);
            }

            walletBean = hm.get(currentUser.toUpperCase());

            double finalAmount;
            if (walletBean == null){
                finalAmount = amount;
                walletBean = new MessagePayloadBean.WalletBean(currentUser, 0.0);
            }else{
                double walletAmount = walletBean.getAmount() == null ? 0.0 : walletBean.getAmount();
                finalAmount = walletAmount + amount;
            }

            if (walletBean.getHmOwnTo() != null){
                HashMap<String, Double> hmOwnTo = walletBean.getHmOwnTo();
                HashMap<String, Double> hmOwnFrom;
                MessagePayloadBean.WalletBean walletBeanFrom;
                for (Map.Entry<String, Double> entry : hmOwnTo.entrySet()) {
                    String key = entry.getKey();
                    Double value = entry.getValue();
                    if (finalAmount > 0){
                        if (finalAmount >= value){
                            finalAmount = finalAmount - value;
                            if (value > 0){
                                msg.append("\n").append(String.format(MSG_TRANSFER, String.valueOf(value), key));
                            }
                            hmOwnTo.put(key.toUpperCase(), 0.0);
                            walletBean.setHmOwnTo(hmOwnTo);

                            walletBeanFrom = hm.get(key.toUpperCase());
                            if (walletBeanFrom == null){
                                walletBeanFrom = new MessagePayloadBean.WalletBean(key.toUpperCase(), 0.0);
                            }
                            hmOwnFrom = walletBeanFrom.getHmOwnFrom();
                            hmOwnFrom.put(currentUser.toUpperCase(), hmOwnFrom.get(currentUser.toUpperCase()) - value);
                            walletBeanFrom.setHmOwnFrom(hmOwnFrom);
                            walletBeanFrom.setAmount(walletBeanFrom.getAmount() + value);
                            hm.put(key.toUpperCase(), walletBeanFrom);
                        }else {
                            value = value - finalAmount;
                            if (finalAmount > 0){
                                msg.append("\n").append(String.format(MSG_TRANSFER, String.valueOf(finalAmount), key));
                            }

                            hmOwnTo.put(key.toUpperCase(), value);
                            MessagePayloadBean.WalletBean walletBeanTo = hm.get(key.toUpperCase());
                            walletBeanTo.setAmount(walletBeanTo.getAmount() + finalAmount);
                            HashMap<String, Double> userHmOwnFrom = walletBeanTo.getHmOwnFrom();
                            userHmOwnFrom.put(currentUser.toUpperCase(), userHmOwnFrom.get(currentUser.toUpperCase()) - finalAmount);
                            walletBeanTo.setHmOwnFrom(userHmOwnFrom);
                            hm.put(key.toUpperCase(), walletBeanTo);
                            walletBean.setHmOwnTo(hmOwnTo);

                            finalAmount = 0;

                            walletBeanFrom = hm.get(key.toUpperCase());
                            if (walletBeanFrom == null){
                                walletBeanFrom = new MessagePayloadBean.WalletBean(key.toUpperCase(), 0.0);
                            }
                            hmOwnFrom = walletBeanFrom.getHmOwnFrom();
                            hmOwnFrom.put(currentUser.toUpperCase(), hmOwnFrom.get(currentUser.toUpperCase()) - finalAmount);
                            walletBeanFrom.setHmOwnFrom(hmOwnFrom);
                            walletBeanFrom.setAmount(walletBeanFrom.getAmount() + finalAmount);
                            hm.put(key.toUpperCase(), walletBeanFrom);


                        }
                    }else{
                        break;
                    }
                }
            }

            walletBean.setAmount(finalAmount);
            hm.put(currentUser.toUpperCase(), walletBean);
            msg.append("\n").append(String.format(MSG_BALANCE, String.valueOf(finalAmount)));
            msg.append(getMessageOwnFrom(walletBean));
            msg.append(getMessageOwnTo(walletBean));

            return new MessageValidatorBean(true, COMMAND_TOPUP,currentUser,null,finalAmount, trim(msg.toString()), hm);
        }catch (NumberFormatException e){
            return new MessageValidatorBean(false, COMMAND_TOPUP,null,null,null, MSG_INVALID_TOPUP, hm);
        }
    }

    private static MessageValidatorBean processPay(String[] msgArr, String currentUser, HashMap<String, MessagePayloadBean.WalletBean> hm){
        String[] nameArr;
        String to;
        double amount, amountOwnTo=0.0, transferAmt = 0.0;
        StringBuffer msg = new StringBuffer();
        try{
            amount = Double.parseDouble(msgArr[msgArr.length -1]);
        }catch (NumberFormatException e){
            return new MessageValidatorBean(false, COMMAND_PAY,null,null,null, MSG_COMMAND_INVALID, hm);
        }
        nameArr = Arrays.copyOfRange(msgArr, 1, msgArr.length-1);
        to = String.join(" ", nameArr);

        MessagePayloadBean.WalletBean walletBeanTo = hm.get(to.toUpperCase());
        if (walletBeanTo == null) {
            return new MessageValidatorBean(false, COMMAND_PAY,null,null,null, String.format(MSG_INVALID_PAY_USER_NOT_EXIST, to), hm);
        }

        MessagePayloadBean.WalletBean walletBeanFrom = hm.get(currentUser.toUpperCase());

        if (walletBeanFrom.getHmOwnFrom().get(to.toUpperCase()) != null){
            HashMap<String, Double> hmOwnFrom = walletBeanFrom.getHmOwnFrom();
            HashMap<String, Double> hmOwnTo = walletBeanTo.getHmOwnTo();
            double amtOwnFrom = hmOwnFrom.get(to.toUpperCase());
            double amtOwnTo = hmOwnTo.get(currentUser.toUpperCase());
            if ((amount - amtOwnFrom) >= 0){
                hmOwnFrom.put(to.toUpperCase(), 0.0);
                walletBeanFrom.setHmOwnFrom(hmOwnFrom);
                walletBeanFrom.setAmount(walletBeanFrom.getAmount() - (amount - amtOwnFrom));

                hmOwnTo.put(currentUser.toUpperCase(), 0.0);
                walletBeanTo.setHmOwnTo(hmOwnTo);
                walletBeanTo.setAmount(walletBeanTo.getAmount() + (amount - amtOwnFrom));

                transferAmt = amount;
            }else{
                hmOwnFrom.put(to.toUpperCase(), (amtOwnFrom - amount));
                walletBeanFrom.setHmOwnFrom(hmOwnFrom);

                hmOwnTo.put(currentUser.toUpperCase(), (amtOwnTo - amount));
                walletBeanTo.setHmOwnTo(hmOwnTo);
                transferAmt = amount;
            }
        }else{
            Double amountFrom = walletBeanFrom.getAmount() - amount;
            if (amountFrom <= 0){
                walletBeanFrom.setAmount(0.0);
                amountOwnTo = amountFrom;
                transferAmt = amount + amountFrom;
                walletBeanTo.setAmount(walletBeanTo.getAmount() + (amount + amountFrom));

                HashMap<String, Double> hmOwnTo = walletBeanFrom.getHmOwnTo();
                if (hmOwnTo == null){
                    hmOwnTo = new HashMap<>();
                }
                hmOwnTo.put(to.toUpperCase(), Math.abs(amountOwnTo));

                HashMap<String, Double> hmOwnFrom = walletBeanTo.getHmOwnFrom();
                if (hmOwnFrom == null) {
                    hmOwnFrom = new HashMap<>();
                }
                hmOwnFrom.put(currentUser.toUpperCase(), Math.abs(amountFrom));
            }else{
                walletBeanFrom.setAmount(amountFrom);
                walletBeanTo.setAmount(walletBeanTo.getAmount() + amount);

                transferAmt = amount;
            }
        }
        hm.put(currentUser.toUpperCase(), walletBeanFrom);
        hm.put(to.toUpperCase(), walletBeanTo);

        if (transferAmt > 0) {
            msg.append(String.format(MSG_TRANSFER, String.valueOf(transferAmt), to));
        }
        msg.append("\n").append(String.format(MSG_BALANCE, String.valueOf(walletBeanFrom.getAmount())));
        msg.append(getMessageOwnTo(walletBeanFrom));
        msg.append(getMessageOwnFrom(walletBeanFrom));

        return new MessageValidatorBean(true, COMMAND_PAY,null,null,null, trim(msg.toString()), hm);
    }

    private static String getMessageOwnFrom(MessagePayloadBean.WalletBean bean){
        StringBuffer msg = new StringBuffer();
        if (bean.getHmOwnFrom() != null){
            HashMap<String, Double> hm = bean.getHmOwnFrom();
            for (Map.Entry<String, Double> entry : hm.entrySet()) {
                String key = entry.getKey();
                Double value = entry.getValue();
                if (value > 0){
                    msg.append("\n").append(String.format(MSG_OWN_FROM, String.valueOf(value), key));
                }
            }
        }
        return msg.toString();
    }

    private static String getMessageOwnTo(MessagePayloadBean.WalletBean bean){
        StringBuffer msg = new StringBuffer();
        if (bean.getHmOwnFrom() != null){
            HashMap<String, Double> hm = bean.getHmOwnTo();
            for (Map.Entry<String, Double> entry : hm.entrySet()) {
                String key = entry.getKey();
                Double value = entry.getValue();
                if (value > 0){
                    msg.append("\n").append(String.format(MSG_OWN_TO, String.valueOf(value), key));
                }
            }
        }
        return msg.toString();
    }

    private static String trim(String msg){
        if (msg.startsWith("\n")) {
            return msg.substring(1);
        }
        return msg;
    }
}
