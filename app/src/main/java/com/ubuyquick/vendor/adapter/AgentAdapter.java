package com.ubuyquick.vendor.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.vendor.DeliveryAgentsActivity;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.model.Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentAdapter extends RecyclerView.Adapter<AgentAdapter.ViewHolder> {

    private static final String TAG = "AgentAdapter";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String shop_id, vendor_number, shop_name, image_url;

    private Context context;
    private List<Agent> agents;

    public AgentAdapter(Context context, String shop_id, String vendor_number, String shop_name, String image_url) {
        this.context = context;
        this.shop_id = shop_id;
        this.image_url = image_url;
        this.vendor_number = vendor_number;
        this.shop_name = shop_name;
        this.agents = new ArrayList<>();
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public void setAgents(List<Agent> agents) {
        this.agents = agents;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_number;
        private ImageButton btn_delete;
        private ImageButton btn_edit;

        public ViewHolder(final View itemView) {
            super(itemView);

            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_number = (TextView) itemView.findViewById(R.id.tv_number);
            this.btn_delete = (ImageButton) itemView.findViewById(R.id.btn_delete);
            this.btn_edit = (ImageButton) itemView.findViewById(R.id.btn_edit);

            this.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                    builder2.setTitle("Delete agent?");
                    builder2.setNegativeButton("Cancel", null);

                    builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Map<String, Object> agentInfo = new HashMap<>();
                            db.collection("delivery_agents").document(agents.get(getAdapterPosition()).getNumber()).delete();
                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                    .collection("shops").document(shop_id).collection("delivery_agents")
                                    .document(agents.get(getAdapterPosition()).getNumber()).delete();
                            agents.remove(getAdapterPosition());
                            agentInfo.put("deliveryagent_count", agents.size());
                            notifyItemRemoved(getAdapterPosition());
                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                    .collection("shops").document(shop_id).update(agentInfo);
                        }
                    });

                    builder2.show();
                }
            });

            this.btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_name_phone, null, false);

                    final TextInputEditText name = (TextInputEditText) viewInflated.findViewById(R.id.et_name);
                    final TextInputEditText number = (TextInputEditText) viewInflated.findViewById(R.id.et_number);


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Agent mobile number:");
                    name.setText(agents.get(getAdapterPosition()).getName());
                    number.setText(agents.get(getAdapterPosition()).getNumber());
                    builder.setView(viewInflated);
                    builder.setNegativeButton("Cancel", null);

                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Map<String, Object> agent = new HashMap<>();
                            agent.put("user_id", number.getText().toString());
                            agent.put("user_role", "DELIVERY_AGENT");
                            agent.put("name", name.getText().toString());

                            String previousNumber = agents.get(getAdapterPosition()).getNumber();

                            db.collection("delivery_agents").document(agents.get(getAdapterPosition()).getNumber()).delete();

                            if (!previousNumber.equals(number.getText().toString())) {
                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                        .collection("shops").document(shop_id).collection("delivery_agents")
                                        .document(agents.get(getAdapterPosition()).getNumber()).delete();
                            }

                            db.collection("delivery_agents").document(number.getText().toString()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.getResult().exists()) {
                                                Map<String, Object> shop = new HashMap<>();
                                                shop.put("shop_name", shop_name);
                                                shop.put("image_url", image_url);
                                                shop.put("vendor_id", vendor_number);
                                                shop.put("shop_id", shop_id);

                                                db.collection("delivery_agents").document(number.getText().toString()).collection("shops").document(shop_id)
                                                        .set(shop);
                                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                        .collection("shops").document(shop_id).collection("delivery_agents")
                                                        .document(number.getText().toString()).update(agent);
                                            } else {
                                                db.collection("delivery_agents").document(number.getText().toString()).set(agent)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Map<String, Object> shop = new HashMap<>();
                                                                shop.put("shop_name", shop_name);
                                                                shop.put("image_url", image_url);
                                                                shop.put("vendor_id", vendor_number);
                                                                shop.put("shop_id", shop_id);
                                                                db.collection("delivery_agents").document(number.getText().toString()).collection("shops").document(shop_id)
                                                                        .set(shop);
                                                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                        .collection("shops").document(shop_id).collection("delivery_agents")
                                                                        .document(number.getText().toString()).update(agent);
                                                            }
                                                        });
                                            }
                                            agents.set(getAdapterPosition(), new Agent(name.getText().toString(), number.getText().toString()));
                                            notifyDataSetChanged();
                                        }
                                    });
                        }
                    });

                    builder.show();
                }
            });

        }


        public void bind(Agent agent) {
            this.tv_name.setText(agent.getName());
            this.tv_number.setText(agent.getNumber());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.card_agent, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.agents.get(position));
    }

    @Override
    public int getItemCount() {
        return agents.size();
    }
}
