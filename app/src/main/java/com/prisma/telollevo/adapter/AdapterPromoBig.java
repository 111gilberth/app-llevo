package com.prisma.telollevo.adapter;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.ApiResources;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.fragments.BusinessFragment;
import com.prisma.telollevo.models.Bussines;
import com.prisma.telollevo.models.PromoteModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterPromoBig extends RecyclerView.Adapter<AdapterPromoBig.ViewHolderBussines> {

    private AppCompatActivity c;
    private ArrayList<PromoteModel> promoteModels = new ArrayList<>();

    public AdapterPromoBig(AppCompatActivity m, ArrayList<PromoteModel> bss){
        this.c = m;
        this.promoteModels = bss;
    }

    @NonNull
    @Override
    public ViewHolderBussines onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.promote_lay, parent, false);
        return new ViewHolderBussines(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderBussines holder, int position) {
final PromoteModel b = promoteModels.get(position);

if(!b.url_img.isEmpty()){
    //Log.e("MAIN", "onBindViewHolder: "+b.url_img );
    Picasso.get().load(Uri.parse(b.url_img)).fit().placeholder(R.drawable.placeholder_img).into(holder.img, new Callback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError(Exception e) {
            Log.e("MAIN", "onError: "+e.getMessage());
        }
    });
}
String prf = b.price;
holder.price.setText(prf);
holder.desc.setText(b.desc);
holder.title_buss.setText(b.negocio_title);


holder.img.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        String url = ApiResources.getBussinesById(b.id_negocio);

        UtilsHelper.getArrayDataFrom(c, url, new UtilsHelper.loadJson() {
            @Override
            public void onLoadSuccess(JSONArray array) {

                try {
                    for (int i = 0; i < array.length(); i++) {
                        Bussines bussines = new Bussines();
                        JSONObject a = array.getJSONObject(i);

                        bussines.codig = a.getString("t_codi");
                        bussines.dir_bus = a.getString("t_direccion");
                        bussines.giro = a.getString("t_giro");
                        bussines.numero_cel = a.getString("t_no");
                        bussines.idne = a.getString("t_idne");
                        bussines.longitud = a.getString("t_long");
                        bussines.lat = a.getString("t_lati");
                        bussines.name_b = a.getString("t_nomb");

                        goToBusiness(bussines);

                        break;
                    }

                }catch(JSONException e){
                    Log.e("MAIN", "onLoadSuccess: "+e.getMessage());
                }
            }

            @Override
            public void onError(String reason) {
                Toast.makeText(c, reason, Toast.LENGTH_SHORT).show();
            }
        });


    }
});
    }

    @Override
    public int getItemCount() {
        return promoteModels.size();
    }

    class ViewHolderBussines extends RecyclerView.ViewHolder{

        ImageView img;
        TextView title_buss, price, desc;

        public ViewHolderBussines(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_promote);
            title_buss = itemView.findViewById(R.id.title_bussi);
            price = itemView.findViewById(R.id.price_promo);
            desc = itemView.findViewById(R.id.desc_promo);
        }
    }


    private void goToBusiness(Bussines b){
        FragmentTransaction tr = c.getSupportFragmentManager().beginTransaction();

        BusinessFragment.negocioAct = b;

        BusinessFragment fb = new BusinessFragment();

        Bundle bund = new Bundle();

        bund.putBoolean(BusinessFragment.key_tos, true);

        fb.setArguments(bund);

        tr.add(R.id.framm, fb);
        tr.addToBackStack(null);

        tr.commitAllowingStateLoss();
        UtilsHelper.hideToolbar(c);
    }
}
