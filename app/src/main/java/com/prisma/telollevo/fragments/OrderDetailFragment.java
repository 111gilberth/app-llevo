package com.prisma.telollevo.fragments;


import android.animation.Animator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.zxing.common.StringUtils;
import com.prisma.telollevo.MainActivity;
import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.ApiResources;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.adapter.AdapterProducts;
import com.prisma.telollevo.dialogs.RateDialog;
import com.prisma.telollevo.models.Order;
import com.prisma.telollevo.models.ProductModel;
import com.hanks.library.AnimateCheckBox;
import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.internal.Util;

import static com.daimajia.androidanimations.library.BaseViewAnimator.DURATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderDetailFragment extends Fragment {


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if(enter) {
            return MoveAnimation.create(MoveAnimation.LEFT, enter, DURATION);
        }else{
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, DURATION);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(UtilsHelper.isDelivery(MainActivity.preferences))
            UtilsHelper.goToMain = false;
        else
            UtilsHelper.goToMain = true;
    }

    public OrderDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }


    private Order ordetail;

    public void setOrdetail(Order ss){
        this.ordetail = ss;
    }


    private TextView bussines_name, totalPay, direction_detail, pay_method, date_detail, num_order,
    name_delivery, order_status, pay_form;
    private RecyclerView recyclerView;
    private AdapterProducts adapterProducts;

    private View rate_btn, btn_expand, details_all, img_collapse, btn_change_status, img_forward_view, anim_loading;
    private boolean isOpen = true;
    private AnimateCheckBox preparing, pending, on_road, delivered, done;
    private View[] anims;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(ordetail == null){
            return;
        }
        if(UtilsHelper.isDelivery(MainActivity.preferences)) {
            UtilsHelper.goToMain = true;
            UtilsHelper.hideToolbar2(getActivity());

        }
        else{
            UtilsHelper.goToMain = false;
            UtilsHelper.hideToolbar(getActivity());
        }

        bussines_name = view.findViewById(R.id.bussi_name);
        recyclerView = view.findViewById(R.id.list_prd);
        img_forward_view = view.findViewById(R.id.img_forward_view);
        anim_loading = view.findViewById(R.id.loading_anim);
        details_all = view.findViewById(R.id.all_info);
        btn_change_status = view.findViewById(R.id.change_status);
        pay_form = view.findViewById(R.id.formaPago);

        isDelivery = UtilsHelper.isDelivery(MainActivity.preferences);
        if(UtilsHelper.isDelivery(MainActivity.preferences)){
            btn_change_status.setVisibility(View.VISIBLE);
        }

        anims = new View[5];

        anims[0] = view.findViewById(R.id.anim_check_1);
        anims[1] = view.findViewById(R.id.anim_check_2);
        anims[2] = view.findViewById(R.id.anim_check_3);
        anims[3] = view.findViewById(R.id.anim_check_4);
        anims[4] = view.findViewById(R.id.anim_check_5);

        pending = view.findViewById(R.id.pending_check);
        preparing = view.findViewById(R.id.preparing_check);
        on_road = view.findViewById(R.id.on_road_check);
        delivered = view.findViewById(R.id.delivered_check);
        done = view.findViewById(R.id.done_check);
        img_collapse = view.findViewById(R.id.img_collapse);

        btn_expand = view.findViewById(R.id.button_collapse);
        if(!ordetail.estado.equals("Terminado"))
        animDetails();



        if(!isDelivery){
            pending.setEnabled(false);
        preparing.setEnabled(false);
        on_road.setEnabled(false);
        delivered.setEnabled(false);
        done.setEnabled(false);
        }
        btn_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animDetails();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        totalPay = view.findViewById(R.id.total_price);
        direction_detail = view.findViewById(R.id.direction_detail);
        pay_method = view.findViewById(R.id.pay_method);
        date_detail = view.findViewById(R.id.date_detail);
        num_order = view.findViewById(R.id.num_order);
        name_delivery = view.findViewById(R.id.delivery_name);
        order_status = view.findViewById(R.id.order_state);
        rate_btn = view.findViewById(R.id.rate_delivery);

        adapterProducts = new AdapterProducts(getActivity(), productModels);
