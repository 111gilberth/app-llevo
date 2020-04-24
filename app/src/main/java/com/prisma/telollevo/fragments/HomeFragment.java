package com.prisma.telollevo.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dagf.dialoglibrary.dialog.LoadingDialog;
import com.prisma.telollevo.MainActivity;
import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.ApiResources;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.adapter.HomePageAdapter;
import com.prisma.telollevo.models.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomePageAdapter adapter;
    private LoadingDialog loadingDialog;
    private ArrayList<Category> categories = new ArrayList<>();
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.list_items);

        adapter = new HomePageAdapter((AppCompatActivity) getActivity(), categories);

loadingDialog = UtilsHelper.getLoadingDialog(getActivity());

recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false));
recyclerView.setAdapter(adapter);

getCats();

    }


    private void getCats(){
        if(getContext() == null){
            return;
        }

        ((MainActivity)getContext()).showLoading(getString(R.string.wait_loading));



        String url = ApiResources.getCategoriesUrl();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        //    Log.e("MAIN", "loginManual: "+url );
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = UtilsHelper.formatJSON(response);
                //        Log.e("MAIN", "onResponse: " + response);
                //  response = response.replaceFirst("\"", "");

                try {
                    JSONArray array = new JSONArray(response);

                    for(int i=0; i < array.length(); i++){

                        Category cat = new Category();

                        JSONObject o = array.getJSONObject(i);

                        String title = o.getString("t_cate");
                        String desc = o.getString("t_desc");
                        String id = o.getString("t_id");
                        String urlimg = o.getString("t_url");

                        cat.title = title;
                        cat.desc = desc;
                        cat.id = id;
                        cat.url_img = urlimg;

                        categories.add(cat);


                    }
                    adapter.notifyDataSetChanged();
                    ((MainActivity)getContext()).showCheckOk(getString(R.string.load_success));


                } catch (JSONException e) {
                    ((MainActivity)getContext()).showCheckOk(getString(R.string.load_error));

                    e.printStackTrace();
                    Log.e("MAIN", "onResponse: "+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((MainActivity)getContext()).showCheckOk(getString(R.string.load_error));

                Log.e("MAIN", "onErrorResponse: "+error.getMessage() );
            }
        });

        queue.add(request);
     //   loadingDialog.show();

    }


}
