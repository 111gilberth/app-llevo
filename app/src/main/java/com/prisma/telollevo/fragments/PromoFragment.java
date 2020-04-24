package com.prisma.telollevo.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.ApiResources;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.adapter.AdapterProducts;
import com.prisma.telollevo.models.ProductModel;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PromoFragment extends Fragment implements UtilsHelper.ProductListener {


    public PromoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_promo, container, false);
    }

    private RecyclerView recyclerView;
    private SearchView searchView;
    private AdapterProducts adapterPromoBig;
    private ArrayList<ProductModel> promoteModels = new ArrayList<>();
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
if(getContext() == null){
    return;
}

recyclerView = view.findViewById(R.id.rec_list);

adapterPromoBig = new AdapterProducts((AppCompatActivity) getActivity(), promoteModels);

adapterPromoBig.setListener(this);

recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
recyclerView.setAdapter(adapterPromoBig);

        searchView = view.findViewById(R.id.search_view);

        // int linlayId = getResources().getIdentifier("android:id/search_plate", null, null);
        ViewGroup v = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        v.setBackgroundResource(R.drawable.search_back);

        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_search_icon));


getDataPromo();

    }

    JSONArray sizeOfPromo = null;
    private String TAG = "MAIN";
    private void getDataPromo() {

        UtilsHelper.getArrayDataFrom(getActivity(), ApiResources.getAllPromotions(), new UtilsHelper.loadJson() {
            @Override
            public void onLoadSuccess(JSONArray array) {
                sizeOfPromo = array;
                try {
                for(int i=0; i < sizeOfPromo.length(); i++){


                        JSONObject object = sizeOfPromo.getJSONObject(i);
                        ProductModel model = new ProductModel();

                        model.desc = object.getString("t_desc");
                        model.id_negocio = object.getString("t_idnegocio");
                        model.id_product = object.getString("t_id");
                        model.img_url = object.getString("t_url");
                        model.price = object.getString("t_prec");
                        model.title = object.getString("t_titu");

                        promoteModels.add(model);




                }

                adapterPromoBig.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "onLoadSuccess: "+e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String reason) {
                Log.e(TAG, "onError: "+reason);
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
                adapterPromoBig.notifyDataSetChanged();
                UtilsHelper.editButtonOrder(getActivity());
            }else if(getContext() != null){
                MDToast.makeText(getContext(), getString(R.string.only_one_business), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            }
        }else{
            productModels.add(product);


            UtilsHelper.saveOrders(productModels);

            // Log.e("MAIN", "onAddProduct: " + product.title);
            adapterPromoBig.notifyDataSetChanged();
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

        adapterPromoBig.notifyDataSetChanged();

        UtilsHelper.editButtonOrder(getActivity());

    }
}
