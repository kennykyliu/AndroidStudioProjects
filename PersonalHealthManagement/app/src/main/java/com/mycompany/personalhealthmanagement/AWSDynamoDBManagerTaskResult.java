package com.mycompany.personalhealthmanagement;

import java.util.ArrayList;

public class AWSDynamoDBManagerTaskResult {
    private Constants.DynamoDBManagerType taskType;
    private String tableStatus;
    private ArrayList<UserProfile> resultList;

    public Constants.DynamoDBManagerType getTaskType() {
        return taskType;
    }

    public void setTaskType(Constants.DynamoDBManagerType taskType) {
        this.taskType = taskType;
    }

    public String getTableStatus() {
        return tableStatus;
    }

    public void setTableStatus(String tableStatus) {
        this.tableStatus = tableStatus;
    }

    public void setItemList(ArrayList<UserProfile> resultList) {
        this.resultList = resultList;
    }

    public ArrayList<UserProfile> getItemList() {
        return resultList;
    }
}
