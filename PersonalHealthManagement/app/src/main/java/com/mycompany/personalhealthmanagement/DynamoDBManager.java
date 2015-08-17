package com.mycompany.personalhealthmanagement;

import android.content.Context;
import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import java.util.ArrayList;

public class DynamoDBManager {

    private static final String TAG = "PHM-DynamoDBManager";
    public static AmazonClientManager clientManager = null;
    private static AmazonDynamoDBClient ddb = null;

    /*
     * Creates a table with the following attributes: Table name: testTableName
     * Hash key: userNo type N Read Capacity Units: 10 Write Capacity Units: 5
     */
    public static void init(Context context) {
        clientManager = new AmazonClientManager(context);
    }
    public static void createTable() {

        Log.d(TAG, "Create table called");

        AmazonDynamoDBClient ddb = clientManager.ddb();

        KeySchemaElement kse = new KeySchemaElement().withAttributeName(
                "indexNo").withKeyType(KeyType.HASH);
        AttributeDefinition ad = new AttributeDefinition().withAttributeName(
                "indexNo").withAttributeType(ScalarAttributeType.N);
        ProvisionedThroughput pt = new ProvisionedThroughput()
                .withReadCapacityUnits(10l).withWriteCapacityUnits(5l);

        CreateTableRequest request = new CreateTableRequest()
                .withTableName("PHM")
                .withKeySchema(kse).withAttributeDefinitions(ad)
                .withProvisionedThroughput(pt);

        try {
            Log.d(TAG, "Sending Create table request");
            ddb.createTable(request);
            Log.d(TAG, "Create request response successfully received");
        } catch (AmazonServiceException ex) {
            Log.e(TAG, "Error sending create table request", ex);
        }
    }

    /*
     * Retrieves the table description and returns the table status as a string.
     */
    public static String getTestTableStatus() {

        try {
            if (ddb == null) {
                ddb = clientManager.ddb();
            }

            DescribeTableRequest request = new DescribeTableRequest()
                    .withTableName(Constants.TABLE_NAME);
            DescribeTableResult result = ddb.describeTable(request);

            String status = result.getTable().getTableStatus();
            return status == null ? "" : status;

        } catch (ResourceNotFoundException e) {
        } catch (AmazonServiceException ex) {
            clientManager.wipeCredentialsOnAuthError(ex);
        }

        return "";
    }

    public static void insertItem(UserProfile userPref) {
        AmazonDynamoDBClient ddb = clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        UserPreference dataEntry = setUpUserPreference(userPref);
        try {
                Log.d(TAG, "Inserting users");
                mapper.save(dataEntry);
                Log.d(TAG, "Users inserted");
        } catch (AmazonServiceException ex) {
            Log.e(TAG, "Error inserting users");
        }
    }

    public static ArrayList<UserProfile> getItemList() {

        if (ddb == null) {
            ddb = clientManager.ddb();
        }

        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        try {
            PaginatedScanList<UserPreference> result = mapper.scan(
                    UserPreference.class, scanExpression);

            ArrayList<UserProfile> resultList = new ArrayList<UserProfile>();
            for (UserPreference ret : result) {
                resultList.add(retrieveUserPreference(ret));
            }
            return resultList;

        } catch (AmazonServiceException ex) {
            clientManager.wipeCredentialsOnAuthError(ex);
        }

        return null;
    }

    public UserProfile getItem(String itemName) {

        if (ddb == null) {
            ddb = clientManager.ddb();
        }
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
            UserPreference userPreference = mapper.load(UserPreference.class,
                    itemName);

            return retrieveUserPreference(userPreference);

        } catch (AmazonServiceException ex) {
            clientManager.wipeCredentialsOnAuthError(ex);
        }

        return null;
    }

    @DynamoDBTable(tableName = Constants.TABLE_NAME)
    public static class UserPreference {
        private long indexNo;
        private String userName;
        private String itemName;
        private long date;
        private int nValue;
        private String sValue;

        @DynamoDBHashKey(attributeName = "indexNo")
        public long getIndexNo() {
            return indexNo;
        }

        public void setIndexNo(long indexNo) {
            this.indexNo = indexNo;
        }

        @DynamoDBAttribute(attributeName = "userName")
        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        @DynamoDBAttribute(attributeName = "itemName")
        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        @DynamoDBAttribute(attributeName = "nvalue")
        public int getNValue() {
            return nValue;
        }

        public void setNValue(int nValue) {
            this.nValue = nValue;
        }

        @DynamoDBAttribute(attributeName = "svalue")
        public String getSValue() {
            return sValue;
        }

        public void setSValue(String sValue) {
            this.sValue = sValue;
        }

        @DynamoDBAttribute(attributeName = "date")
        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }
    }

    public static UserPreference setUpUserPreference(UserProfile userPref) {
        UserPreference dataEntry = new UserPreference();

        dataEntry.setIndexNo(userPref.getIndexNo());
        dataEntry.setUserName(userPref.getUserName());
        dataEntry.setItemName(userPref.getItemName());
        dataEntry.setDate(userPref.getDate());
        dataEntry.setNValue(userPref.getNValue());
        dataEntry.setSValue(userPref.getSValue());

        return dataEntry;
    }

    public static UserProfile retrieveUserPreference(UserPreference up) {
        UserProfile ret = new UserProfile(up.getIndexNo(),
                                          up.getUserName(),
                                          up.getItemName(),
                                          up.getDate(),
                                          up.getNValue(),
                                          up.getSValue(),
                                          Constants.DynamoDBManagerType.NO_ACTION);
        return ret;
    }
}
