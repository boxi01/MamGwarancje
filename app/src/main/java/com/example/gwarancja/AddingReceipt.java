package com.example.gwarancja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.util.LocaleData;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddingReceipt extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextProduct;
    private EditText editTextYears;

    //public static final int REQUEST_IMAGE = 1;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;
    private ImageView imgReceipt;
    private Button btnTakePh;
    private Button btnUpload;
    private ProgressBar progressBar;
    private Uri mImageUri;
    private String url;
    private String pictureFilePath;
    private String deviceIdentifier;
    private TextView viewTextDate;
    private String finalUrl;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath = "";
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_receipt);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Dodaj paragon");

        editTextProduct = findViewById(R.id.editTextName);

        editTextYears = findViewById(R.id.editTextYears);
        viewTextDate = findViewById(R.id.textViewDate);

        btnTakePh = findViewById(R.id.button_take_photo);
        imgReceipt = (ImageView) findViewById(R.id.imageReceipt);


        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        btnTakePh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //openFileChooser();
                dispatchTakePictureIntent();
            }
        });



    }

    private void addToCloudStorage() throws ParseException {


        File f = new File(pictureFilePath);
        Uri picUri = Uri.fromFile(f);
        final String cloudFilePath = deviceIdentifier + picUri.getLastPathSegment();

        Log.d("URL", cloudFilePath);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();
        final StorageReference uploadeRef = storageRef.child(cloudFilePath);

        uploadeRef.putFile(picUri).addOnFailureListener(new OnFailureListener(){
            public void onFailure(@NonNull Exception exception){
                Log.d("TAG","Failed to upload picture to cloud storage");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                Toast.makeText(AddingReceipt.this,
                        "Image has been uploaded to cloud storage",
                        Toast.LENGTH_SHORT).show();
                uploadeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                     @Override
                                                                     public void onSuccess(Uri uri) {
                                                                         final Uri downloadUrl = uri;
                                                                         finalUrl = downloadUrl.toString();
                                                                         Log.d("url", finalUrl);
                                                                     }
                                                                 });

                String product = editTextProduct.getText().toString();
                String mUrl = pictureFilePath;
                String date = viewTextDate.getText().toString();
                int years = Integer.parseInt(editTextYears.getText().toString());


                CollectionReference receiptsRef = FirebaseFirestore.getInstance()
                        .collection("Receipts2");
                try {
                    receiptsRef.add(new Receipt(product, mUrl, date, years, countDate(date, years)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                Toast.makeText(AddingReceipt.this, "Dodano", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    protected synchronized String getInstallationIdentifier() {
        if (deviceIdentifier == null) {
            SharedPreferences sharedPrefs = this.getSharedPreferences(
                    "DEVICE_ID", Context.MODE_PRIVATE);
            deviceIdentifier = sharedPrefs.getString("DEVICE_ID", null);
            if (deviceIdentifier == null) {
                deviceIdentifier = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("DEVICE_ID", deviceIdentifier);
                editor.commit();
            }
        }
        return deviceIdentifier;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
       // startActivityForResult(intent, REQUEST_IMAGE);
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_receipt_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                try {

                    addToCloudStorage();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            case R.drawable.ic_close:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* TAKEING IMAGE */

    private void dispatchTakePictureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //cameraIntent.putExtra( MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
           // startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

            File pictureFile = null;
            try {
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.gwarancja.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "ZOFTINO_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri resultUri;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new File(pictureFilePath);
            mImageUri = Uri.fromFile(imgFile);
            Log.d("uri", mImageUri.toString());
            CropImage.activity(mImageUri)
                    .start(this);
        }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                resultUri = result.getUri();
                Log.d("uri", "jestem tu");
                Log.d("uri", resultUri.toString());

                if (resultUri != null) {
                    imgReceipt.setImageURI(resultUri);
                    imgReceipt.buildDrawingCache();
                    bitmap=imgReceipt.getDrawingCache();
                    detectText();

                }
            }
    }


    public void detectText(){

        if(bitmap == null){
            Log.d("array", "no image");
        } else {
            Log.d("array", "have image");
            FirebaseVisionImage myimage = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            //Task<FirebaseVisionText> result =
                    detector.processImage(myimage)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                    Log.d("array", "process text");
                                    processText(firebaseVisionText);
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.d("array", "faliure");
                                        }
                                    });
        }

    }

    private void processText(FirebaseVisionText firebaseVisionText){

        String[] textArray;
        List<FirebaseVisionText.TextBlock> myblock = firebaseVisionText.getTextBlocks();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
        final List<String> dateFormats = Arrays.asList("yyyy-MM-dd", "dd-MM-yyyy");

        List<SimpleDateFormat> knownPatterns = new ArrayList<SimpleDateFormat>();
        knownPatterns.add(new SimpleDateFormat("yyyy-MM-dd"));
        knownPatterns.add(new SimpleDateFormat("dd-MM-yyyy"));
       // knownPatterns.add(new SimpleDateFormat("dd/MM/yyyy"));
        //knownPatterns.add(new SimpleDateFormat("yyyy/MM/dd"));

        for(FirebaseVisionText.TextBlock block:firebaseVisionText.getTextBlocks()){
            String text = block.getText();
            for(FirebaseVisionText.Line line: block.getLines()){
                String lineText = line.getText();
                for(FirebaseVisionText.Element element: line.getElements()){
                    String elementText = element.getText();
                    Log.d("array", elementText);
                    if (elementText.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")){
                        viewTextDate.setText(elementText);
                    } else if (elementText.matches("([0-9]{2})-([0-9]{2})-([0-9]{4})")){
                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                        Date date = null;
                        try {
                            date = (Date)df.parse(elementText);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                        elementText = newFormat.format(date);
                        viewTextDate.setText(elementText);
                    } else if (elementText.matches("([0-9]{4})/([0-9]{2})/([0-9]{2})")) {
                        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                        Date date = null;
                        try {
                            date = (Date) df.parse(elementText);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                        elementText = newFormat.format(date);
                        viewTextDate.setText(elementText);
                    } else if (elementText.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = null;
                        try {
                            date = (Date) df.parse(elementText);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                        elementText = newFormat.format(date);
                        viewTextDate.setText(elementText);
                    }

                }
            }

        }
    }

    public String countDate(final String date, Integer years) throws ParseException {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date mDate = dateFormat.parse(date);
        String current = dateFormat.format(mDate);
        Calendar cal = Calendar.getInstance();
        final Calendar cal1 = Calendar.getInstance();
        cal.setTime(mDate);
        cal.add(Calendar.YEAR, years);
        Date eDate = cal.getTime();
        String endDate = dateFormat.format(eDate);

        return endDate;
    }

    @Override
    public void onClick(View v) {
        editTextProduct.getText().clear();
        editTextYears.getText().clear();
    }
}