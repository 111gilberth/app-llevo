package com.prisma.telollevo;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.prisma.telollevo.fragments.OrdersFragment;
import com.prisma.telollevo.utils.MyFirebaseInstanceIdService;
import com.prisma.telollevo.utils.TinyDB;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.dialogs.RateDialog;
import com.prisma.telollevo.fragments.HomeFragment;
import com.prisma.telollevo.fragments.ProfileFragment;
import com.prisma.telollevo.fragments.PromoFragment;
import com.prisma.telollevo.fragments.ShopingFragment;
import com.prisma.telollevo.login.LoginActivity;
import com.prisma.telollevo.models.Order;
import com.facebook.FacebookSdk;
import com.google.android.material.tabs.TabLayout;
import com.orm.SugarContext;

import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{



    public View loading_small, backbtn;
    public TextView loading_text;
    public LottieAnimationView animationLo;
    public TabLayout bottomNavigationBar;
    public View card_carrito;

    public TextView text_quantity;
    public TextView text_total;

    private Button ubicarme;
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
        setContentView(R.layout.activity_main);
        preferences = getPreferences(MODE_PRIVATE);

        if(!UtilsHelper.getTokenId().equals(MyFirebaseInstanceIdService.newToken)){
            UtilsHelper.saveNewToken(MyFirebaseInstanceIdService.newToken);
        }

        if(!preferences.getBoolean(UtilsHelper.key_login_status, false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        Log.e("MAIN", "onCreate: paso "+preferences.getBoolean(UtilsHelper.key_login_status, false) );
        if(UtilsHelper.isDelivery(preferences)){

            startActivity(new Intent(this, DeliveryActivity.class));
            finish();
            return;
        }

        SugarContext.init(this);
     setupViews();

        FacebookSdk.sdkInitialize(getApplicationContext());

        UtilsHelper.tinyDB = new TinyDB(this);


        findViewById(R.id.log_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilsHelper.logOut(MainActivity.this);
            }
        });

        toolbar.setTitle("");

        bottomNavigationBar = findViewById(R.id.tablay);
        setupBottomNavi();
        setSupportActionBar(toolbar);




        String id = String.valueOf(preferences.getInt(UtilsHelper.key_id_user, 0));
        UtilsHelper.getLastOrderRate(this, id, new UtilsHelper.getOrderListener() {
            @Override
            public void getting(Order order) {
                RateDialog rateDialog = new RateDialog(MainActivity.this);

                rateDialog.delivery_name = order.nombreDelivery;
                rateDialog.bussiname = order.nombreNegocio;
                rateDialog.id_pedido = order.orden_num;

                rateDialog.show();
            }

            @Override
            public void errorGet(String reason) {
                Log.e("MAIN", "errorGet: "+reason );
            }
        });



        setupUserInfo();



      loadFragment(new HomeFragment());


UtilsHelper.editButtonOrder(this);


        card_carrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInCarrito = true;
                loadFragmentShop(new ShopingFragment());
                UtilsHelper.hideToolbar(MainActivity.this);
                UtilsHelper.goToMain = true;
                UtilsHelper.hideTabLay(MainActivity.this);
            }
        });

        ubicarme=(Button)findViewById(R.id.ubicarme);

        ubicarme.setVisibility(View.GONE);

        ubicarme.setOnClickListener(new View.OnClickListener(){

            public void onClick(View arg0){
                Intent inten = new Intent(MainActivity.this,MainActivity.class);
                startActivity(inten);
            }
        });
    }


    private void setupUserInfo() {
        String name = preferences.getString(UtilsHelper.key_name, "nada");

        nameT = findViewById(R.id.namais);

        String complt = "Bienvenido "+name;

        nameT.setText(complt);
    }


    private void setupViews() {

        card_carrito = findViewById(R.id.carrito_go);
        text_quantity = findViewById(R.id.quantity_order);
        text_total = findViewById(R.id.total_order);
        toolbar = findViewById(R.id.toolbar);
        animationLo = findViewById(R.id.loading_anim_main);
        loading_text = findViewById(R.id.loading_text);
        backbtn = findViewById(R.id.back_main);
        loading_small = findViewById(R.id.loading_small);


    }

    public void loadFragment(Fragment f){
UtilsHelper.showToolbar(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.framm, f).commitAllowingStateLoss();

        }

    private void loadFragmentShop(Fragment f){

        getSupportFragmentManager().beginTransaction().replace(R.id.framm, f).commitAllowingStateLoss();

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


   public static boolean isInCarrito = false;
    private void setupBottomNavi() {
        TabLayout.Tab item = bottomNavigationBar.newTab();

        TabLayout.Tab item2 =bottomNavigationBar.newTab();

        TabLayout.Tab item3 = bottomNavigationBar.newTab();

        TabLayout.Tab item4 = bottomNavigationBar.newTab();



        bottomNavigationBar.addTab(item);
        bottomNavigationBar.addTab(item2);
        bottomNavigationBar.addTab(item3);
        bottomNavigationBar.addTab(item4);

      //  item.setText(R.string.for_you);
        item.setIcon(R.drawable.home);
        //item2.setText(R.string.offers);
        item2.setIcon(R.drawable.ic_offer);
        item3.setIcon(R.drawable.orders);
       // item3.setText(R.string.shopping_cart);
        //item4.setText(R.string.profile_user);

        item4.setIcon(R.drawable.profile_ic);
bottomNavigationBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()){
            case 0:
                loadFragment(new HomeFragment());
                break;
            case 1:
                loadFragment(new PromoFragment());
                break;
            case 2:
                loadFragment(new OrdersFragment());
                break;
            case 3:
                loadFragment(new ProfileFragment());
                break;


            default:
                loadFragment(new HomeFragment());
        }

        if(isInCarrito) {

        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        if(isInCarrito) {
            switch (tab.getPosition()) {
                case 0:
                    loadFragment(new HomeFragment());
                    break;
                case 1:
                    loadFragment(new PromoFragment());
                    break;
                case 2:
                    loadFragment(new ProfileFragment());
                    break;


                default:
                    loadFragment(new HomeFragment());
            }

            isInCarrito = false;
        }
    }
});


    }

}
