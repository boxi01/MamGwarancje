package com.example.gwarancja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ReceiptDetails extends AppCompatActivity {

    private StorageReference storage;
    private TextView detailName;
    private TextView detailDate;
    private TextView detailYear;
    private TextView detailDays;
    private  ImageView imageView;

    public static final String EXTRA_PRODUCT =
            "com.example.gwarancja.EXTRA_PRODUCT";
    public static final String EXTRA_DATE =
            "com.example.gwarancja.EXTRA_DATE";
    public static final String EXTRA_YEARS =
            "com.example.gwarancja.EXTRA_YEARS";
    public static final String EXTRA_IMAGE =
            "com.example.gwarancja.EXTRA_IMAGE";
    public static final String EXTRA_DAYS =
            "com.example.gwarancja.EXTRA_DAYS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_details);

        detailName = findViewById(R.id.viewDetailName);
        detailDate = findViewById(R.id.viewDetailDate);
        detailYear = findViewById(R.id.viewDetailYears);
        imageView = findViewById(R.id.imageView2);

        // Reference to an image file in Cloud Storage
       // StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("uploads/1576714601205.jpg");
       // StorageReference httpsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/gwarancja-66eff.appspot.com/o/uploads%2F1576716403478.jpg");
// ImageView in your Activity


        String url = "https://firebasestorage.googleapis.com/v0/b/gwarancja-66eff.appspot.com/o/nullZOFTINO_201912311731491616078653.jpg?alt=media&token=b7142d19-ad52-4812-bb26-2845f65a9a2f";

        Intent intent = getIntent();
        String product = intent.getExtras().getString(ReceiptDetails.EXTRA_PRODUCT);
        String date = intent.getExtras().getString(ReceiptDetails.EXTRA_DATE);
        Integer years = intent.getIntExtra(ReceiptDetails.EXTRA_YEARS, 0);

        String image = intent.getExtras().getString(ReceiptDetails.EXTRA_IMAGE);
        detailName.setText(product);
        detailDate.setText(date);
        detailYear.setText(String.valueOf(years));



        Glide.with(getApplicationContext()).load(image).into(imageView);
//
//// Download directly from StorageReference using Glide
//// (See MyAppGlideModule for Loader registration)
//        Glide.with(this /* context */)
//                .load(httpsReference)
//                .into(imageView);
}

}
