package com.prisma.telollevo.adapter;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.fragments.CategoryFragment;
import com.prisma.telollevo.models.Category;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.ViewHolderCat> {

    private AppCompatActivity c;
    private ArrayList<Category> categories = new ArrayList<>();

    public HomePageAdapter(AppCompatActivity n, ArrayList<Category> kk){
        this.c = n;
        this.categories = kk;
    }

    @NonNull
    @Override
    public ViewHolderCat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_cat, parent, false);
        return new ViewHolderCat(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCat holder, int position) {

        final Category cat = categories.get(position);

        holder.title.setText(cat.title);


        if(!cat.url_img.isEmpty()){

            Picasso.get().load(Uri.parse(cat.url_img)).placeholder(R.drawable.list).fit().into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Log.e("MAIN", "onError: "+e.toString() );
                }
            });

        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Toast.makeText(c, "Info: id "+cat.id+" descripcion: "+cat.desc, Toast.LENGTH_LONG).show();

                //Intent i = new Intent(c, DeliveryActivity.class);

                Bundle l = new Bundle();
                l.putString(CategoryFragment.key_id_cat, cat.id);

                FragmentTransaction rt = c.getSupportFragmentManager().beginTransaction();

                CategoryFragment frc = new CategoryFragment();

                frc.setArguments(l);

                rt.add(R.id.framm, frc);
                rt.addToBackStack(null);

                rt.commitAllowingStateLoss();
                UtilsHelper.goToMain = true;
                UtilsHelper.hideToolbar(c);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class ViewHolderCat extends RecyclerView.ViewHolder {


        private CircleImageView imageView;
        private TextView title;

        public ViewHolderCat(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_cat);
            imageView = itemView.findViewById(R.id.cat_img);
        }
    }
}
