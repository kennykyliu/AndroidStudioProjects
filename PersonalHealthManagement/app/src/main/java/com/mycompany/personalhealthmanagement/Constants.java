/*
 * Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.mycompany.personalhealthmanagement;

import java.util.List;
import java.util.Random;

public class Constants {

    public static final String ACCOUNT_ID = "961239280963";
    public static final String IDENTITY_POOL_ID = "us-east-1:8f762fd9-6a40-4676-8c03-1b1eb447d67c";
    public static final String UNAUTH_ROLE_ARN = "arn:aws:iam::961239280963:role/Cognito_PersonalHealthManagementUnauth_Role";
    public static final String PHOTO_MANAGER_BUCKET_NAME = "awsphotomanager";
    public static final String USER_LIST_FILE_NAME = "userlist.txt";
    public static final String TABLE_NAME = "PHM";
    public static final int MAX_SIGNUP_USER = 999;
    public static final int DATA_REFRESH_DELAY = 500;
    public static final Random random = new Random();
    public static final String[] NAMES = new String[] {
            "Trevor", "Jim", "Jason", "Zach", "Matt", "Glenn", "Will", "Wade", "Trevor", "Jeremy",
            "Ryan", "Matty", "Steve", "Pavel"
    };
    public static String currUserName = null;
    public static long currUserID = 0;
    public static String currAlbumName = null;
    public static List<String> albumNameList;
    public static boolean photoAutoUpload = true;

    public static String getRandomName() {
        int name = random.nextInt(NAMES.length);

        return NAMES[name];
    }

    public static int getRandomScore() {
        return random.nextInt(1000) + 1;
    }
    public enum DynamoDBManagerType {
        NO_ACTION, GET_TABLE_STATUS, CREATE_TABLE, INSERT_ITEM, LIST_ITEMS, GET_ITEM, CLEAN_UP, CHECK_USER_EXISTENT,
        USER_LOGIN, SYNC_PERSONAL_INFO, SYNC_PERSONAL_INFO_HEIGHT, SYNC_TOTAL_CAL
    }
}
