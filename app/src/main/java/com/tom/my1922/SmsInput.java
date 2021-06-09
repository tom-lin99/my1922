package com.tom.my1922;

public class SmsInput {


    private String receiver;
    private String msg;

    public SmsInput(String receiver, String msg){
        super();
        this.receiver = receiver;
        this.msg = msg;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
