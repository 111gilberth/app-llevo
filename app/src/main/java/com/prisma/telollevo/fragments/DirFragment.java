package com.prisma.telollevo.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.prisma.telollevo.MainActivity;
import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.ApiResources;
import com.prisma.telollevo.utils.UtilsHelper;
import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.daimajia.androidanimations.library.BaseViewAnimator.DURATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class DirFragment extends Fragment {


    public DirFragment() {
        // Required empty public constructor
    }

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
        //UtilsHelper.showToolbar(getActivity());
        UtilsHelper.goToMain = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dir, container, false);
    }

    private EditText info1, info2, info3;
    private TextView dir;
    private RadioButton radioButton, radioButton2;
    private View ok_btun;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if(getActivity() == null){
            return;
        }

        UtilsHelper.goToMain = false;

        info3 = view.findViewById(R.id.info3);
        info1 = view.findViewById(R.id.info1);
        info2 = view.findViewById(R.id.info2);
        dir = view.findViewById(R.id.dir_selected);
        radioButton = view.findViewById(R.id.radioButton1);
        radioButton2 = view.findViewById(R.id.radioButton2);

        ok_btun = view.findViewById(R.id.ok_butn);


        configureSavedInfo();

        SharedPreferences pr = getActivity().getPreferences(Context.MODE_PRIVATE);


        final String dir_saved = pr.getString(UtilsHelper.key_dir, "ninguna");

        dir.setText(dir_saved);

        ok_btun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             try {



                if(!notIsEmpty()){
                    MDToast mdToast = MDToast.makeText(getContext(), getString(R.string.empty_info), MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                    mdToast.show();
                    return;
                }

                String findir = dir_saved + ", "+info1.getText().toString()+", "+info2.getText().toString()+", "+info3.getText().toString();

                saveAllInfo();

                UtilsHelper.direction_actual = findir;


                 ArrayList<Fragment> ff = (ArrayList<Fragment>) getFragmentManager().getFragments();

                 ShopingFragment s = (ShopingFragment) ff.get(0);

                 s.setFulldir();

                getFragmentManager().popBackStack();
             }catch (Exception e){

             }
            }
        });




        ConfigureSelection();
        getDirData();
    }

    private void configureSavedInfo() {

        if(!UtilsHelper.getInfoDir1().isEmpty()){
            info1.setText(UtilsHelper.getInfoDir1());
        }

        if(!UtilsHelper.getInfoDir2().isEmpty()){
            info2.setText(UtilsHelper.getInfoDir2());
        }

        if(!UtilsHelper.getInfoDir3().isEmpty()){
            info3.setText(UtilsHelper.getInfoDir3());
        }

    }

    private void saveAllInfo() {
        UtilsHelper.saveDirInfo(info2.getText().toString(), info2.getText().toString(), info3.getText().toString());
    }

    private void ConfigureSelection() {
        radioButton.setTextColor(getResources().getColor(R.color.primaryTextColor));
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioButton.setTextColor(getResources().getColor(R.color.primaryTextColor));
                radioButton2.setTextColor(getResources().getColor(R.color.obscure));
            }
        });

        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioButton2.setTextColor(getResources().getColor(R.color.primaryTextColor));
                radioButton.setTextColor(getResources().getColor(R.color.obscure));
            }
        });
    }

    public boolean notIsEmpty(){
        return /*!info1.getText().toString().isEmpty() && !info2.getText().toString().isEmpty() && */!info3.getText().toString().isEmpty();
    }

    public void getDirData(){

        String url = ApiResources.getUrlServer()+"placesapi?idusuario="+ MainActivity.preferences.getInt(UtilsHelper.key_id_user, 0);

        UtilsHelper.getJSONGET(getContext(), url, new UtilsHelper.LoadJsonPost() {
            @Override
            public void onLoadSuccess(JSONObject object) {
                //Log.e("MAIN", "onLoadss: "+object.toString() );
                try {
                    info3.setText(object.getString("Direccion"));
                } catch (JSONException e) {
                    Log.e("MAIN", "erno: "+e.getMessage() );
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String reason) {
                Log.e("MAIN", "onError: "+reason );
            }
        });
    }
}
