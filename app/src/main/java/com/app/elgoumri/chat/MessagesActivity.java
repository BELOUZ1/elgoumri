package com.app.elgoumri.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.elgoumri.R;
import com.app.elgoumri.adapter.MessageAdapter;
import com.app.elgoumri.bean.Annonce;
import com.app.elgoumri.bean.Constants;
import com.app.elgoumri.bean.Message;
import com.app.elgoumri.bean.SessionManager;
import com.app.elgoumri.bean.Transaction;
import com.app.elgoumri.data.DataHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText messageET;
    private ImageView sendMassageImg;
    private RecyclerView listMessageRV;
    private DatabaseReference messageRef;
    private SessionManager sessionManager;
    private String idReceiver;
    private String idSender;
    private String titreAnnonce;
    private String receiver;
    private DateFormat dateFormat;
    private DatabaseReference transactionRef;
    private DataHelper dataHelper;
    private boolean createTransaction;
    private String idTransaction;
    private List<Message> messages = new ArrayList<>();
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        messageET = findViewById(R.id.message_et);
        sendMassageImg = findViewById(R.id.send_message_img);
        listMessageRV = findViewById(R.id.list_messages_rv);

        sessionManager = new SessionManager(this);
        dateFormat = new SimpleDateFormat("mm-dd hh:mm");
        dataHelper = new DataHelper(this);

        initTransaction();
        initRecyclerView();
        getMessages();


        sendMassageImg.setOnClickListener(this);
    }

    private void initRecyclerView() {
        adapter = new MessageAdapter(messages, idReceiver);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listMessageRV.setLayoutManager(mLayoutManager);
        listMessageRV.setItemAnimator(new DefaultItemAnimator());
        listMessageRV.setAdapter(adapter);
    }

    private void initTransaction() {
        Intent intent = getIntent();
        boolean fromTransaction = intent.getBooleanExtra(Constants.FROM_TRANSACTION_KEY, false);
        if(fromTransaction){
            Transaction transaction = (Transaction) intent.getSerializableExtra(Constants.TRANSACTION_KEY);
            idReceiver = transaction.getIdReceiver();
            titreAnnonce = transaction.getAnnonce();
            idSender = transaction.getIdSender();
            receiver = transaction.getReceiver();
            idTransaction = transaction.getId();
            createTransaction =false;
        }else{
            Annonce annonce = (Annonce) intent.getSerializableExtra(Constants.ANNONCE_KEY);
            idReceiver = annonce.getUtilisateur().getId();
            titreAnnonce = annonce.getTitre();
            receiver = annonce.getUtilisateur().getPrenom();
            createTransaction = true;
            idSender = sessionManager.getUserFromSession().getId();
            idTransaction = idSender + "-" + idReceiver + "-" + annonce.getId();
        }



        transactionRef = FirebaseDatabase.getInstance().getReference(Transaction.class.getSimpleName().toLowerCase());
        messageRef = FirebaseDatabase.getInstance().getReference(Message.class.getSimpleName().toLowerCase()).child(idTransaction);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        String message = messageET.getText().toString().trim();
        if(id == R.id.send_message_img && StringUtils.isNotEmpty(message)){
            if(createTransaction){
                createTransaction();
            }
            doSendMessage(message);
            messageET.setText("");
        }
    }

    private void doSendMessage(String message) {
        String idMessage = messageRef.push().getKey();
        Date date = Calendar.getInstance().getTime();
        String time = dateFormat.format(date);
        Message mMessage = new Message();
        mMessage.setId(idMessage);
        mMessage.setIdSender(sessionManager.getUserFromSession().getId());
        mMessage.setContent(message);

        if(idReceiver.equals(sessionManager.getUserFromSession().getId())){
            mMessage.setIdReceiver(idSender);
        }else {
            mMessage.setIdReceiver(idReceiver);
        }

        mMessage.setTime(time);

        messageRef.child(idMessage).setValue(mMessage);
    }

    private void createTransaction(){
        String sender = sessionManager.getUserFromSession().getPrenom();
        if(!dataHelper.existeTransaction(idTransaction)){
            Transaction transaction = new Transaction();
            transaction.setId(idTransaction);
            transaction.setIdSender(idSender);
            transaction.setIdReceiver(idReceiver);
            transaction.setSender(sender);
            transaction.setReceiver(receiver);
            transaction.setAnnonce(titreAnnonce);
            transactionRef.child(idTransaction).setValue(transaction);
            createTransaction = false;
        }

    }

    private void getMessages(){

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                messages.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Message message = postSnapshot.getValue(Message.class);
                    messages.add(message);

                }
                adapter.notifyDataSetChanged();
                listMessageRV.smoothScrollToPosition(adapter.getItemCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}