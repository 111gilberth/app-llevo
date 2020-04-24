package com.prisma.telollevo.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prisma.telollevo.R;
import com.prisma.telollevo.models.PromoteModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterPromo extends RecyclerView.Adapter<AdapterPromo.ViewHolderBussines> {

    private Context c;
    private ArrayList<PromoteModel> promoteModels = new ArrayList<>();

    public AdapterPromo(Context m, ArrayList<PromoteModel> bss){
        this.c = m;
        this.promoteModels = bss;
    }

    @NonNull
    @Override
    public ViewHolderBussines onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_promotion, parent, false);
        return new ViewHolderBussines(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderBussines holder, int position) {
PromoteModel b = promoteModels.get(position);

if(!b.url_img.isEmpty()){
    Picasso.get().load(Uri.parse(b.url_img)).fit().placeholder(R.drawable.placeholder_img).into(holder.img);
}

    }

    @Override
    public int getItemCount() {
        return promoteModels.size();
    }

    class ViewHolderBussines extends RecyclerView.ViewHolder{

        ImageView img;

        public ViewHolderBussines(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_promote);
        }
    }
}
