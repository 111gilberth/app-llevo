package com.prisma.telollevo.fragments;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.RadioButton;
import android.widget.TextView;

import com.prisma.telollevo.MainActivity;
import com.prisma.telollevo.R;
import com.prisma.telollevo.dialogs.PayNowDialog;
import com.prisma.telollevo.dialogs.PaymentMethodSelection;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.adapter.AdapterProducts;
import com.prisma.telollevo.models.ProductModel;
import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;

import static com.daimajia.androidanimations.library.BaseViewAnimator.DURATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShopingFragment extends Fragment implements UtilsHelper.ProductListener {


    public ShopingFragment() {
        // Required empty public constructor
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if(enter) {
            return MoveAnimation.create(MoveAnimation.UP, enter, DURATION);
        }else{
            return MoveAnimation.create(MoveAnimation.DOWN, enter, DURATION);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        UtilsHelper.showTabLay(getActivity());
        UtilsHelper.showToolbar(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shoping, container, false);
    }


    private TextView empty, direc_full, pay_text;
    private RecyclerView recyclerView;
    private View pay_method, direct_select;

    private View paynow;
    private RadioButton to_home, to_market;

    private AdapterProducts adapterProducts;
    private ArrayList<ProductModel> productModels = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        empty = view.findViewById(R.id.empty_fam);

        recyclerView = view.findViewById(R.id.list_items);

        pay_method = view.findViewById(R.id.select_payment_method);
        pay_text = view.findViewById(R.id.paywhat);
        direct_select = view.findViewById(R.id.select_direction);
        direc_full = view.findViewById(R.id.full_directy);

        if(getActivity() != null && !UtilsHelper.getFullDir(getActivity()).isEmpty()){
            direc_full.setText(UtilsHelper.getFullDir(getActivity())+" (Cambiar)");
        }


        paynow = view.findViewById(R.id.pay_now);
        to_home = view.findViewById(R.id.radioButton1);
        to_market = view.findViewById(R.id.radioButton2);


        productModels = UtilsHelper.getOrdersActualInCache();

        if(productModels.size() > 0){
            empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            paynow.setVisibility(View.VISIBLE);


            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            adapterProducts = new AdapterProducts(getActivity(), productModels);

            adapterProducts.isShoping = true;

            adapterProducts.setListener(this);

            recyclerView.setAdapter(adapterProducts);

            direct_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction tr = getFragmentManager().beginTransaction();


                    DirFragment fb = new DirFragment();


                    tr.add(R.id.framm, fb);
                    tr.addToBackStack(null);

                    tr.commitAllowingStateLoss();
                  //  UtilsHelper.hideToolbar(getActivity());
                }
            });

            pay_method.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PaymentMethodSelection p = new PaymentMethodSelection(getContext());

                    p.setListener(new PaymentMethodSelection.onSelectListener() {
                        @Override
                        public void onSelected(String selected) {
                            UtilsHelper.setPayMent(selected);
pay_text.setText(selected);
                        }
                    });

                p.show();
                }
            });

            paynow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToPayment();
                }
            });

        }



        to_home.setTextColor(getResources().getColor(R.color.primaryTextColor));
        to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                to_home.setTextColor(getResources().getColor(R.color.primaryTextColor));
                to_market.setTextColor(getResources().getColor(R.color.obscure));
            }
        });

        to_market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                to_market.setTextColor(getResources().getColor(R.color.primaryTextColor));
                to_home.setTextColor(getResources().getColor(R.color.obscure));
            }
        });

        UtilsHelper.showToolbar(getActivity());

    }

    private void goToPayment() {
        MDToast toast = null;

        if(pay_text.getText().toString().equals(getString(R.string.select_payment_method))){
toast = MDToast.makeText(getContext(), getString(R.string.must_select_paymethod), MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING);
            toast.show();
            return;
        }

        if(direc_full.getText().toString().equals(getString(R.string.select_dir))){

            toast = MDToast.makeText(getContext(), getString(R.string.must_select_direction), MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING);
            toast.show();
            return;
        }

        float totalpric = 0;
        String product_names = "";
        String paymth = pay_text.getText().toString();
        String ufldir = direc_full.getText().toString();
        for(int i=0; i < productModels.size(); i++){
            totalpric += Float.parseFloat(productModels.get(i).price);
            if(i == 0){
                product_names = productModels.get(i).title;
            }else{
                product_names = product_names + ", " + productModels.get(i).title;
            }
        }

        PayNowDialog payNowDialog = new PayNowDialog(getContext());

        payNowDialog.setAllInfo(paymth, String.valueOf(totalpric), ufldir.replace(" (Cambiar)", ""), product_names);

        String tipopay = to_home.isChecked() ? getString(R.string.to_home) : getString(R.string.to_market);

        payNowDialog.setMoreData(productModels.get(0).id_negocio, tipopay);


        payNowDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(UtilsHelper.getOrdersActualInCache().size() < 1){
                    MainActivity a = (MainActivity) getActivity();

                    if(a != null) {
                      //  a.backbtn.performClick();
                        ProfileFragment.already_buy = true;
                        a.loadFragment(new ProfileFragment());
                        if(a.bottomNavigationBar.getTabCount() > 2) {
                            a.bottomNavigationBar.getTabAt(2).select();
                        }
                        }

                    UtilsHelper.editButtonOrder(getActivity());

                }
            }
        });

        payNowDialog.show();

    }


    @Override
    public void onAddProduct(ProductModel product) {

        //product.save();

     /*   ArrayList<ProductModel> productModels = UtilsHelper.getOrdersActualInCache();

        productModels.add(product);


        UtilsHelper.saveOrders(productModels);

       // Log.e("MAIN", "onAddProduct: "+product.title );
        adapterProducts.notifyDataSetChanged();
        UtilsHelper.editButtonOrder(getActivity());*/
    }

    @Override
    public void onDeleteProduct(ProductModel prduct) {
        productModels = UtilsHelper.getOrdersActualInCache();


        for(int i=0; i < productModels.size(); i++){
            if(productModels.get(i).id_product.equals(prduct.id_product)){

                productModels.remove(i);

                break;
            }

        }

        UtilsHelper.saveOrders(productModels);

        productModels = UtilsHelper.getOrdersActualInCache();

        adapterProducts.notifyChanged(productModels);

       // Log.e("MAIN", "SIZE "+productModels.size());


        if(productModels.size() < 1){

            empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            paynow.setVisibility(View.GONE);

            MainActivity a = (MainActivity) getActivity();

            if(a != null)
            a.backbtn.performClick();


        }

        UtilsHelper.editButtonOrder(getActivity());

    }


    public void setFulldir()
    {
        if(getActivity() != null)
direc_full.setText(UtilsHelper.getFullDir(getActivity())+" (Cambiar)");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("MAIN", "onPause: paused ye");
    }
}
