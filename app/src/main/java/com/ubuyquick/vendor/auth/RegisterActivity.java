package com.ubuyquick.vendor.auth;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ubuyquick.vendor.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private int GALLERY = 1, CAMERA = 2, IMAGE_UPLOAD;
    private UploadImage uploadImage;

    private String vendor_name, mobile_number, email, pan_card_number, aadhar_card_number;

    private String terms = "By registering, you agree to the <a><u><font color='black'>Terms &amp; Conditions</font></u></a> of " +
            "using this account and to the <a><u><font color='black'>Privacy Policy</font></u></a> of <a><u><font color='#03A9F4'>UBuyQuick.com</font></u></a>";

    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    private static Pattern pattern;
    private Matcher matcher;

    private TextView tv_terms;

    private Button btn_upload_vendor, btn_register, btn_login;
    private TextInputEditText et_vendor_name, et_mobile_number, et_email, et_pan_number, et_aadhar_number;
    private TextInputLayout til_vendor_name, til_mobile_number, til_email, til_pan_number, til_aadhar_number;
    private ImageView img_pan, img_aadhar;
    private CircleImageView img_vendor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        initialize();
    }

    private void initializeViews() {
        tv_terms = (TextView) findViewById(R.id.tv_terms);

        img_aadhar = (ImageView) findViewById(R.id.img_aadhar);
        img_pan = (ImageView) findViewById(R.id.img_pan);
        img_vendor = (CircleImageView) findViewById(R.id.img_vendor);

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_upload_vendor = (Button) findViewById(R.id.btn_upload);

        et_vendor_name = (TextInputEditText) findViewById(R.id.et_vendor_name);
        et_mobile_number = (TextInputEditText) findViewById(R.id.et_mobile_number);
        et_email = (TextInputEditText) findViewById(R.id.et_email);
        et_pan_number = (TextInputEditText) findViewById(R.id.et_pan_number);
        et_aadhar_number = (TextInputEditText) findViewById(R.id.et_aadhar_number);

        til_vendor_name = (TextInputLayout) findViewById(R.id.til_vendor_name);
        til_mobile_number = (TextInputLayout) findViewById(R.id.til_mobile_number);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        til_pan_number = (TextInputLayout) findViewById(R.id.til_pan_number);
        til_aadhar_number = (TextInputLayout) findViewById(R.id.til_aadhar_number);
    }

    private void initialize() {
        tv_terms.setText(Html.fromHtml(terms));

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                til_vendor_name.setErrorEnabled(false);
                til_mobile_number.setErrorEnabled(false);
                til_pan_number.setErrorEnabled(false);
                til_aadhar_number.setErrorEnabled(false);
                til_email.setErrorEnabled(false);

                if (validateForm()) {

                } else {

                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        img_aadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage = UploadImage.AADHAR;
                showChooserDialog();
            }
        });

        img_pan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage = UploadImage.PAN;
                showChooserDialog();
            }
        });

        btn_upload_vendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage = UploadImage.VENDOR;
                showChooserDialog();
            }
        });
    }

    private boolean validateForm() {
        vendor_name = et_vendor_name.getText().toString();
        email = et_email.getText().toString();
        mobile_number = et_mobile_number.getText().toString();
        pan_card_number = et_pan_number.getText().toString();
        aadhar_card_number = et_aadhar_number.getText().toString();

        if (TextUtils.isEmpty(vendor_name)) {
            til_vendor_name.setError("Vendor name cannot empty.");
            et_vendor_name.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(mobile_number)) {
            til_mobile_number.setError("Mobile number required.");
            et_mobile_number.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(email)) {
            til_email.setError("Email address required.");
            et_email.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(pan_card_number)) {
            til_pan_number.setError("PAN number required.");
            et_pan_number.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(aadhar_card_number)) {
            til_aadhar_number.setError("Aadhar number required.");
            et_aadhar_number.requestFocus();
            return false;
        }

        if (mobile_number.length() != 10) {
            til_mobile_number.setError("Mobile number should have 10 digits.");
            return false;
        }

        pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            til_email.setError("Invalid email address format.");
            return false;
        }

        if (aadhar_card_number.length() != 12) {
            til_aadhar_number.setError("Invalid Aadhar number. Please enter 12 digits.");
            return false;
        }

        return true;
    }

    private void showChooserDialog() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            } else {
                chooseImage();
            }
        }
    }

    private void chooseImage() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                } else
                    chooseImage();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                    String path = saveImage(bitmap);
                    Toast.makeText(RegisterActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    if (uploadImage == UploadImage.AADHAR)
                        img_aadhar.setImageBitmap(bitmap);
                    else if (uploadImage == UploadImage.PAN)
                        img_pan.setImageBitmap(bitmap);
                    else
                        img_vendor.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            Log.d(TAG, "onActivityResult: " + uploadImage);
            if (uploadImage == UploadImage.AADHAR)
                img_aadhar.setImageBitmap(thumbnail);
            else if (uploadImage == UploadImage.PAN)
                img_pan.setImageBitmap(thumbnail);
            else
                img_vendor.setImageBitmap(thumbnail);
//            saveImage(thumbnail);
            Toast.makeText(RegisterActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onBackPressed();
    }

    enum UploadImage {
        VENDOR, AADHAR, PAN
    }
}
