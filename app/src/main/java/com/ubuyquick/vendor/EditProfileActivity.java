package com.ubuyquick.vendor;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private int GALLERY = 1, CAMERA = 2, IMAGE_UPLOAD;
    private boolean verified;
    private UploadImage uploadImage;

    private String vendor_name, mobile_number, email, pan_card_number, aadhar_card_number;

    private String terms = "By registering, you agree to the <a><u><font color='black'>Terms &amp; Conditions</font></u></a> of " +
            "using this account and to the <a><u><font color='black'>Privacy Policy</font></u></a> of <a><u><font color='#03A9F4'>UBuyQuick.com</font></u></a>";

    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    private static Pattern pattern;
    private Matcher matcher;

    private TextView tv_terms, tv_upload;

    private Button btn_upload_vendor, btn_save, btn_cancel;
    private TextInputEditText et_vendor_name, et_mobile_number, et_email, et_pan_number, et_aadhar_number;
    private TextInputLayout til_vendor_name, til_mobile_number, til_email, til_pan_number, til_aadhar_number;
    private ImageView img_pan, img_aadhar;
    private CircleImageView img_vendor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeViews();
        initialize();
    }

    private void initializeViews() {
        tv_terms = (TextView) findViewById(R.id.tv_terms);
        tv_upload = (TextView) findViewById(R.id.tv_upload);

        img_aadhar = (ImageView) findViewById(R.id.img_aadhar);
        img_pan = (ImageView) findViewById(R.id.img_pan);
        img_vendor = (CircleImageView) findViewById(R.id.img_vendor);

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
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
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        ImageLoader.getInstance().init(new UniversalImageLoader(this).getConfig());

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> vendor = document.getData();
                                UniversalImageLoader.setImage(vendor.get("photo_url").toString(), img_vendor);

                                if (!vendor.get("aadhar_image_url").toString().equals("NA"))
                                    UniversalImageLoader.setImage(vendor.get("aadhar_image_url").toString(), img_aadhar);
                                if (!vendor.get("pan_image_url").toString().equals("NA"))
                                    UniversalImageLoader.setImage(vendor.get("pan_image_url").toString(), img_pan);

                                verified = (boolean) vendor.get("verified");
                                if (verified) {
                                    et_pan_number.setEnabled(false);
                                    et_aadhar_number.setEnabled(false);
                                    et_email.setEnabled(false);
                                    et_mobile_number.setEnabled(false);
                                    img_aadhar.setOnClickListener(null);
                                    img_pan.setOnClickListener(null);
                                }

                                et_email.setText(vendor.get("email").toString());
                                et_vendor_name.setText(vendor.get("name").toString());
                                et_mobile_number.setText(vendor.get("phone").toString());
                                et_pan_number.setText(vendor.get("pan_number").toString());
                                et_aadhar_number.setText(vendor.get("aadhar_number").toString().replace(" ", ""));
                            }
                        }
                    }
                });

        tv_terms.setText(Html.fromHtml(terms));

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                til_vendor_name.setErrorEnabled(false);
                til_mobile_number.setErrorEnabled(false);
                til_pan_number.setErrorEnabled(false);
                til_aadhar_number.setErrorEnabled(false);
                til_email.setErrorEnabled(false);

                if (validateForm()) {
                    storageRef = storage.getReference().child(mobile_number);
                    img_vendor.setDrawingCacheEnabled(true);
                    img_pan.setDrawingCacheEnabled(true);
                    img_aadhar.setDrawingCacheEnabled(true);
                    Bitmap vendor = img_vendor.getDrawingCache();
                    Bitmap pan = img_pan.getDrawingCache();
                    Bitmap aadhar = img_aadhar.getDrawingCache();
                    ByteArrayOutputStream os_vendor = new ByteArrayOutputStream();
                    ByteArrayOutputStream os_pan = new ByteArrayOutputStream();
                    ByteArrayOutputStream os_aadhar = new ByteArrayOutputStream();
                    vendor.compress(Bitmap.CompressFormat.JPEG, 100, os_vendor);
                    pan.compress(Bitmap.CompressFormat.JPEG, 100, os_pan);
                    aadhar.compress(Bitmap.CompressFormat.JPEG, 100, os_aadhar);
                    byte[] dataVendor = os_vendor.toByteArray();
                    byte[] dataPan = os_pan.toByteArray();
                    byte[] dataAadhar = os_aadhar.toByteArray();

                    UploadTask uploadTask = storageRef.child("vendor_image.jpg").putBytes(dataVendor);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                    .update("photo_url", downloadUrl);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                    UploadTask uploadTask2 = storageRef.child("aadhar_image.jpg").putBytes(dataAadhar);
                    uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                    .update("aadhar_image_url", downloadUrl);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                    UploadTask uploadTask3 = storageRef.child("pan_image.jpg").putBytes(dataPan);
                    uploadTask3.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                    .update("pan_image_url", downloadUrl);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                    Map<String, Object> vendorInfo = new HashMap<>();
                    vendorInfo.put("name", vendor_name);
                    vendorInfo.put("phone", mobile_number);
                    vendorInfo.put("pan_number", pan_card_number);
                    vendorInfo.put("aadhar_number", aadhar_card_number);
                    vendorInfo.put("email", email);

                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                            .update(vendorInfo)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(EditProfileActivity.this, "Updated profile", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                } else {

                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
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
            if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                    Toast.makeText(EditProfileActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    if (uploadImage == UploadImage.AADHAR)
                        img_aadhar.setImageBitmap(bitmap);
                    else if (uploadImage == UploadImage.PAN)
                        img_pan.setImageBitmap(bitmap);
                    else
                        img_vendor.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EditProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(EditProfileActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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
