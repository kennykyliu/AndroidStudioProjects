package com.mycompany.personalhealthmanagement;

import java.util.ArrayList;

public interface TaskCompleted {
    // Define data you like to return from AysncTask
    public void onTaskComplete(Constants.DynamoDBManagerType ddbType,
                               ArrayList<UserProfile> localUP);
}
