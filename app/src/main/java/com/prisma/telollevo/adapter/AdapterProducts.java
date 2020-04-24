package com.prisma.telollevo.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.models.ProductModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.ViewHolderBussines> {

    private Context c;
    private ArrayList<ProductModel> bussines = new ArrayList<>();

    public AdapterProducts(Context m, ArrayList<ProductModel> bss){
        this.c = m;
        this.bussines = bss;
    }

    public void notifyChanged(ArrayList<ProductModel> pr){
        this.bussines = pr;
        notifyDataSetChanged();
    }

    private UtilsHelper.ProductListener listener;
    public boolean isShoping = false;

    public boolean isOrderDetail = false;
    public void setListener(UtilsHelper.ProductListener productListener){
        this.listener = productListener;
    }

    @NonNull
    @Override
    public ViewHolderBussines onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (!isShoping && !isOrderDetail) {
            View v = LayoutInflater.from(c).inflate(R.layout.item_product, parent, false);
            return new ViewHolderBussines(v);
        }else if(!isOrderDetail){
            View v = LayoutInflater.from(c).inflate(R.layout.item_product_inchart, parent, false);
            return new ViewHolderBussines(v);
        }else{
            View v = LayoutInflater.from(c).inflate(R.layout.item_product_detail, parent, false);
            return new ViewHolderBussines(v);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolderBussines holder, int position) {
        final ProductModel b = bussines.get(position);

        ArrayList<ProductModel> prr = UtilsHelper.getOrdersActualInCache();

        if(isShoping){
            holder.add_pr.setVisibility(View.GONE);
        }

        if(isOrderDetail){
            holder.add_pr.setVisibility(View.GONE);
            holder.delete_pr.setVisibility(View.GONE);
        }

        boolean contain = false;
        int quantity = 0;

        for(int i=0; i < prr.size(); i++){
            if(prr.get(i).id_product.equals(b.id_product)){
                contain = true;
                quantity++;
            }

            Log.e("MAIN", "tiene : "+i);
        }

        if(contain){
            holder.delete_pr.setVisibility(View.VISIBLE);
        }else{
            holder.delete_pr.setVisibility(View.GONE);
        }

if(!b.img_url.isEmpty() && !isOrderDetail){
    Picasso.get().load(Uri.parse(b.img_url)).fit().placeholder(R.drawable.placeholder_img).into(holder.img);
}

holder.title.setText(b.title);
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        holder.checkBox.setChecked(!holder.checkBox.isChecked());
    }
});
holder.price.setText(b.price);
holder.desc.setText(b.desc);

        final boolean finalContain = contain;


        holder.add_pr.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if(listener != null){
                listener.onAddProduct(b);
            }
    }
});

        holder.delete_pr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onDeleteProduct(b);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return bussines.size();
    }

    class ViewHolderBussines extends RecyclerView.ViewHolder{


        TextView title, desc, price;
        CheckBox checkBox;
        ImageView img, add_pr, delete_pr;

        public ViewHolderBussines(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_product);
            desc = itemView.findViewById(R.id.desc_product);
            price = itemView.findViewById(R.id.price_product);
            img = itemView.findViewById(R.id.img_product);
            checkBox = itemView.findViewById(R.id.checked_product);
            add_pr = itemView.findViewById(R.id.add_product);
            delete_pr = itemView.findViewById(R.id.delete_product);
        }
    }
}
