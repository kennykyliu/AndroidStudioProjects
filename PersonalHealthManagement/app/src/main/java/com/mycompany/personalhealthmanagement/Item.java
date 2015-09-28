/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycompany.personalhealthmanagement;

/**
 * Represents an Item in our application. Each item has a name, id, full size image url and
 * thumbnail url.
 */
public class Item {

    public static Item[] ITEMS = new Item[] {
        new Item("Personal Info", R.drawable.personal_info, 0, PersonalInfo.class),
        new Item("Calories", R.drawable.health_apple, 0, CaloriesMain.class),
        new Item("Sleep Management", R.drawable.sleep, 0, CaloriesMain.class),
        new Item("Statistics", R.drawable.statistics, 0, Statistics.class),
        new Item("Summary", R.drawable.health_summary, R.string.bmi_intro, DetailActivity.class),
        new Item("Photo Manager", R.drawable.camera, 0, PhotoManager.class),
        new Item("User Preference", R.drawable.settings, 0, UserPreference.class),
        new Item("Log out", R.drawable.logout, 0, null),

    };

    public static Item getItem(int id) {
        for (Item item : ITEMS) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    private final String mName;
    private final int mImageId;
    private final int mContentId;
    private Class<?>  mMainPageClass;

    Item (String name, int imageId, int contentId, Class<?> mainPageClass) {
        mName = name;
        mImageId = imageId;
        mContentId = contentId;
        mMainPageClass = mainPageClass;
    }

    public int getId() {
        return mName.hashCode();
    }

    public String getName() {
        return mName;
    }

    public int getPhotoId() {
        return mImageId;
    }

    public int getThumbnailId() {
        return mImageId;
    }

    public int getContentId() {
        return mContentId;
    }

    public Class<?> getMainPageClass() {
        return mMainPageClass;
    }
}