adapterProducts.isOrderDetail = true;
        recyclerView.setAdapter(adapterProducts);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(getActivity() == null){
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDataOrder();
                    }
                });
            }
        }, 500, 3000);

       // setupData();

        
        

    }

    private void animDetails() {



        if(isOpen) {
            img_collapse.setRotation(0);
            YoYo.with(Techniques.SlideOutUp)
                    .duration(800)
                    .repeat(0)
                    .onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {

                           /* YoYo.with(Techniques.SlideOutUp)
                                    .duration(800)
                                    .repeat(0)
                                    .playOn(btn_expand);*/
                        }
                    })
                    .onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            details_all.setVisibility(View.GONE);

                        }
                    })
                    .playOn(details_all);
        }else{
            img_collapse.setRotation(180);
            YoYo.with(Techniques.SlideInDown)
                    .duration(800)
                    .repeat(0)
                    .onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            details_all.setVisibility(View.VISIBLE);

                        }
                    })
                    .playOn(details_all);
        }
        isOpen = !isOpen;

    }

    private boolean isDelivery = false;
    private ArrayList<ProductModel> productModels = new ArrayList<>();

    private void getDataOrder() {

        String url = ApiResources.getUrlServer()+"pedidosdetalleapi?IdPedido="+ordetail.orden_num;
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONArray arr = new JSONArray(response);
                    JSONObject o = arr.getJSONObject(0);
                    ordetail.estado = o.getString("Estado");
                    ordetail.isRate = o.getInt("Calificado") == 1;
                    ordetail.fecha = o.getString("FechaOrden");
                    ordetail.total = o.getString("Monto");
                    ordetail.formaPago = o.getString("FormaPago");
                    ordetail.nombreNegocio = o.getString("NombreNegocio");
                    ordetail.nombreDelivery = o.getString("NombreRepartidor");
                    ordetail.imagen64 = o.getString("imagen64");
                    ordetail.Descripcion = o.getString("Descripcion");
                    ordetail.payform = o.getString("TipoPago");

                    if(productModels.size() < 1) {
                        JSONArray ar = o.getJSONArray("Detalle");

                        for (int i = 0; i < ar.length(); i++) {
                            JSONObject object = ar.getJSONObject(i);

                            ProductModel m = new ProductModel();

                            m.price = object.getString("Precio");
                            m.id_product = object.getString("IdArticulo");
                            m.id_negocio = o.getString("IdNegocio");
                            m.title = object.getString("Descripcion");
                            productModels.add(m);
                        }

                        adapterProducts.notifyDataSetChanged();
                    }
                    setupData();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("MAIN", "onResponse error: "+e.getMessage() );
                    if(getFragmentManager() != null)
                    getFragmentManager().popBackStack();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MAIN", "onErrorResponse: "+error.getMessage() );
                if(getFragmentManager() != null)
                    getFragmentManager().popBackStack();
                MDToast.makeText(getContext(), getContext().getString(R.string.load_error), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            }
        });

        queue.add(request);

    }

    private void setupData()
    {
        if(getContext() == null){
            return;
        }
        pending.setVisibility(View.VISIBLE);
        preparing.setVisibility(View.VISIBLE);
        on_road.setVisibility(View.VISIBLE);
        delivered.setVisibility(View.VISIBLE);
        done.setVisibility(View.VISIBLE);

        bussines_name.setText(ordetail.nombreNegocio);
    //    all_products.setText(ordetail.Descripcion);
        totalPay.setText(ordetail.total);
        //direction_detail.setText(ordetail.);
        pay_method.setText(ordetail.formaPago);
        date_detail.setText(ordetail.fecha);
        num_order.setText("#"+ordetail.orden_num);
        name_delivery.setText(ordetail.nombreDelivery);
        pay_form.setText(ordetail.payform);

String newss = "Terminado";
        switch (ordetail.estado){
            case "Pendiente":
                newss = "Preparacion";
                break;
            case "Preparacion":
                newss = "En Camino";
                break;


            case "En Camino":
                newss = "Entregado";
                break;

            case "Entregado":
                newss = "Terminado";
                break;

            case "Terminado":

                break;
        }
        newss = getString(R.string.goto_status)+": "+newss;
        order_status.setText(newss);
        if(ordetail.estado.equals("Terminado")){
         //   animDetails();
        //    order_status.setTextColor(getContext().getResources().getColor(R.color.primaryLightColor));
          if(!isDelivery) {
              rate_btn.setVisibility(View.VISIBLE);

              rate_btn.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {

                      if (getContext() == null) {
                          return;
                      }

                      RateDialog r = new RateDialog(getContext());

                      r.delivery_name = ordetail.nombreDelivery;
                      r.bussiname = ordetail.nombreNegocio;

                      r.show();
                  }
              });
          }else
            btn_change_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }else{
            btn_change_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
changeStatus();
                }
            });
        }


        switch (ordetail.estado){
            case "Pendiente":
                pending.setChecked(true);
                pending.setVisibility(View.GONE);
                pending.setCircleColor(getContext().getResources().getColor(R.color.primaryLightColor));
                preparing.setChecked(false);
                on_road.setChecked(false);
                showSpeceficAnim(0);
                delivered.setChecked(false);
                done.setChecked(false);
                break;
            case "Preparacion":
pending.setChecked(true);
pending.setCircleColor(getContext().getResources().getColor(R.color.green));
preparing.setChecked(true);
                showSpeceficAnim(1);
preparing.setCircleColor(getContext().getResources().getColor(R.color.primaryLightColor));
                on_road.setChecked(false);
                delivered.setChecked(false);
                preparing.setVisibility(View.GONE);
                done.setChecked(false);
                break;


            case "En Camino":
                showSpeceficAnim(2);
                pending.setChecked(true);
                pending.setCircleColor(getContext().getResources().getColor(R.color.green));
                preparing.setChecked(true);
                preparing.setCircleColor(getContext().getResources().getColor(R.color.green));
                on_road.setChecked(true);
                on_road.setVisibility(View.GONE);
                on_road.setCircleColor(getContext().getResources().getColor(R.color.primaryLightColor));
                delivered.setChecked(false);
                done.setChecked(false);
                break;

            case "Entregado":
                showSpeceficAnim(3);
                pending.setChecked(true);
                pending.setCircleColor(getContext().getResources().getColor(R.color.green));
                preparing.setChecked(true);
                preparing.setCircleColor(getContext().getResources().getColor(R.color.green));
                on_road.setChecked(true);
                on_road.setCircleColor(getContext().getResources().getColor(R.color.green));
                delivered.setChecked(true);
                delivered.setVisibility(View.GONE);
                delivered.setCircleColor(getContext().getResources().getColor(R.color.primaryLightColor));
                done.setChecked(false);
                break;

            case "Terminado":
                showSpeceficAnim(5);
                pending.setChecked(true);
                pending.setCircleColor(getContext().getResources().getColor(R.color.green));
                preparing.setChecked(true);
                preparing.setCircleColor(getContext().getResources().getColor(R.color.green));
                on_road.setChecked(true);
                on_road.setCircleColor(getContext().getResources().getColor(R.color.green));
                delivered.setChecked(true);
                delivered.setCircleColor(getContext().getResources().getColor(R.color.green));
                done.setChecked(true);
                done.setCircleColor(getContext().getResources().getColor(R.color.green));
                break;
        }
    }


    private void showSpeceficAnim(int pos){
        for(int i=0; i < anims.length; i++){

                anims[i].setVisibility(View.GONE);
        }

        if(pos < anims.length)
        anims[pos].setVisibility(View.VISIBLE);
    }


    private void changeStatus(){
        btn_change_status.setEnabled(false);
        img_forward_view.setVisibility(View.GONE);
        anim_loading.setVisibility(View.VISIBLE);
        String urr = ApiResources.getUrlServer()+"EstadoPedidosapi";

        String newStatus = "";

        switch (ordetail.estado){
            case "Pendiente":
              newStatus = "Preparacion";
                break;
            case "Preparacion":
              newStatus = "En Camino";
                break;


            case "En Camino":
               newStatus = "Entregado";
                break;

            case "Entregado":
               newStatus = "Terminado";
                break;

            case "Terminado":

                break;
        }

        HashMap gg = new HashMap();

        gg.put("Estado", newStatus);
        gg.put("Idpedido", ordetail.orden_num);
        gg.put("Token", UtilsHelper.getTokenId());

        UtilsHelper.getJSONPOST(getContext(), urr, gg, new UtilsHelper.LoadJsonPost() {
            @Override
            public void onLoadSuccess(JSONObject object) {
                img_forward_view.setVisibility(View.VISIBLE);
                anim_loading.setVisibility(View.GONE);
                btn_change_status.setEnabled(true);
            //   Log.e("MAIN", "changed: "+object.toString() +" - "+ (img_forward_view.getVisibility() == View.VISIBLE ));


            }

            @Override
            public void onError(String reason) {
                img_forward_view.setVisibility(View.VISIBLE);
                anim_loading.setVisibility(View.GONE);
                btn_change_status.setEnabled(true);
            //    Log.e("MAIN", "onError: "+reason );



            }
        });




        // Log.e("MAIN", "changeStatus: "+newStatus );
    }
}
