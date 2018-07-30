package com.ubuyquick.vendor;

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
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

public class NewProductActivity extends AppCompatActivity {

    private static final String TAG = "NewProductActivity";

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    private int GALLERY = 1, CAMERA = 2, IMAGE_UPLOAD;

    private Button btn_add, btn_upload;
    private TextInputEditText et_product_name, et_product_stock, et_product_description, et_product_discount, et_product_mrp;
    private TextInputLayout til_product_name, til_product_stock, til_product_description, til_product_discount, til_product_mrp;
    private ImageView img_product;

    private String[] categories = {"dry_fruits", "Organic Staples"};
    private String[] quantities = {"kg", "gm", "ltr", "ml", "pc"};

    private Spinner s_categories, s_subcategories, s_quantities;

    private String[][] cats = {{"sub_category_1", "dry_fruits2"}, {"dry_fruits1", "dry_fruits3"}, {"dry_fruits1", "dry_fruits2"},
            {"dry_fruits1", "dry_fruits2"}, {"dry_fruits1", "dry_fruits2"}, {"dry_fruits1", "dry_fruits2"}, {"dry_fruits1", "dry_fruits2"},
            {"dry_fruits1", "dry_fruits2"}};

    private String category, sub_category, quantity, shop_id, shop_vendor;
    private String name, description, discount, stock, mrp;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayAdapter<String> arrayAdapter2;
    private ArrayAdapter<String> arrayAdapter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        s_categories = (Spinner) findViewById(R.id.s_categories);
        s_subcategories = (Spinner) findViewById(R.id.s_subcategories);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        ImageLoader.getInstance().init(new UniversalImageLoader(this).getConfig());
        shop_id = getIntent().getStringExtra("shop_id");

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter2.addAll(categories);
        arrayAdapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter3.addAll(quantities);
        s_subcategories.setAdapter(arrayAdapter);
        s_categories.setAdapter(arrayAdapter2);

        til_product_description = (TextInputLayout) findViewById(R.id.til_product_description);
        til_product_name = (TextInputLayout) findViewById(R.id.til_product_name);
        til_product_stock = (TextInputLayout) findViewById(R.id.til_product_stock);
        til_product_discount = (TextInputLayout) findViewById(R.id.til_product_discount);
        til_product_mrp = (TextInputLayout) findViewById(R.id.til_product_mrp);

        et_product_description = (TextInputEditText) findViewById(R.id.et_product_description);
        et_product_name = (TextInputEditText) findViewById(R.id.et_product_name);
        et_product_stock = (TextInputEditText) findViewById(R.id.et_product_stock);
        et_product_discount = (TextInputEditText) findViewById(R.id.et_product_discount);
        et_product_mrp = (TextInputEditText) findViewById(R.id.et_product_mrp);

        img_product = (ImageView) findViewById(R.id.img_product);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_upload = (Button) findViewById(R.id.btn_upload);

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooserDialog();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til_product_description.setErrorEnabled(false);
                til_product_name.setErrorEnabled(false);
                til_product_stock.setErrorEnabled(false);
                til_product_discount.setErrorEnabled(false);

                if (validateForm()) {
                    storageRef = storage.getReference().child(mAuth.getCurrentUser().getPhoneNumber().substring(3));
                    img_product.setDrawingCacheEnabled(true);

                    Bitmap bmpProduct = img_product.getDrawingCache();
                    ByteArrayOutputStream os_product = new ByteArrayOutputStream();
                    bmpProduct.compress(Bitmap.CompressFormat.JPEG, 100, os_product);
                    byte[] dataProduct = os_product.toByteArray();

                    final String product_id = et_product_name.getText().toString().toLowerCase().replace(' ', '_');

                    UploadTask uploadTask = storageRef.child(shop_id).child("products").
                            child(product_id + ".jpg")
                            .putBytes(dataProduct);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUri = taskSnapshot.getDownloadUrl().toString();

                            category = s_categories.getSelectedItem().toString().toLowerCase().replace(' ', '_');
                            sub_category = s_subcategories.getSelectedItem().toString().toLowerCase().replace(' ', '_');

                            Map<String, Object> product = new HashMap<>();
                            product.put("product_name", name);
                            product.put("product_discount", discount);
                            product.put("product_id", product_id);
                            product.put("product_mrp", Double.valueOf(mrp));
                            product.put("product_quantity", Integer.valueOf(stock));
                            product.put("quantity", s_quantities.getSelectedItem().toString().toLowerCase().replace(' ', '_'));
                            product.put("image_url", downloadUri);

                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                    .collection("shops").document(shop_id).collection("product_categories")
                                    .document(category).collection(sub_category).document(product_id).set(product);

                            product.put("category", category);
                            product.put("sub_category", sub_category);

                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                    .collection("shops").document(shop_id).collection("inventory").document(product_id)
                                    .set(product);

                            Toast.makeText(NewProductActivity.this, "Added product successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });


                }
            }
        });

        s_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrayAdapter.clear();
                arrayAdapter.addAll(cats[position]);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showChooserDialog() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(NewProductActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                    Toast.makeText(NewProductActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    img_product.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(NewProductActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            img_product.setImageBitmap(thumbnail);
//            saveImage(thumbnail);
            Toast.makeText(NewProductActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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

    private boolean validateForm() {
        name = et_product_name.getText().toString();
        description = et_product_description.getText().toString();
        discount = et_product_discount.getText().toString();
        stock = et_product_stock.getText().toString();
        mrp = et_product_mrp.getText().toString();

        if (TextUtils.isEmpty(name)) {
            til_product_name.setError("Product name cannot be empty.");
            et_product_name.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(description)) {
            til_product_description.setError("Product description required.");
            et_product_description.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(discount)) {
            til_product_discount.setError("Product discount required.");
            et_product_discount.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(stock)) {
            til_product_stock.setError("Product stock cannot be empty.");
            et_product_stock.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(mrp)) {
            til_product_mrp.setError("Product price cannot be empty.");
            et_product_mrp.requestFocus();
            return false;
        } else {
            return true;
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
}
