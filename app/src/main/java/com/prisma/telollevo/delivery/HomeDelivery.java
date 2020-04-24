package com.prisma.telollevo.delivery;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prisma.telollevo.DeliveryActivity;
import com.prisma.telollevo.MainActivity;
import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.ApiResources;
import com.prisma.telollevo.utils.UtilsHelper;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeDelivery extends Fragment {


    public HomeDelivery() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_delivery, container, false);
    }

    private TextView name_del, rate_del, ing_del;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name_del = view.findViewById(R.id.name_delivery);
        rate_del = view.findViewById(R.id.rate_delivery);
        ing_del = view.findViewById(R.id.ing_delivery);

        getDeliveryData();
    }

    private String TAG = "MAIN";
    private void getDeliveryData() {
        String id = String.valueOf(MainActivity.preferences.getInt(UtilsHelper.key_id_user, 0));



        String urr = ApiResources.getDeliveryInfo(id);

      //  Log.e("MAIN", "getDeliveryData: "+urr );

        UtilsHelper.getArrayDataFrom(getActivity(), urr, new UtilsHelper.loadJson() {
            @Override
            public void onLoadSuccess(JSONArray array) {

                try {
                //    Log.e(TAG, "onLoadSuccess: si "+array );
                    JSONObject obb = array.getJSONObject(0);

                 //   Log.e(TAG, "onLoadSuccess: no" );

                    name_del.setText(obb.getString("t_nomb"));

                    float rate1 = Float.parseFloat(obb.getString("t_amab"));
                    float rate2 = Float.parseFloat(obb.getString("t_pres"));
                    float rate3 = Float.parseFloat(obb.getString("t_serv"));

                    float ratt = calculateRateGeneral(rate1, rate2, rate3);

                    String rate_amb = "Calificación: \uD83C\uDF1F"+ratt+"/5.0";

                    rate_del.setText(rate_amb);
                    ing_del.setText(obb.getString("t_ingr"));

                    ((DeliveryActivity)getContext()).showCheckOk(getString(R.string.load_success));

                } catch (JSONException e) {
                    ((DeliveryActivity)getContext()).showCheckOk(getString(R.string.load_success));
                    Log.e("MAIN", "onLoadSuccess home delivery: "+e.getMessage() );
                }

            }

            @Override
            public void onError(String reason) {
                MDToast t = new MDToast(getContext());
                t.setText(getString(R.string.load_error));
                t.setType(MDToast.TYPE_WARNING);
                t.setDuration(MDToast.LENGTH_SHORT);
                t.show();
            }
        });


    }

    private float calculateRateGeneral(float rate1, float rate2, float rate3) {

        float media = rate1+rate2+rate3;

        return media / 3;


    }
}
