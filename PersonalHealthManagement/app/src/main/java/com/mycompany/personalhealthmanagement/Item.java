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

import android.content.Intent;

/**
 * Represents an Item in our application. Each item has a name, id, full size image url and
 * thumbnail url.
 */
public class Item {

    public static Item[] ITEMS = new Item[] {
            new Item("Calories", "Romain Guya", R.drawable.health_apple, R.string.item_A, CaloriesMain.class),
            new Item("Sleep management", "Romain Guyb", R.drawable.health_apple, R.string.item_B, CaloriesMain.class),
            new Item("Personal Info", "Romain Guyc", R.drawable.health_apple, R.string.item_C, CaloriesMain.class),
            new Item("Summary", "Romain Guyd", R.drawable.health_summary, R.string.bacon_ipsum, CaloriesMain.class),
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
    private final String mAuthor;
    private final int mImageId;
    private final int mContentId;
    private Class<?>  mMainPageClass;

    Item (String name, String author, int imageId, int contentId, Class<?> mainPageClass) {
        mName = name;
        mAuthor = author;
        mImageId = imageId;
        mContentId = contentId;
        mMainPageClass = mainPageClass;
    }

    public int getId() {
        return mName.hashCode() + mAuthor.hashCode();
    }

    public String getAuthor() {
        return mAuthor;
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
