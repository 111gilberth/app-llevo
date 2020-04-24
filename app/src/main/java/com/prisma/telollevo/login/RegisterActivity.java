package com.prisma.telollevo.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {

    AutoCompleteTextView email, username, password, confirm_pass, dir, phone;

    private View login_btn;
    private LoadingDialog loadingDialog;
    private boolean isLoginFB;

    public static boolean isDelivery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loadingDialog = UtilsHelper.getLoadingDialog(this);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        if(getIntent().getExtras() != null){
            Bundle ex = getIntent().getExtras();

            username.setText(ex.getString(UtilsHelper.key_name_to));
            email.setText(ex.getString(UtilsHelper.key_email));
loadingDialog.setTextSucc(getString(R.string.ok_weneed), getString(R.string.we_need));
loadingDialog.show();
loadingDialog.loadListener().onCompleteLoad(true);
loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        loadingDialog = UtilsHelper.getLoadingDialog(RegisterActivity.this);
    }
});
        }

        login_btn = findViewById(R.id.login_btn);

        password = findViewById(R.id.password);
        confirm_pass = findViewById(R.id.confirm_password);
        phone = findViewById(R.id.phone);
        dir = findViewById(R.id.dir_user);

       login_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               RegisterNow();
           }
       });
    }

    private void RegisterNow() {

        if (!password.getText().toString().equals(confirm_pass.getText().toString())) {
            Toast.makeText(this, getString(R.string.confirm_notequ), Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.getText().toString().isEmpty() && isValidEmailAddress(email.getText().toString())) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
      return;
        }

        if(username.getText().toString().isEmpty()){
             Toast.makeText(this, getString(R.string.userempty), Toast.LENGTH_SHORT).show();
        return;
        }

        if(dir.getText().toString().isEmpty()){
            Toast.makeText(this, getString(R.string.dir_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        if(phone.getText().toString().isEmpty() || !isValidPhone(phone.getText().toString())){
            Toast.makeText(this, getString(R.string.phone_empty), Toast.LENGTH_SHORT).show();
            return;
        }


        registerRest();

    }

    private void registerRest() {

        String user = username.getText().toString();
        String passwordd = password.getText().toString();
        String phonn = phone.getText().toString();
        String emailc = email.getText().toString();
        String dir_us = dir.getText().toString().replace(" ", "");

        String url = ApiResources.getRegisterUrl(user, emailc, phonn, passwordd, dir_us);

        RequestQueue queue = Volley.newRequestQueue(this);
           // Log.e("MAIN", "register: "+url );
        if(url.contains(" ")){
            url = url.replace(" ", "%20");
        }
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               // Log.e("MAIN", "onResponse: " + response);
            /*    response = response.substring(1, response.length() - 1);
                response = response.replaceAll("\\\\", "");
             */   //        Log.e("MAIN", "onResponse: " + response);
                //  response = response.replaceFirst("\"", "");

                if(response.contains("Completado")) {
                 /*   JSONArray array = new JSONArray(response);

                    JSONObject o = array.getJSONObject(0);

                    String telf = o.getString("Telefono");
                    String names = o.getString("Nombre");
                    String dir = o.getString("Direccion");

                    SharedPreferences.Editor editor = MainActivity.preferences.edit();

                    editor.putString(UtilsHelper.key_phone, telf);
                    editor.putString(UtilsHelper.key_dir, dir);
                    editor.putString(UtilsHelper.key_name, names);
                    editor.putBoolean(UtilsHelper.key_login_status, true);
                    editor.commit();*/

                    loadingDialog.loadListener().onCompleteLoad(true);

                    loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        }
                    });

                }else{
                    if(response.startsWith("\"\\")){
                        response = response.replaceAll("\"", "");
                        response = response.replaceAll("\\\\", "");
                        Log.e("MAIN", "onResponse: "+response);
                    }
                    loadingDialog.setTexts("", "", "Ups!", response);
                    loadingDialog.loadListener().onCompleteLoad(false);
                    login_btn.setEnabled(true);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.loadListener().onCompleteLoad(false);
                Log.e("MAIN", "onErrorResponse: "+error.getMessage() );
                login_btn.setEnabled(true);
            }
        });

        queue.add(request);
        loadingDialog.show();


        login_btn.setEnabled(false);
    }


    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();

    }


    public boolean isValidPhone(String phon){
        return phon.length() > 7;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isDelivery = false;
    }
}
