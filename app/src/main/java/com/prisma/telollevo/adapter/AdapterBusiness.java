package com.prisma.telollevo.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.fragments.BusinessFragment;
import com.prisma.telollevo.models.Bussines;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class AdapterBusiness extends RecyclerView.Adapter<AdapterBusiness.ViewHolderBussines> {

    private AppCompatActivity c;
    private ArrayList<Bussines> bussines = new ArrayList<>();

    public AdapterBusiness(AppCompatActivity m, ArrayList<Bussines> bss){
        this.c = m;
        this.bussines = bss;
    }

    @NonNull
    @Override
    public ViewHolderBussines onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_busi, parent, false);
        return new ViewHolderBussines(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderBussines holder, int position) {
final Bussines b = bussines.get(position);

if(!b.url_img.isEmpty()){
    Picasso.get().load(Uri.parse(b.url_img)).fit().placeholder(R.drawable.placeholder_img).transform(new CropCircleTransformation()).into(holder.img);
}

holder.title.setText(b.name_b);
holder.direccion.setText(b.dir_bus);
holder.desc.setText(b.giro);
holder.rating.setText(b.rate+" ⭐");

holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        /*BussinesActivity.negocioAct = b;
        c.startActivity(new Intent(c, BussinesActivity.class));
        Animatoo.animateSlideUp(c);*/

        FragmentTransaction tr = c.getSupportFragmentManager().beginTransaction();

        BusinessFragment.negocioAct = b;

        tr.add(R.id.framm, new BusinessFragment());
        tr.addToBackStack(null);
        UtilsHelper.goToMain = false;

        tr.commitAllowingStateLoss();
    }
});
    }

    @Override
    public int getItemCount() {
        return bussines.size();
    }

    public void filterSearch(ArrayList<Bussines> busfr) {
        bussines = busfr;
        notifyDataSetChanged();
    }

    class ViewHolderBussines extends RecyclerView.ViewHolder{


        TextView title, desc, direccion, rating;
        ImageView img;

        public ViewHolderBussines(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_bussi);
            desc = itemView.findViewById(R.id.desc_bussin);
            direccion = itemView.findViewById(R.id.dire_bussi);
            rating = itemView.findViewById(R.id.rating_bussi);
            img = itemView.findViewById(R.id.img_bussi);
        }
    }
}
