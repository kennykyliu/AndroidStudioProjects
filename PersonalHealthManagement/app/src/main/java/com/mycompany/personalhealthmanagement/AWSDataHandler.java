package com.mycompany.personalhealthmanagement;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DatasetMetadata;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.mobileconnectors.cognito.SyncConflict;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class AWSDataHandler {
    private static final String TAG = "PHM-AWS_data_Handler";

    private CognitoSyncManager client;
    private Context context;

    public void refreshDatasetMetadata(Context context) {
        this.context = context;
        client = CognitoSyncClientManager.getInstance();
        new RefreshDatasetMetadataTask().execute();
    }

    private class RefreshDatasetMetadataTask extends
            AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;
        boolean authError;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(context, "Syncing",
                    "Please wait");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                client.refreshDatasetMetadata();
            } catch (DataStorageException dse) {
                Log.e(TAG, "failed to refresh dataset metadata", dse);
            } catch (NotAuthorizedException e) {
                Log.e(TAG, "failed to refresh dataset metadata", e);
                authError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            if (!authError) {
                refreshListData();
            } else {
                Log.i(TAG, "Meet an error.");
            }
        }
    }

    private void refreshListData() {
        Log.i(TAG, "refreshListData");
        String datasetName;
        Dataset dataset;
        DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT);
        List<DatasetMetadata> datasets = client.listDatasets();

        for (DatasetMetadata ds : datasets) {
            datasetName = ds.getDatasetName();
            synchronize(false, getDataSet(datasetName));
        }
    }

    private void synchronize(final boolean finish, Dataset dataset) {
        //Log.i(TAG, "synchronize: " + finish);
        dataset.synchronize(new Dataset.SyncCallback() {
            @Override
            public void onSuccess(Dataset dataset, final List<Record> newRecords) {
                Log.i("Sync", "success: " + dataset.getDatasetMetadata().getDatasetName());
            }

            @Override
            public void onFailure(final DataStorageException dse) {
                Log.i("Sync", "failure: ", dse);
            }

            @Override
            public boolean onConflict(final Dataset dataset,
                                      final List<SyncConflict> conflicts) {
                Log.i("Sync", "conflict: " + conflicts);
                return true;
            }

            @Override
            public boolean onDatasetsMerged(Dataset dataset, List<String> mergedDatasetNames) {
                Log.i("Sync", "merge: " + dataset.getDatasetMetadata().getDatasetName());
                return true;
            }

            @Override
            public boolean onDatasetDeleted(Dataset dataset, String datasetName) {
                Log.i("Sync", "delete: " + datasetName);
                return true;
            }
        });
    }

    public static AWSDataHandler getInstance() {
        AWSDataHandler aws = new AWSDataHandler();
        return aws;
    }

    public Dataset getDataSet(String datasetName) {
        if (client == null)
            client = CognitoSyncClientManager.getInstance();
        List<DatasetMetadata> datasets = client.listDatasets();
        Dataset dataset = null;
        boolean found = false;

        for (DatasetMetadata ds : datasets) {
            if (ds.getDatasetName().equals(datasetName)) {
                found = true;
                break;
            }
        }
        if (found) {
            dataset = client.openOrCreateDataset(datasetName);
        } else {
            Log.i(TAG, "dataset [" + dataset + "] not found.");
        }

        return dataset;
    }
}
