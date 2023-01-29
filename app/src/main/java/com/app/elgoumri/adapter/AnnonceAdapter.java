package com.app.elgoumri.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.app.elgoumri.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.elgoumri.annonce.ListAnnonceActivity;
import com.app.elgoumri.bean.Annonce;
import com.app.elgoumri.home.ElGoumriActivity;
import com.app.elgoumri.home.ui.home.HomeFragment;
import com.app.elgoumri.home.ui.searche.SearcheFragment;

import java.util.List;

public class AnnonceAdapter extends RecyclerView.Adapter<AnnonceAdapter.MyViewHolder>{

    private List<Annonce> annonces;
    private ListAnnonceActivity listAnnonceActivity;
    private HomeFragment homeFragment;
    private SearcheFragment searcheFragment;
    private boolean estMoi;

    public AnnonceAdapter(List<Annonce> annonces, ListAnnonceActivity listAnnonceActivity, boolean estMoi) {
        this.annonces = annonces;
        this.listAnnonceActivity = listAnnonceActivity;
        this.estMoi = estMoi;
    }

    public AnnonceAdapter(List<Annonce> annonces, HomeFragment homeFragment, boolean estMoi) {
        this.annonces = annonces;
        this.homeFragment = homeFragment;
        this.estMoi = estMoi;
    }

    public AnnonceAdapter(List<Annonce> annonces, SearcheFragment searcheFragment, boolean estMoi) {
        this.annonces = annonces;
        this.searcheFragment = searcheFragment;
        this.estMoi = estMoi;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.annonceitem, parent, false);
        return new AnnonceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Annonce annonce = annonces.get(position);
        holder.depart.setText("Départ de " + annonce.getAdresseDepart() + " le " + annonce.getDateDepart());
        if(annonce.isGratuit()){
            holder.titre.setText(annonce.getTitre() + " - Gratuit");
        }else{
            holder.titre.setText(annonce.getTitre() + " - " + annonce.getPrix() + " " + annonce.getDevise());
        }
        holder.tag.setText(annonce.getCategorie());
        holder.arrive.setText("Arrivé à " + annonce.getAdressArrive() + " le " + annonce.getDateArrive());

        if(estMoi){
            holder.delete.setVisibility(View.VISIBLE);
        }else{
            holder.delete.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return annonces.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView depart, arrive,tag, titre;
        public ImageView delete, viewAnnonce;

        public MyViewHolder(View view) {
            super(view);
            depart = view.findViewById(R.id.annonce_depart_tv);
            arrive = view.findViewById(R.id.annonce_arv_tv);
            titre = view.findViewById(R.id.annonce_titre_tv);
            delete = view.findViewById(R.id.annonce_delete_img);
            tag = view.findViewById(R.id.annonce_tag_tv);
            viewAnnonce = view.findViewById(R.id.annonce_view_img);

            delete.setOnClickListener(this);
            viewAnnonce.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id == R.id.annonce_delete_img){
                if(listAnnonceActivity != null){
                    listAnnonceActivity.deleteAnnonce(getAdapterPosition());
                }
            }

            if(id == R.id.annonce_view_img){

                if(listAnnonceActivity != null){
                    listAnnonceActivity.viewAnnonce(getAdapterPosition());
                }

                if(homeFragment != null){
                    homeFragment.viewAnnonce(getAdapterPosition());
                }

                if(searcheFragment != null){
                    searcheFragment.viewAnnonce(getAdapterPosition());
                }

            }
        }
    }
}
