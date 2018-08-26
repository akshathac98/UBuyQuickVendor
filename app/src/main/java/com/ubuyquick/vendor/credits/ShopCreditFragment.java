package com.ubuyquick.vendor.credits;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.adapter.CreditAdapter;
import com.ubuyquick.vendor.model.Credit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShopCreditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShopCreditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopCreditFragment extends Fragment {

    private static final String TAG = "CreditFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RecyclerView rv_credits;
    private CreditAdapter creditAdapter;
    private List<Credit> credits;
    private EditText et_search;
    private Button btn_add, btn_message;

    private String shop_id;
    private String shop_name;
    private int LOGIN_MODE = 0;
    private String number1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ShopCreditFragment() {


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShopCreditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShopCreditFragment newInstance(String param1, String param2) {
        ShopCreditFragment fragment = new ShopCreditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_credit, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_MODE", Context.MODE_PRIVATE);
        LOGIN_MODE = preferences.getInt("LOGIN_MODE", 0);
        if (LOGIN_MODE == 1) {
            number1 = getArguments().getString("vendor_id");
        } else {
            number1 = mAuth.getCurrentUser().getPhoneNumber().substring(3);
        }
        shop_id = getArguments().getString("shop_id");
        shop_name = getArguments().getString("shop_name");

        rv_credits = (RecyclerView) view.findViewById(R.id.rv_credits);
        credits = new ArrayList<>();
        et_search = (EditText) view.findViewById(R.id.et_search);
        creditAdapter = new CreditAdapter(view.getContext(), shop_id, getArguments().getString("vendor_id"), shop_name);
        rv_credits.setAdapter(creditAdapter);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_message = (Button) view.findViewById(R.id.btn_message);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                creditAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        db.collection("vendors").document(number1)
                .collection("shops").document(shop_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                btn_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_credit_message, null, false);
                        final TextInputEditText message = (TextInputEditText) viewInflated.findViewById(R.id.et_message);
                        message.setText(task.getResult().getData().get("credit_message").toString());

                        builder.setTitle("Enter credit remainder message:");
                        builder.setView(viewInflated)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (TextUtils.isEmpty(message.getText().toString()))
                                            Toast.makeText(getContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
                                        else {
                                            Map<String, Object> msg = new HashMap<>();
                                            msg.put("credit_message", message.getText().toString());
                                            db.collection("vendors").document(number1)
                                                    .collection("shops").document(shop_id).update(msg);
                                            Toast.makeText(getContext(), "Credit remainder message saved.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", null);
                        builder.show();
                    }
                });
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_credit, null, false);
                final TextInputEditText name = (TextInputEditText) viewInflated.findViewById(R.id.et_name);
                final TextInputEditText number = (TextInputEditText) viewInflated.findViewById(R.id.et_number);
                final TextInputEditText balance = (TextInputEditText) viewInflated.findViewById(R.id.et_balance);

                builder.setTitle("Enter credit holder info:");
                builder.setView(viewInflated)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(number.getText().toString()) || TextUtils.isEmpty(balance.getText().toString()))
                                    Toast.makeText(getContext(), "Incomplete info", Toast.LENGTH_SHORT).show();
                                else {
                                    Map<String, Object> credit = new HashMap<>();
                                    credit.put("name", name.getText().toString());
                                    credit.put("number", number.getText().toString());
                                    credit.put("balance", Double.parseDouble(balance.getText().toString()));

                                    credits.add(new Credit(number.getText().toString(), name.getText().toString(),
                                            number.getText().toString(), Double.parseDouble(balance.getText().toString())));
                                    creditAdapter.setCredits(credits);
                                    db.collection("vendors").document(number1).collection("shops")
                                            .document(shop_id).collection("credits").document(number.getText().toString())
                                            .set(credit);
                                    Toast.makeText(getContext(), "Saved credit holder info.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null);
                builder.show();
            }
        });

        db.collection("vendors").document(number1).collection("shops")
                .document(shop_id).collection("credits").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot document : documents) {
                    Map<String, Object> credit = document.getData();
                    credits.add(new Credit(document.getId(), credit.get("name").toString(),
                            credit.get("number").toString(),
                            Double.parseDouble(credit.get("balance").toString())));
                }
                creditAdapter.setCredits(credits);
            }
        });

        return view;
    }

    private void filter(String text) {
        List<Credit> temp = new ArrayList<>();
        for (Credit credit : credits) {
            if (credit.getCustomerName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(credit);
            }
        }
        creditAdapter.setCredits(temp);
    }
/*

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
