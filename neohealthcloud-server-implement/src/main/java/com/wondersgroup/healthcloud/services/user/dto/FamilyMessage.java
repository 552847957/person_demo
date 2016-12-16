package com.wondersgroup.healthcloud.services.user.dto;

import lombok.Data;

@Data
public class FamilyMessage {
    private String notifierUID; 
    private String receiverUID; 
    private String msgType; 
    private String msgTitle; 
    private String msgContent; 
    private String jumpUrl; 
    private String reqRecordID;
    
    public FamilyMessage() {

    } 
    
    public FamilyMessage(String notifierUID, String receiverUID, String msgType, String msgTitle, String msgContent) {
        this.notifierUID = notifierUID;
        this.receiverUID = receiverUID;
        this.msgType = msgType;
        this.msgTitle = msgTitle;
        this.msgContent = msgContent;
    } 
    
    public FamilyMessage(String notifierUID, String receiverUID, String msgType, String msgTitle, String msgContent,
            String jumpUrl, String reqRecordID) {
        this.notifierUID = notifierUID;
        this.receiverUID = receiverUID;
        this.msgType = msgType;
        this.msgTitle = msgTitle;
        this.msgContent = msgContent;
        this.jumpUrl = jumpUrl;
        this.reqRecordID = reqRecordID;
    } 
    
    
}
