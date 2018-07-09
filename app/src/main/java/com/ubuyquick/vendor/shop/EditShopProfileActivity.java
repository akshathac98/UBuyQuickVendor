package com.ubuyquick.vendor.shop;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.ubuyquick.vendor.AddShopActivity;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.ShopActivity;
import com.ubuyquick.vendor.ShopLocationActivity;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditShopProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditShopProfileActivity";

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore db;

    private Button btn_from, btn_to, btn_location, btn_save_profile;
    private TextView tv_location, tv_delivery_radius, tv_upload;
    private ImageView img_shop;
    private EditText et_shop_name, et_address, et_pincode, et_gstin;
    private TextInputLayout til_shop_name, til_address, til_pincode, til_gstin;
    private FloatingActionButton btn_upload;

    private int GALLERY = 2, CAMERA = 3;

    private String timings_from = null, timings_to = null, mobile_number;

    private double lat = 0.0D, lng = 0.0D, radius = 0.0D;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shop_profile);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .collection("shops").document(getIntent().getStringExtra("shop_id"))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> shop = task.getResult().getData();
                UniversalImageLoader.setImage(shop.get("shop_image_url").toString(), img_shop);
                et_shop_name.setText(shop.get("shop_name").toString());
                et_address.setText(shop.get("shop_address").toString());
                et_pincode.setText(shop.get("shop_pincode").toString());
                et_gstin.setText(shop.get("shop_gstin").toString());
                timings_from = shop.get("shop_timings").toString().substring(0, 9);
                btn_from.setText(timings_from);
                timings_to = shop.get("shop_timings").toString().substring(12);
                btn_to.setText(timings_to);
                radius = Double.parseDouble(shop.get("delivery_radius").toString());
                tv_delivery_radius.setText("Delivery radius: " + radius + "mts.");
                lat = Double.parseDouble(shop.get("shop_location").toString().split(",")[0]);
                lng = Double.parseDouble(shop.get("shop_location").toString().split(",")[1]);
                tv_location.setText("Location: " + lat + ", " + lng);
                tv_upload.setVisibility(View.GONE);
            }
        });

        initializeViews();
        initialize();
    }

    private void initialize() {

        mobile_number = mAuth.getCurrentUser().getPhoneNumber().substring(3);

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(EditShopProfileActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EditShopProfileActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                    } else {
                        startActivityForResult(new Intent(EditShopProfileActivity.this, ShopLocationActivity.class), 1);
                    }
                }
            }
        });

        btn_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditShopProfileActivity.this,
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditShopProfileActivity.this,
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

        btn_save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til_shop_name.setErrorEnabled(false);
                til_address.setErrorEnabled(false);
                til_pincode.setErrorEnabled(false);
                if (tv_upload.getVisibility() == View.VISIBLE) {
                    Toast.makeText(EditShopProfileActivity.this, "Upload shop image", Toast.LENGTH_SHORT).show();
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
                } else if (TextUtils.isEmpty(et_gstin.getText())) {
                    til_gstin.setError("Enter shop GSTIN.");
                    et_gstin.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(et_pincode.getText())) {
                    til_pincode.setError("Pincode can't be empty.");
                    et_pincode.requestFocus();
                    return;
                } else if (timings_from == null || timings_to == null) {
                    Toast.makeText(EditShopProfileActivity.this, "Please set the shop timings", Toast.LENGTH_SHORT).show();
                    return;
                } else if (lat == 0.0D || lng == 0.0D || radius == 0.0D) {
                    Toast.makeText(EditShopProfileActivity.this, "Set your shop location to continue", Toast.LENGTH_SHORT).show();
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
                        final String shop_id = getIntent().getStringExtra("shop_id");
                        String url = taskSnapshot.getDownloadUrl().toString();
                        Map<String, Object> shop = new HashMap<>();
                        shop.put("shop_name", et_shop_name.getText().toString());
                        shop.put("shop_address", et_address.getText().toString());
                        shop.put("shop_gstin", et_gstin.getText().toString());
                        shop.put("shop_pincode", et_pincode.getText().toString());
                        shop.put("shop_timings", btn_from.getText() + " to " + btn_to.getText());
                        shop.put("shop_location", lat + "," + lng);
                        shop.put("delivery_radius", radius);
                        shop.put("shop_image_url", url);

                        db.collection("vendors").document(mobile_number).collection("shops").document(shop_id).update(shop)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        finish();
                                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditShopProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditShopProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
            if (ContextCompat.checkSelfPermission(EditShopProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                    startActivity(new Intent(EditShopProfileActivity.this, ShopLocationActivity.class));
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
                    Toast.makeText(EditShopProfileActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    img_shop.setImageBitmap(bitmap);
                    tv_upload.setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EditShopProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            img_shop.setImageBitmap(thumbnail);
            tv_upload.setVisibility(View.GONE);
//            saveImage(thumbnail);
            Toast.makeText(EditShopProfileActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        til_shop_name = (TextInputLayout) findViewById(R.id.textInputLayout4);
        til_address = (TextInputLayout) findViewById(R.id.textInputLayout5);
        til_pincode = (TextInputLayout) findViewById(R.id.textInputLayout7);
        til_gstin = (TextInputLayout) findViewById(R.id.textInputLayout8);

        tv_upload = (TextView) findViewById(R.id.tv_upload);
        btn_upload = (FloatingActionButton) findViewById(R.id.btn_upload);
        tv_delivery_radius = (TextView) findViewById(R.id.tv_delivery_radius);
        tv_location = (TextView) findViewById(R.id.tv_location);
        btn_from = (Button) findViewById(R.id.btn_from);
        btn_to = (Button) findViewById(R.id.btn_to);
        btn_location = (Button) findViewById(R.id.btn_location);
        btn_save_profile = (Button) findViewById(R.id.btn_save_profile);

        img_shop = (ImageView) findViewById(R.id.img_shop);
        et_shop_name = (EditText) findViewById(R.id.et_shop_name);
        et_address = (EditText) findViewById(R.id.et_shop_address);
        et_pincode = (EditText) findViewById(R.id.et_shop_pincode);
        et_gstin = (EditText) findViewById(R.id.et_shop_gstin);
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
