package com.prisma.telollevo.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prisma.telollevo.DeliveryActivity;
import com.prisma.telollevo.MainActivity;
import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.ApiResources;
import com.prisma.telollevo.utils.UtilsHelper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private LoginButton loginButton;

    private EditText user_name;
    private EditText password;
    private View login_btn, loginlike;
    private CallbackManager callbackManager;
    private View register_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();

        View anims = findViewById(R.id.anim_delivery);
        View ic_main = findViewById(R.id.ic_main);

        loginlike = findViewById(R.id.login_like_dm);

            anims.setVisibility(View.GONE);
            ic_main.setVisibility(View.VISIBLE);

            loginlike.setVisibility(View.GONE);


        user_name = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);

        loginButton = findViewById(R.id.login_button);
        register_btn = findViewById(R.id.register_text);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        setupLoginFB();

        loginButton.setBackgroundResource(R.drawable.icon_fb);
        loginButton.setLoginText("");
        loginButton.setLogoutText("");
        loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        loginButton.setTextSize(50);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginManual();
            }
        });

    }

    private void setupLoginFB() {
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
             //   Log.e("MAIN", "onSuccess: yes");

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                               // Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    String email = object.getString("email");



                                    String name = object.getString("name"); // 01/31/1980 format

                               //     Log.e("MAIN", "onCompleted: "+name + " "+email);

                                    isValidFB(email, name);

                                  /*  Bundle bundle = new Bundle();

                                    bundle.putString(UtilsHelper.key_email, email);
                                    bundle.putString(UtilsHelper.key_name_to, name);

                                    Intent n = new Intent(LoginActivity.this, RegisterActivity.class);

                                    n.putExtras(bundle);

                                    startActivity(n);*/

                                } catch (JSONException e) {
                                    Log.e("MAIN", "onCompleted: "+e.getMessage());
                                    e.printStackTrace();
                                }
                                 }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email");
                request.setParameters(parameters);
                request.executeAsync();



                // App code
            }

            @Override
            public void onCancel() {
                // App code
                Log.e("MAIN", "onCancel: cancelado" );
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e("MAIN", "onError: "+exception.getMessage() );
                // App code
            }
        });
    }


    /** LOGIN MANUAL

     */
    private void loginManual(){



        if(user_name.getText().toString().isEmpty()){
            Toast.makeText(this, getString(R.string.userempty), Toast.LENGTH_SHORT).show();
        return;
        }

        if(password.getText().toString().isEmpty()){
            Toast.makeText(this, getString(R.string.passwrdempty), Toast.LENGTH_SHORT).show();
            return;
        }



        String user = user_name.getText().toString();
        String passwordd = password.getText().toString();

        String url = ApiResources.getLoginUrl(user, passwordd);

        RequestQueue queue = Volley.newRequestQueue(this);
    //    Log.e("MAIN", "loginManual: "+url );
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            response = UtilsHelper.formatJSON(response);
         //     Log.e("MAIN", "onResponse: " + response);
              //  response = response.replaceFirst("\"", "");

                try {
                    JSONArray array = new JSONArray(response);

                    JSONObject o = array.getJSONObject(0);

                    if(!o.getString("MSJ").contains("OK")) {

                       MDToast toast = MDToast.makeText(LoginActivity.this, o.getString("MSJ"), MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                        toast.show();
                        login_btn.setEnabled(true);
                        return;
                    }

                    String telf = o.getString("Telefono");
                    String names = o.getString("Nombre");
                    String dir = o.getString("Direccion");
                    int stt = o.getInt("EsCliente");
                    int id = o.getInt("IdUsuario");



                    SharedPreferences.Editor editor = MainActivity.preferences.edit();

                    editor.putString(UtilsHelper.key_phone, telf);
                    editor.putString(UtilsHelper.key_dir, dir);
                    editor.putInt(UtilsHelper.key_id_user, id);
                    editor.putInt(UtilsHelper.key_account_type, stt);
                    editor.putString(UtilsHelper.key_name, names);
                    editor.putBoolean(UtilsHelper.key_login_status, true);
                    editor.commit();

                       Log.e("MAIN", "onResponse: es repartidor "+id + "   "+o.toString() );

                    if(stt == 1) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        finish();
                    }else{

                        startActivity(new Intent(LoginActivity.this, DeliveryActivity.class));

                        finish();
                    }



                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, getString(R.string.load_error), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    login_btn.setEnabled(true);
                    Log.e("MAIN", "onResponse: "+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, getString(R.string.load_error), Toast.LENGTH_SHORT).show();
                Log.e("MAIN", "onErrorResponse: "+error.getMessage() );
                login_btn.setEnabled(true);
            }
        });

        queue.add(request);
      //  loadingDialog.show();


        login_btn.setEnabled(false);
    }


    /** REVISAR SI YA EXISTE UN USUARIO CON ESE FACEBOOK **/
    private void isValidFB(final String email, final String name){

        String url = ApiResources.getValidLoginFB();

        RequestQueue queue = Volley.newRequestQueue(this);
        //    Log.e("MAIN", "loginManual: "+url );
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
              //  Log.e("MAIN", "onResponse: "+response );
                response = response.substring(1, response.length() - 1);
                response = response.replaceAll("\\\\", "");
             //           Log.e("MAIN", "onResponse: " + response);
                //  response = response.replaceFirst("\"", "");

                if(response.equals("[]")){

                    Intent registerInte = new Intent(LoginActivity.this, RegisterActivity.class);

                    Bundle bandul = new Bundle();
                    bandul.putString(UtilsHelper.key_name_to, name);
                    bandul.putString(UtilsHelper.key_email, email);

                    registerInte.putExtras(bandul);

                    startActivity(registerInte);
                    finish();
                }else {
                    try {
                        JSONArray array = new JSONArray(response);

                        JSONObject o = array.getJSONObject(0);

                        if(!o.getString("MSJ").contains("OK")) {

                            MDToast toast = MDToast.makeText(LoginActivity.this, o.getString("MSJ"), MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                            toast.show();
                            login_btn.setEnabled(true);
                            return;
                        }

                        String telf = o.getString("Telefono");
                        String names = o.getString("Nombre");
                        String dir = o.getString("Direccion");
                        int stt = o.getInt("EsCliente");
                        int id = o.getInt("IdUsuario");



                        SharedPreferences.Editor editor = MainActivity.preferences.edit();

                        editor.putString(UtilsHelper.key_phone, telf);
                        editor.putString(UtilsHelper.key_dir, dir);
                        editor.putInt(UtilsHelper.key_id_user, id);
                        editor.putInt(UtilsHelper.key_account_type, stt);
                        editor.putString(UtilsHelper.key_name, names);
                        editor.putBoolean(UtilsHelper.key_login_status, true);
                        editor.commit();


                        Log.e("MAIN", "onResponse: es repartidor "+stt +" id "+id);
if(stt == 1) {
    startActivity(new Intent(LoginActivity.this, MainActivity.class));

    finish();
}else{

    startActivity(new Intent(LoginActivity.this, DeliveryActivity.class));

    finish();
}


                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, getString(R.string.load_error), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        login_btn.setEnabled(true);
                        Log.e("MAIN", "onResponse: " + e.getMessage());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, getString(R.string.load_error), Toast.LENGTH_SHORT).show();
                Log.e("MAIN", "onErrorResponse: "+error.getMessage() );
                login_btn.setEnabled(true);
            }
        }){
            @Override
            protected Map<String, String> getParams(){

                Map<String, String> ke = new HashMap<String, String>();

                ke.put("correo", email);

                return ke;
            }
        };

        queue.add(request);
        //loadingDialog.show();


        login_btn.setEnabled(false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
