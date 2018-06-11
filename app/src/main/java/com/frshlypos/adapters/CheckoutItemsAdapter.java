package com.frshlypos.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frshlypos.databinding.RowCheckoutItemBinding;
import com.frshlypos.R;
import com.frshlypos.model.CurrentOrderModel;
import com.frshlypos.model.FormattedServerItemModel;
import com.frshlypos.ui.activities.MainActivity;
import com.frshlypos.utils.AppConstants;
import com.frshlypos.utils.SessionManager;
import com.frshlypos.utils.Util;

import java.util.ArrayList;

/**
 * Created by Akshay.Panchal on 12-Jul-17
 */

public class CheckoutItemsAdapter extends RecyclerView.Adapter<CheckoutItemsAdapter.ViewHolder> {

    private final ArrayList<CurrentOrderModel> dataSet;
    private Context context;
    SessionManager session;
    MainActivity mainActivity;

    public CheckoutItemsAdapter(Context context, ArrayList<CurrentOrderModel> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
        session = new SessionManager(context);
        mainActivity = ((MainActivity) context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowCheckoutItemBinding mBinder = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_checkout_item, parent, false);
        return new ViewHolder(mBinder);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CurrentOrderModel model = dataSet.get(position);
        FormattedServerItemModel priceData = mainActivity.mapPriceData.get(model.getItemId());
        float totalAmount = 0;
        float price;
        float gstPercent = Float.valueOf(priceData.getCgstPercent()) + Float.valueOf(priceData.getCgstPercent());
        float mrpWithoutTax = (priceData.getMrp() * 100) / (100 + Float.valueOf(priceData.getCgstPercent()) + Float.valueOf(priceData.getSgstPercent()));
        price = mrpWithoutTax * model.getQuantity();
        totalAmount += price;

        holder.mBinder.tvQty.setText("" + (model.getQuantity() < 10 ? "0" + model.getQuantity() : model.getQuantity()));
        if (Util.isTestModeItem(model.getItemId())) {
            holder.mBinder.tvItemName.setText("" + mainActivity.mapPriceData.get(model.getItemId()).getName());
            holder.mBinder.tvPrice.setText(session.getDataByKey(AppConstants.PREF_KEY_CURRENCY, AppConstants.RUPPEE) + " 1");
        } else {
            holder.mBinder.tvItemName.setText(mainActivity.mapPriceData.get(model.getItemId()).getName());
            holder.mBinder.tvPrice.setText(session.getDataByKey(AppConstants.PREF_KEY_CURRENCY, AppConstants.RUPPEE) + " " + Util.getRoundedValue(price));
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowCheckoutItemBinding mBinder;

        public ViewHolder(RowCheckoutItemBinding mBinder) {
            super(mBinder.getRoot());
            this.mBinder = mBinder;

        }
    }

}
