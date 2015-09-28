package com.mycompany.personalhealthmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

public class PhotoManager extends AppCompatActivity {
    private final String TAG = "PhotoManager";
    private TextView photoNameTextView;
    private Uri photoUri = null;
    private TransferModel[] mModels = new TransferModel[0];
    private ProgressDialog dialog;
    private EditText albumNameEditText;
    private static Spinner albumSpinner;
    private AmazonS3Client mClient;
    private ArrayAdapter<String> albumSpinnerAdapter;
    static final int REQUEST_TAKE_PHOTO = 2;
    private ImageView mImageView;
    private String mCurrentPhotoPath;
    private String mCurrentPhotoPath2;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_manager);

        photoNameTextView = (TextView) findViewById(R.id.photoNameTextView);
        albumNameEditText = (EditText) findViewById(R.id.albumNameEditText);
        mImageView = (ImageView) findViewById(R.id.mImageView);
        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
        }
        Constants.albumNameList = new ArrayList<>();
        mClient = Util.getS3Client(PhotoManager.this);
        new syncAlbumTask().execute();
    }

    private void setUpAlbumNameSpinner() {
        albumSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item,
                Constants.albumNameList);
        albumSpinner = (Spinner) findViewById(R.id.albumSpinner);
        albumSpinner.setAdapter(albumSpinnerAdapter);
        albumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Constants.currAlbumName = parent.getItemAtPosition(position).toString();
                Log.i("PHM", "Selected album name = " + Constants.currAlbumName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onChoosePhotoClick(View view) {
        Log.i("Photo manager", "on Choose Photo click");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a photo"), 1);
    }

    public void onUploadPhotoClick(View view) {
        if (Constants.currAlbumName == null) {
            Toast.makeText(this, "Please create a album and select one for uploading.", Toast.LENGTH_LONG).show();
            return;
        } else if (photoUri == null) {
            Toast.makeText(this, "Please choose a photo for uploading.", Toast.LENGTH_LONG).show();
            return;
        }

        dialog = ProgressDialog.show(PhotoManager.this, "Data Syncing...",
                "Please wait");
        Log.i("upload", "start to upload");
        TransferController.upload(this, photoUri);
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                PhotoManager.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        syncModels();
                        int waitDoneCount = mModels.length;
                        Log.i("Photomanager", "timer running. waitDone count = " + waitDoneCount);
                        for (int i = 0; i < mModels.length; i++) {
                            int progress = mModels[i].getProgress();
                            Log.i("Photomanager", "progress = " + progress);
                            if (progress == 100) {
                                waitDoneCount--;
                                mModels[i].removeTaskById(mModels[i].getId());
                            }
                        }
                        if (waitDoneCount == 0) {
                            timer.cancel();
                            timer.purge();
                            dialog.dismiss();
                            photoUri = null;
                        }
                    }
                });

            }
        };
        timer.schedule(timerTask, 1500, Constants.DATA_REFRESH_DELAY);
    }

    public void autoUploadPhoto() {
        if (photoUri == null) {
            Toast.makeText(this, "Please choose a photo for uploading.", Toast.LENGTH_LONG).show();
            return;
        }

        Log.i("upload", "start to upload");
        TransferController.upload(this, photoUri);
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                PhotoManager.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        syncModels();
                        int waitDoneCount = mModels.length;
                        Log.i("Photomanager", "timer running. waitDone count = " + waitDoneCount);
                        for (int i = 0; i < mModels.length; i++) {
                            int progress = mModels[i].getProgress();
                            Log.i("Photomanager", "progress = " + progress);
                            if (progress == 100) {
                                waitDoneCount--;
                                mModels[i].removeTaskById(mModels[i].getId());
                            }
                        }
                        if (waitDoneCount == 0) {
                            timer.cancel();
                            timer.purge();
                            dialog.dismiss();
                            photoUri = null;
                        }
                    }
                });

            }
        };
        timer.schedule(timerTask, 1500, Constants.DATA_REFRESH_DELAY);
    }

    /* makes sure that we are up to date on the transfers */
    private void syncModels() {
        TransferModel[] models = TransferModel.getAllTransfers();
        if (mModels.length != models.length) {
            mModels = models;
        }
    }

    public void onCreateAlbumClick(View view) {
        String albumName = albumNameEditText.getText().toString();

        if (albumName.equals("")) {
            Toast.makeText(this, "Album name cannot be empty! Please try again.", Toast.LENGTH_LONG).show();
            return;
        } else if (Constants.albumNameList.contains(albumName)) {
            Toast.makeText(this, "Album name [" + albumName + "] name already existed! Please try again.", Toast.LENGTH_LONG).show();
            return;
        }

        Constants.albumNameList.add(albumName);
        Log.i("PHM", "album name = " + albumName);
        setUpAlbumNameSpinner();

        albumNameEditText.setText("");
    }

    public void onCreateAlbumResetClick(View view) {
        albumNameEditText.setText("");
    }

    public void onListPhotoClick(View view) {
        Intent intent = new Intent(
                PhotoManager.this, DownloadActivity.class);
        startActivity(intent);
    }

    public void onTakePhotoClick(View view) {
        if (Constants.currAlbumName == null) {
            Toast.makeText(this, "Please create a album and select one for auto uploading.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Fail to create file.", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == RESULT_OK) {
            if (reqCode == 1) {
                photoUri = data.getData();
                String mExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(
                        this.getContentResolver().getType(photoUri));
                Log.i("photo magager", data.getDataString());
                photoNameTextView.setText("Photo Name:    " + Util.getFileName(photoUri.toString()) + "." + mExtension);
            } else if (reqCode == REQUEST_TAKE_PHOTO) {
                galleryAddPic();
                setPic();
                if (Constants.photoAutoUpload) {
                    dialog = ProgressDialog.show(PhotoManager.this, "Data Syncing...",
                            "Please wait");
                    Log.e(TAG, "Auto upload feature is enabled.");
                    File f = new File(mCurrentPhotoPath2);
                    Log.e("photo", "contentUri for auto upload = " + Uri.fromFile(f));
                    photoUri = Uri.fromFile(f);
                    final Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        public void run() {
                            autoUploadPhoto();
                            t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                        }
                    }, 5000); // start auto upload after 5 sec.
                } else {
                    Log.e(TAG, "Auto upload feature is disable.");
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath2 = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        Log.e("photo", "contentUri = " + contentUri);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath2, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        Log.e(TAG, "current path = " + mCurrentPhotoPath2);
        Log.e(TAG, "tW = " + targetW + ", tH = " + targetW);
        Log.e(TAG, "W = " + photoW + ", H = " + photoH);

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        Log.e(TAG, "scale factor = " + scaleFactor);
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath2, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    private class syncAlbumTask extends AsyncTask<Void, Void, List<S3ObjectSummary>> {
        @Override
        protected List<S3ObjectSummary> doInBackground(Void... params) {
            // get all the objects in bucket
            return mClient.listObjects(Constants.PHOTO_MANAGER_BUCKET_NAME.toLowerCase(Locale.US),
                    Constants.currUserName + "/").getObjectSummaries();
        }

        @Override
        protected void onPostExecute(List<S3ObjectSummary> objects) {
            for(S3ObjectSummary o : objects) {
                String path = o.getKey();
                String key = path.substring(path.indexOf("/") + 1, path.lastIndexOf("/"));
                if (!Constants.albumNameList.contains(key)) {
                    Constants.albumNameList.add(key);
                }
            }
            setUpAlbumNameSpinner();
        }
    }
}
