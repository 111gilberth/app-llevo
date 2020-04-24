package com.prisma.telollevo.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;

import com.prisma.telollevo.R;

public class PaymentMethodSelection extends AlertDialog {
    public PaymentMethodSelection(Context context) {
        super(context);
    }

    protected PaymentMethodSelection(Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface onSelectListener{
        void onSelected(String selected);
    }

    public void setListener(onSelectListener listener) {
        this.listener = listener;
    }

    private onSelectListener listener;

    private View pay_onhome, snipemov;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_select);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setBackgroundDrawable(ActivityCompat.getDrawable(getContext(), R.color.tran));

        snipemov = findViewById(R.id.snipe_movil);
        pay_onhome = findViewById(R.id.effective);


        snipemov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onSelected(getContext().getString(R.string.credit_option));
                    dismiss();
                }
            }
        });

        pay_onhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onSelected(getContext().getString(R.string.cash_option));
                    dismiss();
                }
            }
        });
    }
}
