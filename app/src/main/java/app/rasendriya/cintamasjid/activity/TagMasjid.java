package app.rasendriya.cintamasjid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.service.AndroidMultiPartEntity;
import app.rasendriya.cintamasjid.service.Config;
import app.rasendriya.cintamasjid.service.ScalingUtilities;


@SuppressWarnings("ALL")
public class TagMasjid extends ActionBarActivity implements Validator.ValidationListener {

    private Button btnTag;
    private Button btnSimpan;

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int RESULT_LOAD_IMAGE = 1;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "CintaMasjid";

    private Uri fileUri; // file url to store image/video

//    @NotEmpty(messageId = R.string.alamat_empty)
    @NotEmpty
    EditText textLokasi;
    ImageView preview;

    AlertDialog.Builder builder;
    AlertDialog alert;

    LatLng latLng;

//    @NotEmpty(messageId = R.string.masjid_empty)
    @NotEmpty
    TextView namaMasjid;

    Activity self;

    int reqCode;
    public Button getBtnTag() {
        return btnTag;
    }

    public void setBtnTag(Button btnTag) {
        this.btnTag = btnTag;
    }


    boolean isUpload = false;
    double lat, lon;
    Bundle extra;

    String selectedImagePath;

    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_masjid);

        validator = new Validator(this);
        validator.setValidationListener(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f15d28")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Tag Masjid");

        builder =  new AlertDialog.Builder(this);
        alert = builder.create();

        self = this;

        preview = (ImageView) findViewById(R.id.preview);

        extra = getIntent().getExtras();
        textLokasi = (EditText) findViewById(R.id.textLokasi);
        textLokasi.setText(extra.get("alamat").toString());

        latLng = (LatLng) extra.get("latlng");



//        btnTag = (Button) findViewById(R.id.btnFoto);
//        btnTag.setVisibility(0);
//        btnTag.setOnClickListener(this);

        namaMasjid = (EditText) findViewById(R.id.namaMasjid);

        btnSimpan = (Button) findViewById(R.id.btnSimpan);


        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lat = extra.getDouble("lat");
                lon = extra.getDouble("lon");

                validator.validate();

//                FormValidator.validate(self, new SimpleErrorPopupCallback(self));


