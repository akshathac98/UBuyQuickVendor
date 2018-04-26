package com.ubuyquick.vendor;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddShopActivity extends AppCompatActivity {

    private static final String TAG = "AddShopActivity";

    private Button btn_from, btn_to, btn_location, btn_add_shop;
    private CheckBox cb_groceries, cb_provisions, cb_livestock, cb_vegetables;

    private String timings_from = null, timings_to = null;

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

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(AddShopActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddShopActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                    } else {
                        startActivity(new Intent(AddShopActivity.this, ShopLocationActivity.class));
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

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case 101:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location required to set radius", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(AddShopActivity.this, ShopLocationActivity.class));
                }
                break;

                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initializeViews() {
        btn_from = (Button) findViewById(R.id.btn_from);
        btn_to = (Button) findViewById(R.id.btn_to);
        btn_location = (Button) findViewById(R.id.btn_location);
        btn_add_shop = (Button) findViewById(R.id.btn_add_shop);

        cb_groceries = (CheckBox) findViewById(R.id.checkBox2);
        cb_vegetables = (CheckBox) findViewById(R.id.checkBox3);
        cb_livestock = (CheckBox) findViewById(R.id.checkBox4);
        cb_provisions = (CheckBox) findViewById(R.id.checkBox5);
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
