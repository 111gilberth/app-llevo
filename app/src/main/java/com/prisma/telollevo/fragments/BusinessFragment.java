package com.prisma.telollevo.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.ApiResources;
import com.prisma.telollevo.utils.SnappingLinearLayoutManager;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.adapter.AdapterFaml;
import com.prisma.telollevo.adapter.AdapterProducts;
import com.prisma.telollevo.models.Bussines;
import com.prisma.telollevo.models.Family;
import com.prisma.telollevo.models.ProductModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusinessFragment extends Fragment implements UtilsHelper.ProductListener {


    private TextView title, rate;
    private SearchView searchView;
    private RecyclerView list_rec;
    private ArrayList<Family> families = new ArrayList<>();
    private TabLayout tabLayout;
    private ImageView img;
    private ArrayList<String> strings = new ArrayList<>();
    private AdapterFaml adapterBusiness;
    private AdapterProducts adapterProducts;
    public static Bussines negocioAct = null;
    private AppBarLayout barLayout;


    public BusinessFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_bussines, container, false);
    }

    @Override
    public void onDetach() {
        UtilsHelper.goToMain = !UtilsHelper.goToMain;
        super.onDetach();
    }

    SharedPreferences preferences;
    public static final String key_tos = "KW21";
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(negocioAct == null || getActivity() == null){
return;
        }

        if(getArguments() != null){
            if(getArguments().getBoolean(key_tos)){

                UtilsHelper.goToMain = true;
            }
        }

        view.findViewById(R.id.back_buton).setVisibility(View.GONE);

        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        barLayout = view.findViewById(R.id.linlay);
        list_rec = view.findViewById(R.id.rec_rest);
        searchView = view.findViewById(R.id.search_buss);
        title = view.findViewById(R.id.title_bussi);
        tabLayout = view.findViewById(R.id.tablay);
        rate = view.findViewById(R.id.rate_buss);


        view.findViewById(R.id.back_buton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getFragmentManager().popBackStack();
            }
        });
        img = view.findViewById(R.id.img_bussin);


        Picasso.get().load(Uri.parse(negocioAct.url_img)).fit().into(img);

        title.setText(negocioAct.name_b);
        rate.setText(negocioAct.numero_cel);

        ViewGroup v = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        v.setBackgroundResource(R.drawable.search_back);

        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_search_icon));


        adapterBusiness = new AdapterFaml(getContext(), arrayLists, strings);

        list_rec.setLayoutManager(new SnappingLinearLayoutManager(getContext()));
        list_rec.setAdapter(adapterBusiness);

        getDataProducts();


    }

    private JSONArray familis;
    private void getDataProducts() {

        String url = ApiResources.getFamilyFromBussines(negocioAct.idne);

        UtilsHelper.getArrayDataFrom(getContext(), url, new UtilsHelper.loadJson() {
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

            UtilsHelper.getArrayDataFrom(getContext(), urr, new UtilsHelper.loadJson() {
                @Override
                public void onLoadSuccess(JSONArray array) {
                    String d = "0";
                    try {
                      //  Log.e("MAIN", "onLoadSuccess: "+array.length() );
                        for(int o=0; o < array.length(); o++){

                            JSONObject obj = array.getJSONObject(o);

                            ProductModel mo = new ProductModel();

                            mo.img_url = obj.getString("imagen64");
mo.id_negocio = negocioAct.idne;
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

                       //goingToFirst();

                            goinToSecond();

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


    private void goinToSecond() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                barLayout.setExpanded(false, true);

                ArrayList<ProductModel> pr = arrayLists.get(tab.getPosition());

                adapterProducts = new AdapterProducts(getActivity(), pr);
                adapterProducts.setListener(BusinessFragment.this);
                list_rec.setAdapter(adapterProducts);


             //   list_rec.smoothScrollToPosition(tab.getPosition());
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

        ArrayList<ProductModel> pr = arrayLists.get(0);

        adapterProducts = new AdapterProducts(getActivity(), pr);
        adapterProducts.setListener(BusinessFragment.this);
        list_rec.setAdapter(adapterProducts);
    }


    private void goingToFirst(){
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




    @Override
    public void onAddProduct(ProductModel product) {

        //product.save();

        ArrayList<ProductModel> productModels = UtilsHelper.getOrdersActualInCache();

        if(productModels.size() > 0) {
            if (product.id_negocio.equals(productModels.get(0).id_negocio)) {

                productModels.add(product);


                UtilsHelper.saveOrders(productModels);

               // Log.e("MAIN", "onAddProduct: " + product.title);
                adapterProducts.notifyDataSetChanged();
                UtilsHelper.editButtonOrder(getActivity());
            }else if(getContext() != null){
                MDToast.makeText(getContext(), getString(R.string.only_one_business), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            }
        }else{
            productModels.add(product);


            UtilsHelper.saveOrders(productModels);

            // Log.e("MAIN", "onAddProduct: " + product.title);
            adapterProducts.notifyDataSetChanged();
            UtilsHelper.editButtonOrder(getActivity());
        }
    }

    @Override
    public void onDeleteProduct(ProductModel prduct) {
        ArrayList<ProductModel> productsInCache = UtilsHelper.getOrdersActualInCache();


        for(int i=0; i < productsInCache.size(); i++){
            if(productsInCache.get(i).id_product.equals(prduct.id_product)){

                productsInCache.remove(i);
                break;
            }

        }

        UtilsHelper.saveOrders(productsInCache);

        adapterProducts.notifyDataSetChanged();

        UtilsHelper.editButtonOrder(getActivity());

    }
}
