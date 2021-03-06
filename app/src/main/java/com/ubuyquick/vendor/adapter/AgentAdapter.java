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

    private String shop_id, vendor_number, shop_name, image_url, agent_mode;

    private Context context;
    private List<Agent> agents;

    public AgentAdapter(Context context, String shop_id, String vendor_number, String shop_name, String image_url, String agent_mode) {
        this.context = context;
        this.shop_id = shop_id;
        this.agent_mode = agent_mode;
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

            this.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                    if (agent_mode.equals("DELIVERY_AGENT")) {
                        builder2.setTitle("Delete Agent?");
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
                    } else {
                        builder2.setTitle("Delete Manager?");
                        builder2.setNegativeButton("Cancel", null);

                        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String, Object> agentInfo = new HashMap<>();
                                db.collection("managers").document(agents.get(getAdapterPosition()).getNumber()).delete();
                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                        .collection("shops").document(shop_id).collection("managers")
                                        .document(agents.get(getAdapterPosition()).getNumber()).delete();
                                agents.remove(getAdapterPosition());
                                agentInfo.put("manager_count", agents.size());
                                notifyItemRemoved(getAdapterPosition());
                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                        .collection("shops").document(shop_id).update(agentInfo);
                            }
                        });

                        builder2.show();
                    }
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
