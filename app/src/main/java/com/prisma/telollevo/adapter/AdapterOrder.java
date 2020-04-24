package com.prisma.telollevo.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.prisma.telollevo.R;
import com.prisma.telollevo.dialogs.RateDialog;
import com.prisma.telollevo.fragments.OrderDetailFragment;
import com.prisma.telollevo.models.Order;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.OrderViewHolder> {

    private AppCompatActivity context;
    private ArrayList<Order> orders_array = new ArrayList<>();

    public AdapterOrder(AppCompatActivity c, ArrayList<Order> orders){
        this.context = c;
        this.orders_array = orders;
    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);

        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        final Order actualOrder = orders_array.get(position);

        if(actualOrder.imagen64 != null && !actualOrder.imagen64.isEmpty())
        {
            Picasso.get().load(Uri.parse(actualOrder.imagen64)).placeholder(R.drawable.placeholder_img).fit().into(holder.img64);
        }

        String tit = actualOrder.nombreNegocio+" #"+actualOrder.orden_num;

        holder.title_order.setText(tit);
        holder.desc.setText(actualOrder.estado);
        holder.date.setText(actualOrder.fecha);
        holder.total.setText(actualOrder.total);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction tr = context.getSupportFragmentManager().beginTransaction();

                OrderDetailFragment orderDetailFragment = new OrderDetailFragment();

                orderDetailFragment.setOrdetail(actualOrder);



                FragmentManager of = context.getSupportFragmentManager();

                if(of.findFragmentByTag("ggg") == null  || !of.findFragmentByTag("ggg").getClass().toString().equals(OrderDetailFragment.class.toString())) {
                    tr.add(R.id.framm, orderDetailFragment, "ggg");
                    tr.addToBackStack(null);
                    tr.commitAllowingStateLoss();
                }
            }
        });

        if(!actualOrder.isRate) {

            holder.rate_btn.setTextColor(context.getResources().getColor(R.color.primaryColor));
holder.rate_btn.setText(context.getString(R.string.rate_ttl));
            holder.rate_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RateDialog r = new RateDialog(context);

                    r.delivery_name = actualOrder.nombreDelivery;
                    r.bussiname = actualOrder.nombreNegocio;

                    r.show();
                }
            });
        }else{
            holder.rate_btn.setText(context.getString(R.string.rated_already));
            holder.rate_btn.setTextColor(context.getResources().getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return orders_array.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder{


        ImageView img64;
        TextView title_order, desc, date, total, rate_btn;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            img64 = itemView.findViewById(R.id.img_order);
            title_order = itemView.findViewById(R.id.title_order);
            desc = itemView.findViewById(R.id.desc_order);
            date = itemView.findViewById(R.id.date_order);
            total = itemView.findViewById(R.id.total_order);
            rate_btn = itemView.findViewById(R.id.rate_btn);
        }
    }
}
