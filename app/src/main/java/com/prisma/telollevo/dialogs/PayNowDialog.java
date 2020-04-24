package com.prisma.telollevo.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.prisma.telollevo.MainActivity;
import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.ApiResources;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.models.ProductModel;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PayNowDialog extends AlertDialog {

    public PayNowDialog(@NonNull Context context) {
        super(context);
    }

    protected PayNowDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


public void setAllInfo(String pyt, String totalp, String direc, String prodcs){
        this.paymt = pyt;
        this.totalprice = totalp;
        this.direction = direc;
        this.productson = prodcs;
}

public void setMoreData(String id_negocio, String tipopagos){
this.tipopago =tipopagos;
this.id_negocio = id_negocio;
}

    private String paymt, totalprice, direction, productson, id_negocio, tipopago;
    private TextView pay_method, total, dir, product_names;
    private ArrayList<ProductModel> productModels;
    private View paynow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pay_dialog);
        if(paymt == null || paymt.isEmpty()){
            dismiss();
            return;
        }

        productModels = UtilsHelper.getOrdersActualInCache();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setBackgroundDrawable(ActivityCompat.getDrawable(getContext(), R.color.tran));

        pay_method = findViewById(R.id.payment_method);
        total = findViewById(R.id.total_topay);
        dir = findViewById(R.id.full_directy);
        product_names = findViewById(R.id.products_names);


        String methodp = getContext().getString(R.string.payselect)+": "+paymt;

        pay_method.setText(methodp);

        String dirs = getContext().getString(R.string.direction_ttl)+" "+direction;

        dir.setText(dirs);

        String totalpr = totalprice;

        total.setText(totalpr);

        String pr = getContext().getString(R.string.prodcc)+" "+productson;

        product_names.setText(pr);

        paynow = findViewById(R.id.paynow);

        paynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   MDToast t = MDToast.makeText(getContext(), "Felicitaciones, comprado!", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);

                t.show();*/

                try {
                    goPay();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("MAIN", "onClick: "+e.getMessage());
                }
            }
        });
    }

    private void goPay() throws Exception {

        String url = ApiResources.getUrlServer()+"pedidosapi";


       HashMap ke = new HashMap();

        String iduser = String.valueOf(MainActivity.preferences.getInt(UtilsHelper.key_id_user, 0));

        String tipopay = "";

        if(tipopago.equals(getContext().getString(R.string.to_home))){
            tipopay = "entrega";
        }else{
            tipopay = "local";
        }



        ke.put("idusuario", iduser);
        ke.put("idnegocio", id_negocio);
        ke.put("iddireccion", direction.replace("(Cambiar)", ""));
        ke.put("tipopago", tipopay);
        ke.put("formapago", paymt);
        ke.put("Token", UtilsHelper.getTokenId());



        JSONArray jsonArray = new JSONArray();

        for(int i=0; i < productModels.size(); i++){
            JSONObject object = new JSONObject();

            ProductModel pro = productModels.get(i);

            object.put("cantidad", 1);
            object.put("precio", pro.price);
            object.put("idarticulo", pro.id_product);
            object.put("descripcion", pro.title);


jsonArray.put(object);
        }

        ke.put("Detalle", jsonArray);


        RequestQueue queue = Volley.newRequestQueue(getContext());
          //  Log.e("MAIN", "loginManual: "+url + "\n" + new JSONObject(ke).toString() );
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(ke),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject  response) {
                //  Log.e("MAIN", "onResponse: "+response );
            //    response = response.substring(1, response.length() - 1);
             //   response = response.replaceAll("\\\\", "");
                //            Log.e("MAIN", "onResponse: " + response);
                //  response = response.replaceFirst("\"", "");

               // Log.e("MAIN", "onResponse: "+response );

                    try {
                    //JSONArray array = new JSONArray(response);


                    if(response.getString("MSJ").equals("ok")){
                        MDToast.makeText(getContext(), getContext().getString(R.string.ok_pay), MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
UtilsHelper.clearOrders();
                        dismiss();
                    }else{
                        MDToast.makeText(getContext(), getContext().getString(R.string.load_error), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    }


                    } catch (JSONException e) {
                        e.printStackTrace();
                          Log.e("MAIN", "onResponse: " + e.getMessage());
                        MDToast.makeText(getContext(), getContext().getString(R.string.load_error), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                    }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                   Log.e("MAIN", "onErrorResponse: "+error.getMessage() );
MDToast.makeText(getContext(), getContext().getString(R.string.load_error), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            }
        }){

        };

        queue.add(request);

    }
}
