package com.ubuyquick.vendor.shop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ImageView img_shop;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        img_shop = (ImageView) view.findViewById(R.id.img_shop);
        ImageLoader.getInstance().init(new UniversalImageLoader(getContext()).getConfig());
        UniversalImageLoader.setImage("https://firebasestorage.googleapis.com/v0/b/ubuyquick-d4121.appspot.com/o/9008003968%2FBhyrava%20Provisions%2Fshop_image.jpg?alt=media&token=8592f84e-42c3-4e6a-a522-54bddc907ec6", img_shop);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        return view;
    }
}
