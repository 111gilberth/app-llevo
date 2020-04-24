package com.prisma.telollevo.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.ApiResources;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.HashMap;
import java.util.Map;

public class RateDialog extends AlertDialog {

    public RateDialog(@NonNull Context context) {
        super(context);
    }

    public String delivery_name = "";
    public String bussiname = "";
    public String id_pedido = "";

    private TextView ttl;
    private RatingBar ratingBar, ratingService, ratingPresent, ratingPresentC, ratingSauce;
    private float generalRate = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_delivery_dialog);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setBackgroundDrawable(ActivityCompat.getDrawable(getContext(), R.color.tran));

        View okbtn = findViewById(R.id.rate_btn);
        View cancelbtn = findViewById(R.id.cancel_btn);

        ttl = findViewById(R.id.title_rate_dialog);
        TextView v = findViewById(R.id.title_rate_buss);
        String titlewes = getContext().getString(R.string.rating_tittl)+ " "+bussiname;
        v.setText(titlewes);

        ratingBar = findViewById(R.id.rating_bar);
        ratingService = findViewById(R.id.rating_service);
        ratingPresent = findViewById(R.id.rating_present);
        ratingSauce = findViewById(R.id.rating_sauce);
        ratingPresentC = findViewById(R.id.rating_presentc);
ratingBar.setMax(5);
ratingBar.setNumStars(5);
        ratingService.setMax(5);
        ratingService.setNumStars(5);
        ratingPresent.setMax(5);
        ratingPresent.setNumStars(5);

        ratingSauce.setMax(5);
        ratingSauce.setNumStars(5);

        ratingPresentC.setMax(5);
        ratingPresentC.setNumStars(5);

        String titlewe = getContext().getString(R.string.rating_tittl)+ " "+delivery_name;
        ttl.setText(titlewe);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                //pushRate(v);
              //
               //dismiss();
                calculateRate();
            }
        });
        ratingPresent.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                calculateRate();
            }
        });
        ratingService.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                calculateRate();
            }
        });
        ratingBar.setRating(3);
        ratingService.setRating(3);
        ratingPresent.setRating(3);
        ratingSauce.setRating(3);
        ratingPresentC.setRating(3);

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushRate(generalRate);
            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void calculateRate(){
      float rating =  ratingBar.getRating() + ratingService.getRating() + ratingPresent.getRating();

    //    Log.e("MAIN", "calculateRate: "+rating );
      rating = rating / 3;

      generalRate = rating;

    }

    private void pushRate(float v) {
        if(id_pedido.isEmpty()){
            //MDToast.makeText(getContext(), "Calificado a "+v).show();
            return;
        }

        dismiss();

        String url = ApiResources.getUrlServer()+"calificarpedidosapi";
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //  Log.e("MAIN", "onResponse: "+response );
                response = response.substring(1, response.length() - 1);
                response = response.replaceAll("\\\\", "");
                            Log.e("MAIN", "onResponse: " + response);
                //  response = response.replaceFirst("\"", "");

                if(response.equals("MSJ")){
                    MDToast.makeText(getContext(), getContext().getString(R.string.rated)+" "+delivery_name, MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
dismiss();
return;
                }else{
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
            @Override
            protected Map<String, String> getParams(){

                Map<String, String> ke = new HashMap<String, String>();


                ke.put("IdPedido", id_pedido);
                ke.put("Amabilidad", String.valueOf(ratingBar.getRating()));
                ke.put("Servicio", String.valueOf(ratingService.getRating()));
                ke.put("Presentacion", String.valueOf(ratingPresent.getRating()));
                ke.put("Sabor", String.valueOf(ratingSauce.getRating()));
                ke.put("PresentacionC", String.valueOf(ratingPresentC.getRating()));
              //  Log.e("MAIN", "getParams: "+String.valueOf(ratingSauce.getRating()));
                return ke;
            }
        };

        queue.add(request);

    }
}
