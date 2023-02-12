package com.app.elgoumri.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.elgoumri.R;
import com.app.elgoumri.bean.Transaction;
import com.app.elgoumri.chat.UsersActivity;

import org.w3c.dom.Text;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder>{

    private List<Transaction> transactionList;
    private String idUser;
    private UsersActivity usersActivity;


    public TransactionAdapter(List<Transaction> transactionList, String idUser, UsersActivity usersActivity) {
        this.transactionList = transactionList;
        this.idUser = idUser;
        this.usersActivity = usersActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transactionitem, parent, false);
        return new TransactionAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.annonce.setText(transaction.getAnnonce());
        String initials;

        if(transaction.getIdUser1().equals(idUser)){
            initials = transaction.getUser2();
            holder.sender.setText(transaction.getUser2());
        }else{
            initials = transaction.getUser1();
            holder.sender.setText(transaction.getUser1());
        }

        String initialNom = String.valueOf(initials.split(" ")[1].charAt(0)).toUpperCase();
        String initialPrenom = String.valueOf(initials.split(" ")[0].charAt(0)).toUpperCase();
        holder.initials.setText(initialPrenom + initialNom);

    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView sender;
        private TextView annonce;
        private TextView initials;
        private CardView transactionCV;

        public MyViewHolder(View view) {
            super(view);
            sender = view.findViewById(R.id.sender_tv);
            annonce = view.findViewById(R.id.annonceitem_tv);
            transactionCV = view.findViewById(R.id.transaction_cv);
            initials = view.findViewById(R.id.initials_tv);

            transactionCV.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();

            if(id == R.id.transaction_cv){
                usersActivity.goToMessage(transactionList.get(getAdapterPosition()));
            }
        }
    }
}
