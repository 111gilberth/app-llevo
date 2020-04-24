package com.prisma.telollevo.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prisma.telollevo.MainActivity;
import com.prisma.telollevo.R;
import com.prisma.telollevo.utils.UtilsHelper;
import com.prisma.telollevo.dialogs.UpdateDataDialog;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    private CircleImageView circleImageView;
    private TextView name_text;
    private View go_to;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        go_to = view.findViewById(R.id.go_to_con);
        name_text = view.findViewById(R.id.name_textview);
        circleImageView = view.findViewById(R.id.img_profile);

        String name = MainActivity.preferences.getString(UtilsHelper.key_name, "Ninguno");

        name_text.setText(name);


        go_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction tr = getActivity().getSupportFragmentManager().beginTransaction();

                tr.add(R.id.framm, new OrdersFragment());
                tr.addToBackStack(null);

                tr.commitAllowingStateLoss();
            }
        });

        if(already_buy){
            go_to.performClick();
            already_buy = false;
        }

        view.findViewById(R.id.go_change_data_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateDataDialog d = new UpdateDataDialog(getContext());

            d.show();
            }
        });


    }

    public static boolean already_buy = false;
}
