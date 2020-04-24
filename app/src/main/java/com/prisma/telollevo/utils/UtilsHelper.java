package com.prisma.telollevo.utils;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dagf.dialoglibrary.dialog.LoadingDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.prisma.telollevo.DeliveryActivity;
import com.prisma.telollevo.MainActivity;
import com.prisma.telollevo.R;
import com.prisma.telollevo.fragments.HomeFragment;
import com.prisma.telollevo.models.Order;
import com.prisma.telollevo.models.ProductModel;
import com.facebook.login.LoginManager;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UtilsHelper {

    
    public static LoadingDialog getLoadingDialog(Activity c){
        
        LoadingDialog loadingDialog = new LoadingDialog(c);
        
        loadingDialog.setTextSucc(c.getString(R.string.load_success), c.getString(R.string.weready));
        
        loadingDialog.setTexts(c.getString(R.string.loading), c.getString(R.string.wait_loading), c.getString(R.string.title_error), c.getString(R.string.info_error));

        
        
        return loadingDialog;
        
    }

    public static void logOut(Activity a){
        SharedPreferences preferences = MainActivity.preferences;

        preferences.edit().putBoolean(key_login_status, false).commit();


        LoginManager.getInstance().logOut();

        Intent i = new Intent(a, MainActivity.class);

        a.startActivity(i);
        a.finish();

    }

    public static void saveNewToken(String s) {
        MainActivity.preferences.edit().putString(key_token_push, s).commit();
    }

    /**
     Esto es desde main
    **/

    public interface loadJson{
        void onLoadSuccess(JSONArray array);
        void onError(String reason);
    }
    public static void getArrayDataFrom(Context c, String url, final loadJson interf){
        RequestQueue queue = Volley.newRequestQueue(c);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = UtilsHelper.formatJSON(response);

                try {
                    JSONArray o = new JSONArray(response);

                    interf.onLoadSuccess(o);

                } catch (JSONException e) {
                    e.printStackTrace();
                    interf.onError(e.getMessage());
                    Log.e("MAIN", "onResponse: "+e.getMessage() );
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                interf.onError(error.getMessage());
                Log.e("MAIN", "onErrorResponse: "+error.getMessage());
            }
        });

        queue.add(request);

    }

    public static String formatJSON(String resp){
        resp = resp.substring(1, resp.length() - 1);
        resp = resp.replaceAll("\\\\", "");

        return resp;
    }


    public interface LoadJsonPost{
        void onLoadSuccess(JSONObject object);
        void onError(String reason);
    }

    public static final String key_token_push = "LKWQQPOEIKD";
    public static String getTokenId(){
        return MainActivity.preferences.getString(key_token_push, "nothing");
    }


    public static void getJSONPOST(final Context c, String url, final HashMap map, final LoadJsonPost inter){

        RequestQueue queue = Volley.newRequestQueue(c);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map) ,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //  Log.e("MAIN", "onResponse: "+response );

                Log.e("MAIN", "onResponse: " + response.toString());
                inter.onLoadSuccess(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                inter.onError(error.getMessage());
                Log.e("MAIN", "onErrorResponse: "+error.getMessage() );
                MDToast.makeText(c, c.getString(R.string.load_error), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            }
        });

        queue.add(request);

    }


    public static void getJSONGET(final Context c, String url, final LoadJsonPost inter){

        RequestQueue queue = Volley.newRequestQueue(c);
        StringRequest request = new StringRequest(Request.Method.GET, url ,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //  Log.e("MAIN", "onResponse: "+response );


                if(response.startsWith("\"")){
                  //  response = response.substring(1, response.length() - 1);
                    response = formatJSON(response);
                }

                Log.e("MAIN", "onResponse: " + response.toString());
                try {
                    JSONArray rr = new JSONArray(response);
                    inter.onLoadSuccess(rr.getJSONObject(0));
                } catch (JSONException e) {
                    inter.onError(e.getMessage());
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                inter.onError(error.getMessage());
                Log.e("MAIN", "onErrorResponse: "+error.getMessage() );
                MDToast.makeText(c, c.getString(R.string.load_error), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            }
        });

        queue.add(request);

    }


    public static final String key_phone = "JASDJASDJA";
    public static final String key_name = "ASADMMAMA";
    public static final String key_dir = "MDMWEWKW";
    public static final String key_login_status = "JAMMMMWW";
    public static final String key_account_type = "JLLWWWKQQ";
    public static final String key_id_user = "AALKLSS";

    public static boolean isDelivery(SharedPreferences preferences){
        return preferences.getInt(key_account_type, 1) == 0;
    }


    public static final String key_name_to = "jasjdajsww";
    public static final String key_email = "JAJAJMIM";

    public static void hideToolbar(Activity activity){


            final MainActivity am = (MainActivity) activity;

        YoYo.with(Techniques.FadeOut)
                .duration(800)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        am.nameT.setVisibility(View.GONE);

                        YoYo.with(Techniques.FadeInUp)
                                .duration(500)
                                .repeat(0)
                                .onEnd(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        am.backbtn.setVisibility(View.VISIBLE);

                                    }
                                })
                                .playOn(am.backbtn);

                        am.backbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(goToMain){
                                    showToolbar(am);

                                if(MainActivity.isInCarrito){
                                    am.getSupportFragmentManager().beginTransaction().replace(R.id.framm, new HomeFragment()).commitAllowingStateLoss();
                               MainActivity.isInCarrito = false;
                                }
                                }

                                am.getSupportFragmentManager().popBackStack();
                            }
                        });

                    }
                })
                .playOn(am.nameT);




    }


    public static void hideToolbar2(Activity activity){


        final DeliveryActivity am = (DeliveryActivity) activity;

        YoYo.with(Techniques.FadeOutUp)
                .duration(800)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {

                      //  am.toolbar.setVisibility(View.GONE);
                        if(goToMain) {
                            am.nameT.setVisibility(View.VISIBLE);

                            YoYo.with(Techniques.FadeInUp)
                                    .duration(500)
                                    .repeat(0)
                                    .onEnd(new YoYo.AnimatorCallback() {
                                        @Override
                                        public void call(Animator animator) {
                                            am.backbtn.setVisibility(View.VISIBLE);

                                        }
                                    })
                                    .playOn(am.backbtn);

                            am.backbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (goToMain) {
                                        showToolbar2(am);

                                        if (MainActivity.isInCarrito) {
                                            am.getSupportFragmentManager().beginTransaction().replace(R.id.framm, new HomeFragment()).commitAllowingStateLoss();
                                            MainActivity.isInCarrito = false;
                                        }
                                    }

                                    am.getSupportFragmentManager().popBackStack();
                                }
                            });
                        }

                    }
                })
                .playOn(am.nameT);




    }

    public static void showToolbar2(Activity activity){


        final DeliveryActivity am = (DeliveryActivity) activity;

        YoYo.with(Techniques.FadeIn)
                .duration(800)
                .repeat(0)
                .onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        am.nameT.setVisibility(View.VISIBLE);

                        YoYo.with(Techniques.FadeOutDown)
                                .duration(500)
                                .repeat(0)
                                .onEnd(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        am.backbtn.setVisibility(View.VISIBLE);

                                    }
                                })
                                .playOn(am.backbtn);

                    }
                })
                .playOn(am.nameT);


    }


    public static boolean goToMain = false;

    public static void showToolbar(Activity activity){


        final MainActivity am = (MainActivity) activity;

        YoYo.with(Techniques.FadeIn)
                .duration(800)
                .repeat(0)
                .onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        am.nameT.setVisibility(View.VISIBLE);

                        YoYo.with(Techniques.FadeOutDown)
                                .duration(500)
                                .repeat(0)
                                .onEnd(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        am.backbtn.setVisibility(View.VISIBLE);

                                    }
                                })
                                .playOn(am.backbtn);

                    }
                })
                .playOn(am.nameT);


    }

    public static void editButtonOrder(Activity activity){
        ArrayList<ProductModel> prr = getOrdersActualInCache();

        int quantity = 0;
        int price_total = 0;

        for(int i=0;i < prr.size(); i++){

            float rrf =  Float.parseFloat(prr.get(i).price);

            price_total += (int) rrf;

            quantity++;


        }


        final MainActivity am = (MainActivity) activity;


        am.text_total.setText(""+price_total);
am.text_quantity.setText(""+quantity);

if(quantity < 1){
    hideButtonOrder(activity);
}else{
    showButtonOrder(activity);
}

    }

    public static void showButtonOrder(Activity activity){
        final MainActivity am = (MainActivity) activity;

        if(am.card_carrito.getVisibility() != View.GONE){
            return;
        }

        YoYo.with(Techniques.FadeInUp)
                .duration(800)
                .repeat(0)
                .onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        am.card_carrito.setVisibility(View.VISIBLE);
                        am.text_quantity.setVisibility(View.VISIBLE);

                    }
                })
                .playOn(am.card_carrito);

    }

    public static void hideButtonOrder(Activity activity){
        final MainActivity am = (MainActivity) activity;

        if(am.card_carrito.getVisibility() != View.VISIBLE){
            return;
        }

        YoYo.with(Techniques.FadeOutDown)
                .duration(800)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        am.card_carrito.setVisibility(View.GONE);
                        am.text_quantity.setVisibility(View.GONE);

                    }
                })
                .playOn(am.card_carrito);
    }

    public static void showTabLay(Activity activity){
        final MainActivity am = (MainActivity) activity;

        if(am.bottomNavigationBar.getVisibility() != View.GONE){
            return;
        }

        YoYo.with(Techniques.BounceInUp)
                .duration(800)
                .repeat(0)
                .onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        am.bottomNavigationBar.setVisibility(View.VISIBLE);
                      //  am.text_quantity.setVisibility(View.VISIBLE);

                    }
                })
                .playOn(am.bottomNavigationBar);
    }

    public static void hideTabLay(Activity ac){
        final MainActivity am = (MainActivity) ac;

        if(am.bottomNavigationBar.getVisibility() != View.VISIBLE){
            return;
        }

        YoYo.with(Techniques.SlideOutDown)
                .duration(1000)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        am.bottomNavigationBar.setVisibility(View.GONE);
                      //  am.text_quantity.setVisibility(View.GONE);

                    }
                })
                .playOn(am.bottomNavigationBar);
    }

    public interface ProductListener{
        void onAddProduct(ProductModel product);

        void onDeleteProduct(ProductModel prduct);
    }

    public static final String key_products_inch = "KKWWLQQ";

    public static TinyDB tinyDB;

    public static ArrayList<ProductModel> getOrdersActualInCache(){

        ArrayList<ProductModel> rr = tinyDB.getListProductsInCache(key_products_inch, ProductModel.class);

        return rr;
    }

    public static void clearOrders(){
        ArrayList<ProductModel> productModels = new ArrayList<>();
        tinyDB.putListProducts(key_products_inch, productModels);
    }




    public static void saveOrders(ArrayList<ProductModel> productModels){
        tinyDB.putListProducts(key_products_inch, productModels);
    }

    public static String direction_actual = "";


    private static final String key_info1 = "JJWWOOO";

    private static final String key_info2 = "JJW863OO";
    private static final String key_info3 = "55JWWOOO";
    public static void saveDirInfo(String info1, String info2, String info3){
        tinyDB.putString(key_info1, info1);
        tinyDB.putString(key_info2, info2);
        tinyDB.putString(key_info3, info3);
    }

    private static final String key_paymethod ="çsdw2çç";

    public static String getPayMent(){
      return tinyDB.getString(key_paymethod);
    }

    public static void setPayMent(String pay){
        tinyDB.putString(key_paymethod, pay);
    }

    public static String getFullDir(Activity activity){
        String info1 = getInfoDir1();
        String info2 = getInfoDir2();
        String info3 = getInfoDir3();

        SharedPreferences pr = activity.getPreferences(Context.MODE_PRIVATE);

        String saveddr = pr.getString(key_dir, "nn");

        saveddr = info3;


        return saveddr;
    }

    public static String getInfoDir1(){
        return tinyDB.getString(key_info1);
    }

    public static String getInfoDir2(){
        return tinyDB.getString(key_info2);
    }
    public static String getInfoDir3(){
        return tinyDB.getString(key_info3);
    }




    public interface getOrderListener{
        void getting(Order order);
        void errorGet(String reason);
    }
    public static void getLastOrderRate(Context c, String user_id, final getOrderListener lis){
        getArrayDataFrom(c, ApiResources.getOrderByUser(user_id), new loadJson() {
            @Override
            public void onLoadSuccess(JSONArray array) {
                try {
                for(int i=0; i  < array.length(); i++){


                    JSONObject ob = null;

                        ob = array.getJSONObject(i);


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

                    if(!order.isRate && order.estado.equals("Terminado")) {
                        lis.getting(order);
                        break;
                    }
                    //  Log.e("MAIN", "onLoadSuccess: "+order.estado);

                }

                lis.errorGet("No hay");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(String reason) {
lis.errorGet(reason);
            }
        });
    }
}
