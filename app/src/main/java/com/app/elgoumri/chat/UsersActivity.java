package com.app.elgoumri.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.elgoumri.R;
import com.app.elgoumri.adapter.TransactionAdapter;
import com.app.elgoumri.bean.Constants;
import com.app.elgoumri.bean.SessionManager;
import com.app.elgoumri.bean.Transaction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView listTransactionRV;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList = new ArrayList<>();
    private DatabaseReference transactionRef;
    private SessionManager sessionManager;
    private Intent intent;
    private String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        sessionManager = new SessionManager(this);
        idUser = sessionManager.getUserFromSession().getId();
        listTransactionRV = findViewById(R.id.transactions_rv);
        intent = new Intent(this, MessagesActivity.class);
        initRecyclerView(this);
        transactionRef = FirebaseDatabase.getInstance().getReference(Transaction.class.getSimpleName().toLowerCase());
        sessionManager = new SessionManager(this);

        getTransactions();
    }

    private void getTransactions() {
        transactionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Transaction transaction = postSnapshot.getValue(Transaction.class);
                    if(transaction.getIdUser1().equals(idUser) || transaction.getIdUser2().equals(idUser)){
                        transactionList.add(transaction);
                    }
                }

                transactionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initRecyclerView(UsersActivity usersActivity) {
        transactionAdapter = new TransactionAdapter(transactionList, idUser, usersActivity);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listTransactionRV.setLayoutManager(mLayoutManager);
        listTransactionRV.setItemAnimator(new DefaultItemAnimator());
        listTransactionRV.setAdapter(transactionAdapter);
    }

    public void goToMessage(Transaction transaction){
        intent.putExtra(Constants.TRANSACTION_KEY, transaction);
        intent.putExtra(Constants.FROM_TRANSACTION_KEY, true);
        startActivity(intent);
    }
}