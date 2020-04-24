package com.prisma.telollevo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prisma.telollevo.R;
import com.prisma.telollevo.models.ProductModel;

import java.util.ArrayList;

public class AdapterFaml extends RecyclerView.Adapter<AdapterFaml.ViewHolderBussines> {

    private Context c;
    private ArrayList<ArrayList<ProductModel>> bussines = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();



    public AdapterFaml(Context m, ArrayList<ArrayList<ProductModel>> bss, ArrayList<String> ttls){
        this.c = m;
        this.names = ttls;
        this.bussines = bss;
    }

    @NonNull
    @Override
    public ViewHolderBussines onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_family, parent, false);
        return new ViewHolderBussines(v);
    }

    private ArrayList<View> views = new ArrayList<>();
    @Override
    public void onBindViewHolder(@NonNull final ViewHolderBussines holder, int position) {
       ArrayList<ProductModel> models = bussines.get(position);

       holder.title.setText(names.get(position));

       if(!views.contains(holder.title)){
           Log.e("MAIN", "onBindViewHolder: added "+names.get(position) );
           views.add(holder.title);
       }

       // Log.e("MAIN", "setup: "+names.get(position) + " "+models.size());


        holder.setup(models);
    }

    @Override
    public int getItemCount() {
        return bussines.size();
    }

    public View getFamily(int position){
        if(position >= views.size()){
            return null;
        }else
        return views.get(position);
    }

    class ViewHolderBussines extends RecyclerView.ViewHolder{


  RecyclerView recyclerView;
  TextView title, empty;


        public ViewHolderBussines(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_fam);
            recyclerView = itemView.findViewById(R.id.rec_prod);

        empty = itemView.findViewById(R.id.empty_fam);
        }

        public void setup(ArrayList<ProductModel> productModels){
           AdapterProducts adapterBusiness = new AdapterProducts(c, productModels);

           if(productModels.size() > 0){
               empty.setVisibility(View.GONE);
           }else{
               empty.setVisibility(View.VISIBLE);
           }

            recyclerView.setLayoutManager(new LinearLayoutManager(c));
            recyclerView.setAdapter(adapterBusiness);
        }
    }
}
