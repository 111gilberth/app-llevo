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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.adapter.AdapterBusiness;
import com.prisma.telollevo.models.Bussines;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchBusiFragment extends Fragment {


    @Override
    public void onDetach() {
        super.onDetach();
        UtilsHelper.goToMain = true;
    }

    public SearchBusiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_busi, container, false);
    }


    private RecyclerView recyclerView;
    private AdapterBusiness adapterBusiness;
    private SearchView searchView;
    public static ArrayList<Bussines> basines = new ArrayList<>();
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        view.findViewById(R.id.back_buton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        view.findViewById(R.id.back_buton).setVisibility(View.GONE);

        searchView = view.findViewById(R.id.search_buss);

        ViewGroup v = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        v.setBackgroundResource(R.drawable.search_back);

        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_search_icon));


        adapterBusiness = new AdapterBusiness((AppCompatActivity) getActivity(), basines);

        recyclerView = view.findViewById(R.id.rec_list);
        recyclerView.setAdapter(adapterBusiness);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filterSearch(newText);

                return true;
            }
        });
    }

    private void filterSearch(String searching) {
        ArrayList<Bussines> busfr = new ArrayList<>();

        String realser = searching.toLowerCase();

        for(int i=0; i < basines.size(); i++){
            if(basines.get(i).name_b.toLowerCase().contains(realser) || basines.get(i).giro.toLowerCase().contains(realser)){
                Bussines b = basines.get(i);
                busfr.add(b);
            }
        }


adapterBusiness.filterSearch(busfr);

    }
}
