package com.mycompany.personalhealthmanagement;

public class UserProfile {
    private long indexNo;
    private String userName;
    private String itemName;
    private long date;
    private int nValue;
    private String sValue;
    Constants.DynamoDBManagerType actionType;

    public UserProfile(Constants.DynamoDBManagerType actionType) {
        this.actionType = actionType;
    }
    public UserProfile(long indexNo,
                       String userName,
                       String itemName,
                       long date,
                       int nValue,
                       String sValue,
                       Constants.DynamoDBManagerType actionType) {
        this.indexNo = indexNo;
        this.userName = userName;
        this.itemName = itemName;
        this.date = date;
        this.nValue = nValue;
        this.sValue = sValue;
        this.actionType = actionType;
    }
    public long getIndexNo() {
        return indexNo;
    }
    public String getUserName() {
        return userName;
    }
    public String getItemName() {
        return itemName;
    }
    public int getNValue() {
        return nValue;
    }
    public String getSValue() {
        return sValue;
    }
    public long getDate() {
        return date;
    }

    public Constants.DynamoDBManagerType getActionType() {
        return actionType;
    }
}