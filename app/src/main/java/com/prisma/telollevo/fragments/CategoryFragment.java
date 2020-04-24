package com.prisma.telollevo.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.ApiResources;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.adapter.AdapterBusiness;
import com.prisma.telollevo.adapter.AdapterPromo;
import com.prisma.telollevo.models.Bussines;
import com.prisma.telollevo.models.PromoteModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {
    private SearchView searchView;

    private RecyclerView recyclerView;
    private RecyclerView promoList;
    private View filters;
    private AdapterBusiness adapterBusiness;
    public ArrayList<PromoteModel> promoteModels = new ArrayList<>();
    private AdapterPromo promoAdapter;
    private ArrayList<Bussines> bussinesArrayList = new ArrayList<>();
    public static final String key_id_cat = "JDASJDASD";
    private String id_cat;

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getContext() fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if(getArguments() != null){
            id_cat = getArguments().getString(key_id_cat);
        }

        view.findViewById(R.id.back_buton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//UtilsHelper.showToolbar(getActivity());
          getFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.back_buton).setVisibility(View.GONE);
        //view.setVisibility(View.GONE);


        adapterBusiness = new AdapterBusiness((AppCompatActivity) getActivity(), bussinesArrayList);
        promoAdapter = new AdapterPromo(getContext(), promoteModels);

        recyclerView = view.findViewById(R.id.rec_rest);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapterBusiness);



        promoList = view.findViewById(R.id.rec_promo);
        promoList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        promoList.setAdapter(promoAdapter);

        searchView = view.findViewById(R.id.search_buss);

        // int linlayId = getResources().getIdentifier("android:id/search_plate", null, null);
        ViewGroup v = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        v.setBackgroundResource(R.drawable.search_back);

        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_search_icon));


        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction tr = getActivity().getSupportFragmentManager().beginTransaction();

                SearchBusiFragment.basines = bussinesArrayList;

                tr.add(R.id.framm, new SearchBusiFragment());
                tr.addToBackStack(null);

                UtilsHelper.goToMain = false;

                tr.commitAllowingStateLoss();
            }
        });

        setupData();

    }

    private void setupData() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = ApiResources.getBusinessByCatId(id_cat);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = UtilsHelper.formatJSON(response);

                try {
                    JSONArray o = new JSONArray(response);

                    for(int i=0; i < o.length(); i++){
                        Bussines bussines = new Bussines();
                        JSONObject a = o.getJSONObject(i);

                        bussines.codig = a.getString("t_codi");
                        bussines.dir_bus = a.getString("t_direccion");
                        bussines.giro = a.getString("t_giro");
                        bussines.numero_cel = a.getString("t_no");
                        bussines.idne = a.getString("t_idne");
                        bussines.longitud = a.getString("t_long");
                        bussines.lat = a.getString("t_lati");
                        bussines.name_b = a.getString("t_nomb");
                        bussines.rate = a.getString("t_cal");
                        bussines.url_img = a.getString("imagen64");

                        bussinesArrayList.add(bussines);

                    }
                    adapterBusiness.notifyDataSetChanged();
                    setupPromos();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("MAIN", "onResponse: "+e.getMessage() );
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MAIN", "onErrorResponse: "+error.getMessage());
            }
        });

        queue.add(request);

    }

    private void setupPromos() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = ApiResources.getPromoteCat(id_cat);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = UtilsHelper.formatJSON(response);

                try {
                    JSONArray o = new JSONArray(response);

                    for(int i=0; i < o.length(); i++){
                        PromoteModel bussines = new PromoteModel();
                        JSONObject a = o.getJSONObject(i);

                        bussines.url_img = a.getString("t_url");
                        bussines.id_negocio = a.getString("t_idnegocio");
                        bussines.desc = a.getString("t_desc");

                        promoteModels.add(bussines);

                    }
                    promoAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("MAIN", "onResponse: "+e.getMessage() );
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MAIN", "onErrorResponse: "+error.getMessage());
            }
        });

        queue.add(request);

    }
}
