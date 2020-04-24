package com.prisma.telollevo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.prisma.telollevo.utils.TinyDB;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.delivery.HomeDelivery;
import com.prisma.telollevo.fragments.OrdersFragment;
import com.prisma.telollevo.login.LoginActivity;
import com.facebook.FacebookSdk;
import com.orm.SugarContext;

import java.util.Timer;
import java.util.TimerTask;

public class DeliveryActivity extends AppCompatActivity {


    public View loading_small, backbtn, orders_btn, orders_detail;
    public TextView loading_text;
    public LottieAnimationView animationLo;


    public void showLoading(String text){

        loading_small.setVisibility(View.VISIBLE);
        loading_text.setText(text);
    }

    public void showCheckOk(String oktext){
        animationLo.setAnimation("success.json");
        animationLo.loop(false);
        animationLo.playAnimation();
        loading_text.setText(oktext);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animationLo.addAnimatorListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                loading_small.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });

                    }
                });

            }
        }, 500);
    }

    public static SharedPreferences preferences;
    public Toolbar toolbar;
    public TextView nameT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        SugarContext.init(this);
        setupViews();

        FacebookSdk.sdkInitialize(getApplicationContext());

        UtilsHelper.tinyDB = new TinyDB(this);


        findViewById(R.id.log_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilsHelper.logOut(DeliveryActivity.this);
            }
        });

        orders_btn = findViewById(R.id.orders);

        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        preferences = MainActivity.preferences;//getPreferences(MODE_PRIVATE);


        if(!preferences.getBoolean(UtilsHelper.key_login_status, false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        setupUserInfo();



        loadFragment(new HomeDelivery());

        orders_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isOrdering) {
                    FragmentTransaction tr = getSupportFragmentManager().beginTransaction();

                    setHotest();

                    tr.add(R.id.framm, new OrdersFragment());
                    //   tr.addToBackStack(null);


                    TextView text_order = findViewById(R.id.text_order);

                    text_order.setText(getString(R.string.back_tohome));

                    ImageView img = findViewById(R.id.arrow_order);

                    img.setRotation(0);

                    tr.commitAllowingStateLoss();
                    isOrdering = true;
                }else{
                    loadFragment(new HomeDelivery());

unsetHotest();

                    TextView text_order = findViewById(R.id.text_order);

                    text_order.setText(getString(R.string.orders_ttl));

                    ImageView img = findViewById(R.id.arrow_order);

                    img.setRotation(180);
                    isOrdering = false;
                }
            }
        });

    }

    private boolean isOrdering = false;
    private void setupUserInfo() {
        String name = preferences.getString(UtilsHelper.key_name, "nada");

        nameT = findViewById(R.id.namais);

        String complt = "Bienvenido "+name;

        nameT.setText(complt);
    }


    private void setupViews() {

        toolbar = findViewById(R.id.toolbar);
        animationLo = findViewById(R.id.loading_anim_main);
        loading_text = findViewById(R.id.loading_text);
        backbtn = findViewById(R.id.back_main);
        loading_small = findViewById(R.id.loading_small);


    }

    private void loadFragment(Fragment f){

        getSupportFragmentManager().beginTransaction().replace(R.id.framm, f).commitAllowingStateLoss();

    }

    private void loadFragmentShop(Fragment f){

        getSupportFragmentManager().beginTransaction().replace(R.id.framm, f).commitAllowingStateLoss();

    }

    public void setHotest(){
        FrameLayout jsw = findViewById(R.id.framm);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        layoutParams.setMargins(0, toolbar.getHeight(), 0, 0);

        toolbar.setBackground(getResources().getDrawable(R.drawable.white_shadow_backdraw));
        jsw.setLayoutParams(layoutParams);

       //toolbar.setVisibility(View.GONE);

    }

    public void unsetHotest(){
        FrameLayout jsw = findViewById(R.id.framm);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        layoutParams.setMargins(0, 0, 0, 0);

        jsw.setLayoutParams(layoutParams);
        toolbar.setBackground(ContextCompat.getDrawable(this, R.color.transparent));
        //toolbar.setVisibility(View.VISIBLE);
        Log.e("MAIN", "unsetHotest: is visible" );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




}