//                if(isUpload){
////                    upload();
//
//                }else{
//                    Toast.makeText(self, "Upload ga jadi", Toast.LENGTH_LONG).show();
//                }
//                final ProgressDialog progressDialog = new ProgressDialog(self);
//                progressDialog.setMessage("Please wait");
//                progressDialog.show();
//
//                RequestParams requestParams = new RequestParams();
//                requestParams.put("nama", namaMasjid.getText());
//                requestParams.put("lat", extra.getDouble("lat"));
//                requestParams.put("lon", extra.getDouble("lon"));
//                requestParams.put("alamat", extra.getString("alamat"));
////                try {
////                    requestParams.put("fileToUpload", new File(fileUri.getPath()));
////                } catch (FileNotFoundException e) {
////                    e.printStackTrace();
////                }
//
//                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
//                System.out.println("param");
//                System.out.println(requestParams.toString());
//                asyncHttpClient.post(Config.TAG_MASJID, requestParams, new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                        progressDialog.dismiss();
////                        new FetchDataMasjid(self).execute();
//                        Toast.makeText(getApplicationContext(), "tag sukses", Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(self, MasjidActivity.class);
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                        progressDialog.dismiss();
//                        System.out.println(error.toString());
//                        Toast.makeText(getApplicationContext(), "tag gagal", Toast.LENGTH_LONG).show();
//
//                    }
//                });
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tag_masjid, menu);
        return true;
    }

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Ambil photo dari")
                .setCancelable(false)
                .setPositiveButton("Galeri", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                RESULT_LOAD_IMAGE);

                    }
                })
                .setNegativeButton("Kamera", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                        // start the image capture Intent
                        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }



    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        reqCode = requestCode;
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE ) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                launchUploadActivity(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        }else if(requestCode == RESULT_LOAD_IMAGE){
//            launchUploadActivity(true);
            fileUri = data.getData();
            String[] projection = { MediaStore.MediaColumns.DATA };
            Cursor cursor = managedQuery(fileUri, projection, null, null,
                    null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();

            selectedImagePath = cursor.getString(column_index);

            System.out.println("PATH : " + selectedImagePath);
            Bitmap bm;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(selectedImagePath, options);
            final int REQUIRED_SIZE = 200;
            int scale = 1;
            while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                    && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(selectedImagePath, options);

            preview.setImageBitmap(bm);

        }
    }

    private void launchUploadActivity(boolean isImage){
//        Intent i = new Intent(MainActivity.this, UploadActivity.class);
//        i.putExtra("filePath", fileUri.getPath());
//        i.putExtra("isImage", isImage);
//        startActivity(i);

        previewMedia(isImage);

    }

    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            preview.setVisibility(View.VISIBLE);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 2;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

            preview.setImageBitmap(bitmap);
        }
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        System.out.println("IMAGE DIRECTORY : " + mediaStorageDir.getPath());
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("UPLOAD", "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".png");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MapTag.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.action_photo:
                captureImage();
                isUpload = true;
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    float totalSize;
    ProgressDialog progressDialog;

    @Override
    public void onValidationSucceeded() {
        if(isUpload){

            new UploadFileToServer().execute();
        }else{
//                    validator.validate();
            Toast.makeText(self, "Sertakan Foto saat Tag Masjid", Toast.LENGTH_LONG).show();
        }

//        Toast.makeText(this, "Yay! we got it right!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override


        protected void onPreExecute() {
            // setting progress bar to zero
            progressDialog = new ProgressDialog(self);
            progressDialog.setMessage("Please wait");
            progressDialog.show();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
//            progressBar.setVisibility(View.VISIBLE);
            progressDialog.setMessage(String.valueOf(progress[0]) + "%");
//
//            // updating progress bar value
//            progressBar.setProgress(progress[0]);
//
//            // updating percentage value
//            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
//                Bitmap b= BitmapFactory.decodeFile(fileUri.getPath());
//                Bitmap out = Bitmap.createScaledBitmap(b, 320, 480, false);
//                File sourceFile = new File(fileUri.getPath());
//                File sourceFile = new File(decodeFile((reqCode == 1) ? selectedImagePath : fileUri.getPath(), 300, 300));
                File sourceFile;
                if(reqCode == 1){
                    System.out.println("BACA REQCODE 1 : " + selectedImagePath);
                    sourceFile = new File(decodeFile(selectedImagePath, 300, 300));
                }else{
                    System.out.println("BACA REQCODE 100 : " + fileUri.getPath());
                    sourceFile = new File(decodeFile(fileUri.getPath(), 300, 300));
                }

                System.out.println("REQUEST CODE : " + reqCode + "ABSOLUTE PATH : " + ((reqCode == 1) ? selectedImagePath : fileUri.getPath()) + " IMAGE PATH : " + selectedImagePath );
//                FileOutputStream fileOutputStream;
//                try{
//                    fileOutputStream = new FileOutputStream(sourceFile);
//                    out.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
//                    fileOutputStream.flush();
//                    fileOutputStream.close();
//                    b.recycle();
//                    out.recycle();
//                }catch (Exception e){
//                    System.out.println("Ada kesalahan : " + e.getMessage());
//                }
                System.out.println("source path : " + fileUri.getPath());
                // Adding file data to http body

                entity.addPart("fileToUpload",  new org.apache.http.entity.mime.content.FileBody(sourceFile));
//
                // Extra parameters if you want to pass to server
                entity.addPart("nama",
                        new org.apache.http.entity.mime.content.StringBody(namaMasjid.getText().toString()));
                entity.addPart("latitude",  new org.apache.http.entity.mime.content.StringBody(String.valueOf(lat)));
                entity.addPart("longitude",  new org.apache.http.entity.mime.content.StringBody(String.valueOf(lon)));
                entity.addPart("alamat",  new org.apache.http.entity.mime.content.StringBody(textLokasi.getText().toString()));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);

                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }



            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("UPLOAD", "Response from server: " + result);
            progressDialog.dismiss();
            // showing the server response in an alert dialog
            showAlert(result);

            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                        Intent intent = new Intent(self, MasjidActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String decodeFile(String path,int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

//            String extr = Environment.getExternalStorageDirectory().toString();
//            File mFolder = new File(extr);
//            if (!mFolder.exists()) {
//                mFolder.mkdir();
//            }

            String s = "tmp.png";

            File f = new File((reqCode == 1) ? selectedImagePath : fileUri.getPath());

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }



}
