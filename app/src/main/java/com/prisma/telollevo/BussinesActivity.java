package com.prisma.telollevo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.prisma.telollevo.utils.ApiResources;
import com.prisma.telollevo.utils.SnappingLinearLayoutManager;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.adapter.AdapterFaml;
import com.prisma.telollevo.models.Bussines;
import com.prisma.telollevo.models.Family;
import com.prisma.telollevo.models.ProductModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BussinesActivity extends AppCompatActivity {

    private TextView title, rate;
    private SearchView searchView;
    private RecyclerView list_rec;
    private ArrayList<Family> families = new ArrayList<>();
    private TabLayout tabLayout;
    private ImageView img;
    private ArrayList<String> strings = new ArrayList<>();
    private AdapterFaml adapterBusiness;
    public static Bussines negocioAct = null;
    private AppBarLayout barLayout;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        negocioAct = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bussines);
        if(negocioAct == null){
            finish();
        }
        barLayout = findViewById(R.id.linlay);
        list_rec = findViewById(R.id.rec_rest);
        searchView = findViewById(R.id.search_buss);
        title = findViewById(R.id.title_bussi);
        tabLayout = findViewById(R.id.tablay);
        rate = findViewById(R.id.rate_buss);


        findViewById(R.id.back_buton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        img = findViewById(R.id.img_bussin);


        Picasso.get().load(Uri.parse(negocioAct.url_img)).fit().into(img);

        title.setText(negocioAct.name_b);
        rate.setText(negocioAct.numero_cel);

        ViewGroup v = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        v.setBackgroundResource(R.drawable.search_back);

        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_search_icon));


        adapterBusiness = new AdapterFaml(this, arrayLists, strings);

        list_rec.setLayoutManager(new SnappingLinearLayoutManager(this));
        list_rec.setAdapter(adapterBusiness);

        getDataProducts();




    }

    private JSONArray familis;
    private void getDataProducts() {

     String url = ApiResources.getFamilyFromBussines(negocioAct.idne);

  UtilsHelper.getArrayDataFrom(this, url, new UtilsHelper.loadJson() {
         @Override
         public void onLoadSuccess(JSONArray array) {
             familis = array;
             try {
             for(int i=0; i < familis.length(); i++){
                 Family f = new Family();

                     JSONObject obb = familis.getJSONObject(i);

                     f.idEmpresa = obb.getString("IdEmpresa");
                     f.Descripcion = obb.getString("Descripcion");

                     f.idFamilia = obb.getString("IdFamilia");

                     families.add(f);


             }

             for(int i = 0; i < families.size(); i++){
                 TabLayout.Tab niutab = tabLayout.newTab();

                 niutab.setText(families.get(i).Descripcion);

                 tabLayout.addTab(niutab);
             }

             getAllDataByFamiliId();

             } catch (JSONException e) {
                 Log.e("MAIN", "onLoadSuccess: "+e.getMessage() );
                 e.printStackTrace();
             }
         }

         @Override
         public void onError(String reason) {

         }
     });

    }


    private ArrayList<ArrayList<ProductModel>> arrayLists = new ArrayList<>();

int fin = 0;
    private void getAllDataByFamiliId() {



        for(int i=0; i < families.size(); i++){

            final ArrayList<ProductModel> productModels = new ArrayList<>();

            final Family f = families.get(i);

            String urr = ApiResources.getProductsByFamily(negocioAct.idne, f.idFamilia);

          //  Log.e("MAIN", "Family url "+urr);
            arrayLists.add(productModels);
            strings.add(f.Descripcion);

            UtilsHelper.getArrayDataFrom(this, urr, new UtilsHelper.loadJson() {
                @Override
                public void onLoadSuccess(JSONArray array) {
                    String d = "0";
                    try {
                        Log.e("MAIN", "onLoadSuccess: "+array.length() );
                    for(int o=0; o < array.length(); o++){

                            JSONObject obj = array.getJSONObject(o);

                            ProductModel mo = new ProductModel();

                            mo.img_url = obj.getString("imagen64");

                        mo.title = obj.getString("Descripcion");
                        mo.price = obj.getString("Precio");
                        mo.desc = obj.getString("Observaciones");
                        mo.id_product = obj.getString("IdArticulo");
                        if(d.equals("0")){
                            d = obj.getString("IdFamilia");
                        }

                        productModels.add(mo);

                        }

                    fin++;

                    if(fin >= families.size()){

                        adapterBusiness.notifyDataSetChanged();

                        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {

                              barLayout.setExpanded(false, true);

                                list_rec.smoothScrollToPosition(tab.getPosition());
                               // View v = adapterBusiness.getFamily(tab.getPosition());
                                //list_rec.scrollTo((int)v.getX(), (int)v.getX());

                            }

                            @Override
                            public void onTabUnselected(TabLayout.Tab tab) {

                            }

                            @Override
                            public void onTabReselected(TabLayout.Tab tab) {

                            }
                        });

                    }
                          }
                    catch (JSONException e) {
                        Log.e("MAIN", "onLoadSuccess: "+e.getMessage() );
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String reason) {
                    Log.e("MAIN", "onError: "+reason);
                }
            });


        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideDown(this);
    }
}
