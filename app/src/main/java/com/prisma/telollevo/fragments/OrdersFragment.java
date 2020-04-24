package com.prisma.telollevo.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.prisma.telollevo.DeliveryActivity;
import com.prisma.telollevo.MainActivity;
import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.ApiResources;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.adapter.AdapterOrder;
import com.prisma.telollevo.models.Order;
import com.google.android.material.tabs.TabLayout;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.daimajia.androidanimations.library.BaseViewAnimator.DURATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {


    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }


    private TabLayout tabLayout;
private TextView textView;
    private RecyclerView recyclerView;
    private ArrayList<Order> orders = new ArrayList<>();
    private AdapterOrder adapterOrder;

    @Override
    public void onDetach() {
        UtilsHelper.goToMain = false;
        if(UtilsHelper.isDelivery(MainActivity.preferences)){
            ((DeliveryActivity)getActivity()).unsetHotest();
        }
        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
     //       UtilsHelper.hideToolbar(getActivity());
        }catch (Exception e){
        //    UtilsHelper.hideToolbar2(getActivity());
        }
        tabLayout = view.findViewById(R.id.tablay);
        recyclerView = view.findViewById(R.id.rec_list);

        adapterOrder = new AdapterOrder((AppCompatActivity) getActivity(), orders);
        view.findViewById(R.id.back_buton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilsHelper.showToolbar(getActivity());
                getFragmentManager().popBackStack();
            }
        });
        view.findViewById(R.id.back_buton).setVisibility(View.GONE);
        UtilsHelper.goToMain = true;

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        textView = view.findViewById(R.id.empty_btt);

        setupTabLayout();

        getDataOrders();

    }

    private void getDataOrders()
    {
        textView.setVisibility(View.GONE);

        String idUser = String.valueOf(MainActivity.preferences.getInt(UtilsHelper.key_id_user, 0));


      //  idUser = "1";
        if(idUser.isEmpty()){
textView.setVisibility(View.VISIBLE);
recyclerView.setVisibility(View.GONE);
        }
        String urr = "";
        if(UtilsHelper.isDelivery(MainActivity.preferences)) {
            urr = ApiResources.getOrdersByDeliveryId(idUser);
        }else{
            urr = ApiResources.getOrderByUser(idUser);
        }

        Log.e("MAIN", "getDataOrders: "+urr );

        UtilsHelper.getArrayDataFrom(getActivity(), urr, new UtilsHelper.loadJson() {
            @Override
            public void onLoadSuccess(JSONArray array) {
              //  Log.e("MAIN", "onLoadSuccess: "+array.toString() );
                try {
                for(int i=0; i  < array.length(); i++){


                        JSONObject ob = array.getJSONObject(i);

                        Order order = new Order();

                        order.Descripcion = ob.getString("Descripcion");
                        order.Encargado = ob.getString("Encargado");
                        order.estado = ob.getString("Estado");
                        order.fecha = ob.getString("FechaOrden");
                        order.total = ob.getString("Monto");
                        order.imagen64 = ob.getString("imagen64");
                        order.orden_num = ob.getString("IdPedido");
                        order.nombreDelivery = ob.getString("NombreRepartidor");
                        order.nombreNegocio = ob.getString("NombreNegocio");
                        order.formaPago = ob.getString("FormaPago");
                    order.isRate = ob.getInt("Calificado") == 1;

                        orders.add(order);
                    Log.e("MAIN", "onLoadSuccess: "+order.estado);

                }
                    setupListPending();
//textView.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    Log.e("MAIN", "onLoadSuccess: "+e.getMessage() );
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String reason) {

                Toast.makeText(getContext(), getString(R.string.load_error), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.pending_order)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.history_order)));
tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        recyclerView.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        historyOrders.clear();
        pendingOrders.clear();
        if(tab.getPosition() == 0){
            setupListPending();
        }else{
        setupListHistory();
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
});


    }


    /** Configurar ordenes en history **/
    private ArrayList<Order> historyOrders = new ArrayList<>();
    private void setupListHistory() {

        for(int i =0;i < orders.size(); i++){
            if(orders.get(i).estado.equals("Terminado")){
                historyOrders.add(orders.get(i));
            }
        }

        if(historyOrders.size() > 0) {
            adapterOrder = new AdapterOrder((AppCompatActivity) getActivity(), historyOrders);

            recyclerView.setAdapter(adapterOrder);
            recyclerView.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceInLeft)
                    .duration(800)
                    .repeat(0)
                    .playOn(recyclerView);

        }else{
            textView.setVisibility(View.VISIBLE);
        }

    }


    /** Configurar ordenes en estado pendiente **/
    private ArrayList<Order> pendingOrders = new ArrayList<>();
    private void setupListPending() {
        pendingOrders.clear();

for(int i=0; i < orders.size(); i++){
    if(!orders.get(i).estado.equals("Terminado")){
        pendingOrders.add(orders.get(i));
    }
}

if(pendingOrders.size() > 0) {
    adapterOrder = new AdapterOrder((AppCompatActivity) getActivity(), pendingOrders);

    recyclerView.setAdapter(adapterOrder);
    recyclerView.setVisibility(View.VISIBLE);
    YoYo.with(Techniques.BounceInRight)
            .duration(800)
            .repeat(0)
            .playOn(recyclerView);

}else{
    textView.setVisibility(View.VISIBLE);
}

    }
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (UtilsHelper.isDelivery(MainActivity.preferences)) {
            if (enter) {
                return MoveAnimation.create(MoveAnimation.RIGHT, enter, DURATION);
            } else {
                return MoveAnimation.create(MoveAnimation.LEFT, enter, DURATION);
            }
        }else{
            return null;
        }
    }



}
