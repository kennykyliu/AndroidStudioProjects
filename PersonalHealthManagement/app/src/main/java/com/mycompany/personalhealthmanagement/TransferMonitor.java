/*
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import android.content.Context;
import android.widget.LinearLayout;
/*
 * This view handles user interaction with a single transfer, such as giving the
 * option to pause and abort, and also showing the user the progress of the transfer.
 */
public class TransferMonitor {
    private static final String TAG = "TransferView";
    private TransferModel mModel;

    public TransferMonitor(TransferModel model) {

        mModel = model;

        refresh();
    }

    /* refresh method for public use */
    public int refresh() {
        return refresh(mModel.getStatus());
    }

    /*
     * We use this method within the class so that we can have the UI update
     * quickly when the user selects something
     */
    private int refresh(TransferModel.Status status) {
        int leftRes = 0;
        int progress = 0;
        switch (status) {
            case IN_PROGRESS:
                progress = mModel.getProgress();
                break;
            case PAUSED:
                progress = mModel.getProgress();
                break;
            case CANCELED:
                progress = 0;
                break;
            case COMPLETED:
                progress = 100;

//                if (mModel instanceof DownloadModel) {
//                    // if download completed, show option to open file
//                    Button button = (Button) findViewById(R.id.open);
//                    button.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            // get the file extension
//                            MimeTypeMap m = MimeTypeMap.getSingleton();
//                            String mimeType = m.getMimeTypeFromExtension(
//                                    MimeTypeMap.getFileExtensionFromUrl(
//                                            mModel.getUri().toString()));
//
//                            try {
//                                // try opening activity to open file
//                                Intent intent = new Intent(
//                                        Intent.ACTION_GET_CONTENT);
//                                intent.setDataAndType(mModel.getUri(), mimeType);
//                                mContext.startActivity(intent);
//                            } catch (ActivityNotFoundException e) {
//                                Log.d(TAG, "", e);
//                                // if file fails to be opened, show error
//                                // message
//                                Toast.makeText(
//                                        mContext,
//                                        R.string.nothing_found_to_open_file,
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                    button.setVisibility(View.VISIBLE);
//                }
                break;
        }
        return progress;
    }
}
