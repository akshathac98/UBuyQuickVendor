package com.ubuyquick.vendor.shop;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private View view;
    private ImageView img_shop;

    private Button btn_delivery_agent;
    private Button btn_manager;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment, container, false);



        initializeViews();
        initialize();

        return view;
    }

    private void initializeViews() {
        img_shop = (ImageView) view.findViewById(R.id.img_shop);


        btn_delivery_agent = (Button) view.findViewById(R.id.btn_delivery_agent);
        btn_manager = (Button) view.findViewById(R.id.btn_manager);
        btn_delivery_agent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delivery Agent mobile number:");
                final EditText input = new EditText(v.getContext());
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                builder.setView(input);
                builder.setNegativeButton("Cancel", null);

                builder.setPositiveButton("Add Agent", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(v.getContext(), input.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();

            }
        });
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ImageLoader.getInstance().init(new UniversalImageLoader(getContext()).getConfig());
        UniversalImageLoader.setImage("https://firebasestorage.googleapis.com/v0/b/ubuyquick-d4121.appspot.com/o/9008003968%2FBhyrava%20Provisions%2Fshop_image.jpg?alt=media&token=8592f84e-42c3-4e6a-a522-54bddc907ec6", img_shop);
    }
}
