package com.prisma.telollevo.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

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
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UpdateDataDialog extends AlertDialog {
    public UpdateDataDialog(@NonNull Context context) {
        super(context);
    }

    protected UpdateDataDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    private EditText change_name, change_number, change_password, confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_data_dialog);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setBackgroundDrawable(ActivityCompat.getDrawable(getContext(), R.color.tran));


        change_name = findViewById(R.id.name_textview);
        change_number = findViewById(R.id.change_number);
        change_password = findViewById(R.id.change_password);
        confirm_password = findViewById(R.id.change_password_confirm);

        findViewById(R.id.paynow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!change_password.getText().toString().isEmpty()){

                    if(!change_password.getText().toString().equals(confirm_password.getText().toString())) {
                        MDToast.makeText(getContext(), getContext().getString(R.string.confirm_notequ), MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();

                        return;
                    }

                }

                updateData();

            }
        });

    }

    private void updateData() {
        HashMap ke = new HashMap();
        RequestQueue queue = Volley.newRequestQueue(getContext());

        if(!change_name.getText().toString().isEmpty())
        ke.put("nombre", change_name.getText().toString());
        if(!change_number.getText().toString().isEmpty())
            ke.put("telefono", change_number.getText().toString());
        if(!change_password.getText().toString().isEmpty())
            ke.put("clave", change_password.getText().toString());

        String iduser = String.valueOf(MainActivity.preferences.getInt(UtilsHelper.key_id_user, 0));

        ke.put("idusuario", iduser);

        String url = ApiResources.getUrlServer()+"actualizaloginapi?";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(ke),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject  response) {
                try {


                    if(response.getString("MSJ").equals("OK")){
                        MDToast.makeText(getContext(), getContext().getString(R.string.ok_update), MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                        //UtilsHelper.clearOrders();
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

        if(!change_password.getText().toString().isEmpty() || !change_number.getText().toString().isEmpty() || !change_name.getText().toString().isEmpty())
        queue.add(request);
else{
            MDToast.makeText(getContext(), getContext().getString(R.string.empty_info), MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
        }

    }
}
