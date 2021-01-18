package com.example.recycle_app.User.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.recycle_app.Pengepul.Model.ModelHargaBarang;
import com.example.recycle_app.User.Adapter.MyOrderAdapter;
import com.example.recycle_app.User.Model.ModelJualBarang;
import com.example.recycle_app.User.Model.ModelMyOrder;
import com.example.recycle_app.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MyOrderFragment extends Fragment {

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<ModelMyOrder, MyOrderAdapter> mAdapter;
    private RecyclerView mRecyclerView;

    FirebaseAuth auth;

    ModelJualBarang modelJualBarang = new ModelJualBarang();
    public String id_transaksi;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_order, container, false);

        //Untuk atuhentikasi user yang login
        auth = FirebaseAuth.getInstance();


        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecyclerView = view.findViewById(R.id.recyclerview_myOrder);
        mRecyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        getdata();
        return view;
    }

    private void getdata() {
        // Set up FirebaseRecyclerAdapter with the Query
        Query query = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<ModelMyOrder>()
                .setQuery(query, ModelMyOrder.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<ModelMyOrder, MyOrderAdapter>(options) {
            @NonNull
            @Override
            public MyOrderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new MyOrderAdapter(inflater.inflate(R.layout.item_myorder, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull MyOrderAdapter holder, int position, @NonNull final ModelMyOrder model) {
                holder.bindtoItemOrder(model);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hapusdata(model);
                    }
                });
            }
        };
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    private Query getQuery(DatabaseReference mDatabase) {
        String getUserID = auth.getCurrentUser().getUid();
        Query query = mDatabase.child("Transaksi").orderByChild("id_pengguna").equalTo(getUserID);
        return query;
    }

    public void hapusdata(ModelMyOrder modelMyOrder) {
        /*
         * Kode ini akan dipanggil ketika method onDeleteData
         * dipanggil dari adapter pada RecyclerView melalui interface.
         * kemudian akan menghapus data berdasarkan primary key dari data tersebut
         * Jika berhasil, maka akan memunculkan Toast

        String userID = auth.getUid();
        String key = modelJualBarang.getKey();
        if(mDatabase != null){
            mDatabase.child("Transaksi")
                    .child(userID)
                    .child(modelJualBarang.getKey())
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
         */

        String userID = auth.getUid();
        mDatabase.child("Transaksi")
                .orderByChild("id_transaksi")
                .equalTo(modelMyOrder.id_transaksi)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1: snapshot.getChildren()){
                            snapshot1.getRef().removeValue();
                        }
                        Toast.makeText(getContext(), "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}