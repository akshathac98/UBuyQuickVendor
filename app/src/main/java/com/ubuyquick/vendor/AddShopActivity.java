package com.ubuyquick.vendor;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class AddShopActivity extends AppCompatActivity {

    private static final String TAG = "AddShopActivity";

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore db;

    private Button btn_from, btn_to, btn_location, btn_add_shop;
    private TextView tv_location, tv_delivery_radius, tv_upload;
    private CheckBox cb_groceries, cb_provisions, cb_livestock, cb_vegetables;
    private ImageView img_shop;
    private EditText et_shop_name, et_address, et_pincode;
    private TextInputLayout til_shop_name, til_address, til_pincode;
    private FloatingActionButton btn_upload;

    private int GALLERY = 2, CAMERA = 3;

    private String timings_from = null, timings_to = null, mobile_number;

    private double lat = 0.0D, lng = 0.0D, radius = 0.0D;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViews();
        initialize();
    }

    private void initialize() {

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mobile_number = mAuth.getCurrentUser().getPhoneNumber().substring(3);

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(AddShopActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddShopActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                    } else {
                        startActivityForResult(new Intent(AddShopActivity.this, ShopLocationActivity.class), 1);
                    }
                }
            }
        });

        btn_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddShopActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if (hourOfDay < 12) {
                                    timings_from = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + " AM";
                                    btn_from.setText(timings_from);
                                } else if (hourOfDay > 12) {
                                    timings_from = String.format("%02d", hourOfDay % 12) + ":" + String.format("%02d", minute) + " PM";
                                    btn_from.setText(timings_from);
                                } else {
                                    timings_from = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + " PM";
                                    btn_from.setText(timings_from);
                                }
                            }
                        }, 0, 0, false);
                timePickerDialog.show();

            }
        });

        btn_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddShopActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if (hourOfDay < 12) {
                                    timings_to = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + " AM";
                                    btn_to.setText(timings_to);
                                } else if (hourOfDay > 12) {
                                    timings_to = String.format("%02d", hourOfDay % 12) + ":" + String.format("%02d", minute) + " PM";
                                    btn_to.setText(timings_to);
                                } else {
                                    timings_to = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + " PM";
                                    btn_to.setText(timings_to);
                                }
                            }
                        }, 0, 0, false);
                timePickerDialog.show();

            }
        });

        btn_add_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til_shop_name.setErrorEnabled(false);
                til_address.setErrorEnabled(false);
                til_pincode.setErrorEnabled(false);
                if (tv_upload.getVisibility() == View.VISIBLE) {
                    Toast.makeText(AddShopActivity.this, "Upload shop image", Toast.LENGTH_SHORT).show();
                    tv_upload.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(et_shop_name.getText())) {
                    til_shop_name.setError("Please enter the shop name.");
                    et_shop_name.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(et_address.getText())) {
                    til_address.setError("Enter shop address.");
                    et_address.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(et_pincode.getText())) {
                    til_pincode.setError("Pincode can't be empty.");
                    et_pincode.requestFocus();
                    return;
                } else if (timings_from == null || timings_to == null) {
                    Toast.makeText(AddShopActivity.this, "Please set the shop timings", Toast.LENGTH_SHORT).show();
                    return;
                } else if (lat == 0.0D || lng == 0.0D || radius == 0.0D) {
                    Toast.makeText(AddShopActivity.this, "Set your shop location to continue", Toast.LENGTH_SHORT).show();
                    return;
                }

                StorageReference storageReference = storage.getReference().child(mobile_number).child(et_shop_name.getText().toString())
                        .child("shop_image.jpg");

                img_shop.setDrawingCacheEnabled(true);
                img_shop.buildDrawingCache();
                Bitmap bitmap = img_shop.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = storageReference.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String url = taskSnapshot.getDownloadUrl().toString();
                        Map<String, Object> shop = new HashMap<>();
                        shop.put("shop_name", et_shop_name.getText().toString());
                        shop.put("shop_address", et_address.getText().toString());
                        shop.put("shop_pincode", et_pincode.getText().toString());
                        shop.put("shop_timings", btn_from.getText() + " to " + btn_to.getText());
                        shop.put("shop_location", lat + "," + lng);
                        shop.put("delivery_radius", radius);
                        shop.put("shop_image_url", url.substring(0, url.indexOf(".jpg")+4));

                        db.collection("vendors").document(mobile_number).collection("shops").add(shop)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(AddShopActivity.this, "Shop added successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddShopActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddShopActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooserDialog();
            }
        });
    }


    private void showChooserDialog() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(AddShopActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location required to set radius", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(AddShopActivity.this, ShopLocationActivity.class));
                }
                break;

            case 102:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                } else
                    chooseImage();
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initializeViews() {
        til_shop_name = (TextInputLayout) findViewById(R.id.textInputLayout4);
        til_address = (TextInputLayout) findViewById(R.id.textInputLayout5);
        til_pincode = (TextInputLayout) findViewById(R.id.textInputLayout7);

        tv_upload = (TextView) findViewById(R.id.tv_upload);
        btn_upload = (FloatingActionButton) findViewById(R.id.btn_upload);
        tv_delivery_radius = (TextView) findViewById(R.id.tv_delivery_radius);
        tv_location = (TextView) findViewById(R.id.tv_location);
        btn_from = (Button) findViewById(R.id.btn_from);
        btn_to = (Button) findViewById(R.id.btn_to);
        btn_location = (Button) findViewById(R.id.btn_location);
        btn_add_shop = (Button) findViewById(R.id.btn_add_shop);

        img_shop = (ImageView) findViewById(R.id.img_shop);
        et_shop_name = (EditText) findViewById(R.id.et_shop_name);
        et_address = (EditText) findViewById(R.id.et_shop_address);
        et_pincode = (EditText) findViewById(R.id.et_shop_pincode);

        cb_groceries = (CheckBox) findViewById(R.id.checkBox2);
        cb_vegetables = (CheckBox) findViewById(R.id.checkBox3);
        cb_livestock = (CheckBox) findViewById(R.id.checkBox4);
        cb_provisions = (CheckBox) findViewById(R.id.checkBox5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                double[] result = data.getDoubleArrayExtra("result");
                lat = result[0];
                lng = result[1];
                radius = data.getDoubleExtra("radius", 0);
                tv_delivery_radius.setText("Delivery Radius: " + radius + " Mtrs.");
                tv_location.setText("Location: " + String.format("%.4f", lat) + ", " + String.format("%.4f", lng));
            }
        } else if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                    String path = saveImage(bitmap);
                    Toast.makeText(AddShopActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    img_shop.setImageBitmap(bitmap);
                    tv_upload.setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddShopActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            img_shop.setImageBitmap(thumbnail);
            tv_upload.setVisibility(View.GONE);
//            saveImage(thumbnail);
            Toast.makeText(AddShopActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            super.onBackPressed();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        super.onBackPressed();
    }
}
