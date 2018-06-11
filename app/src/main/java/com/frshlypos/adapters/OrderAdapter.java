package com.frshlypos.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frshlypos.databinding.RowOrderItemBinding;
import com.frshlypos.FrshlyApp;
import com.frshlypos.R;
import com.frshlypos.listeners.OnItemClickListener;
import com.frshlypos.listeners.OnOrderUpdated;
import com.frshlypos.model.CurrentOrderModel;
import com.frshlypos.model.FormattedServerItemModel;
import com.frshlypos.model.StockItemModel;
import com.frshlypos.model.requestapimodel.TryLockModel;
import com.frshlypos.retrofitservice.APIService;
import com.frshlypos.retrofitservice.ServiceFactory;
import com.frshlypos.ui.activities.MainActivity;
import com.frshlypos.utils.AppConstants;
import com.frshlypos.utils.Logger;
import com.frshlypos.utils.SessionManager;
import com.frshlypos.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Akshay.Panchal on 12-Jul-17.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final ArrayList<CurrentOrderModel> dataSet;
    private Context context;
    OnItemClickListener onItemClickListener;
    OnOrderUpdated onOrderUpdated;
    SessionManager session;
    MainActivity mainActivity;

    public OrderAdapter(Context context, ArrayList<CurrentOrderModel> dataSet, OnItemClickListener onItemClickListener, OnOrderUpdated onOrderUpdated) {
        this.context = context;
        this.dataSet = dataSet;
        this.onItemClickListener = onItemClickListener;
        this.onOrderUpdated = onOrderUpdated;
        session = new SessionManager(context);
        mainActivity = ((MainActivity) context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowOrderItemBinding mBinder = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_order_item, parent, false);
        return new ViewHolder(mBinder);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CurrentOrderModel model = dataSet.get(position);

        if (Util.isTestModeItem(model.getItemId())) {
            holder.mBinder.tvItemName.setText("" + mainActivity.mapPriceData.get(model.getItemId()).getName());
        } else {
            holder.mBinder.tvItemName.setText(mainActivity.mapPriceData.get(model.getItemId()).getName());
        }
        holder.mBinder.tvQty.setText("" + model.getQuantity());
        holder.mBinder.tvPrice.setText(session.getDataByKey(AppConstants.PREF_KEY_CURRENCY, AppConstants.RUPPEE) + " " + Util.getRoundedValue(model.getQuantity() * (mainActivity.mapPriceData.get(model.getItemId()).getMrp())));

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RowOrderItemBinding mBinder;
        View.OnClickListener decreaseClickListener;
        View.OnClickListener increaseClickListener;
        StockItemModel tempModelStock;
        FormattedServerItemModel tempModelOutSide;
        boolean isDispenserItem;

        public ViewHolder(final RowOrderItemBinding mBinder) {
            super(mBinder.getRoot());
            this.mBinder = mBinder;
            mBinder.rlMain.setOnClickListener(this);
            mBinder.ibPlus.setOnClickListener(this);
            mBinder.ibMinus.setOnClickListener(this);

            mBinder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FrshlyApp.getInstance().isWifiConnected()) {
                        isDispenserItem = Util.isItemInDispenser(dataSet.get(getAdapterPosition()).getItemId(), dataSet.get(getAdapterPosition()).getLocation());
                        if (isDispenserItem) {
                            tempModelStock = mainActivity.getStockItemByItemId(dataSet.get(getAdapterPosition()).getItemId());
                            if (tempModelStock == null) {
                                tempModelStock = mainActivity.getRemovedStockItemByItemId(dataSet.get(getAdapterPosition()).getItemId());
                            }
                        } else {
                            tempModelOutSide = mainActivity.getOutSidetemByItemId(dataSet.get(getAdapterPosition()).getItemId());
                        }
                        final int currentValue = Integer.parseInt(mBinder.tvQty.getText().toString().toString());
                        if (currentValue <= 1) {
                            mBinder.tvRemoveItemNotice.setText(R.string.this_will_remove_items_if_exists);
                            mBinder.tvRemoveItemNotice.setVisibility(View.VISIBLE);
                        } else {
                            mBinder.tvRemoveItemNotice.setVisibility(View.GONE);
                        }

                        if (currentValue > 0 ) {
                            if (isDispenserItem) {
                                Logger.e("Single Click decrease lock");
                                decreaseLockByQty(tempModelStock.getFormattedServerItemModel().getItemId(), currentValue);
                            }
                        }
                        int newValue = 0;
                        mBinder.tvQty.setText("" + newValue);
                        if (isDispenserItem) {
                            tempModelStock.getFormattedServerItemModel().setQuantity(newValue);
                        } else {
                            tempModelOutSide.setQuantity(newValue);
                        }

                        confirmQuantity();
                    }
                }
            });

            decreaseClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FrshlyApp.getInstance().isWifiConnected()) {
                        isDispenserItem = Util.isItemInDispenser(dataSet.get(getAdapterPosition()).getItemId(), dataSet.get(getAdapterPosition()).getLocation());
                        if (isDispenserItem) {
                            tempModelStock = mainActivity.getStockItemByItemId(dataSet.get(getAdapterPosition()).getItemId());
                            if (tempModelStock == null) {
                                tempModelStock = mainActivity.getRemovedStockItemByItemId(dataSet.get(getAdapterPosition()).getItemId());
                            }
                        } else {
                            tempModelOutSide = mainActivity.getOutSidetemByItemId(dataSet.get(getAdapterPosition()).getItemId());
                        }
                        final int currentValue = Integer.parseInt(mBinder.tvQty.getText().toString().toString());
                        //int currentValue = tempModel.getFormattedServerItemModel().getQuantity() == null ? 1 : tempModel.getFormattedServerItemModel().getQuantity();
                        if (currentValue <= 1) {
                            mBinder.tvRemoveItemNotice.setText(R.string.this_will_remove_items_if_exists);
                            mBinder.tvRemoveItemNotice.setVisibility(View.VISIBLE);
                        } else {
                            mBinder.tvRemoveItemNotice.setVisibility(View.GONE);
                        }

                        if (currentValue > 0) {
                            if (isDispenserItem) {
                                decreaseLockByOne(tempModelStock.getFormattedServerItemModel().getItemId());
                            }
                        }
                        int newValue = currentValue < 1 ? currentValue : currentValue - 1;
                        mBinder.tvQty.setText("" + newValue);
                        if (isDispenserItem) {
                            tempModelStock.getFormattedServerItemModel().setQuantity(newValue);
                        } else {
                            tempModelOutSide.setQuantity(newValue);
                        }
                        confirmQuantity();
                    }
                }
            };

            increaseClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FrshlyApp.getInstance().isWifiConnected()) {
                        isDispenserItem = Util.isItemInDispenser(dataSet.get(getAdapterPosition()).getItemId(), dataSet.get(getAdapterPosition()).getLocation());
                        if (isDispenserItem) {
                            tempModelStock = mainActivity.getStockItemByItemId(dataSet.get(getAdapterPosition()).getItemId());
                            if (tempModelStock == null) {
                                mBinder.tvRemoveItemNotice.setText("Only " + mBinder.tvQty.getText().toString() + " items present.");
                                return;
                            }
                        } else {
                            tempModelOutSide = mainActivity.getOutSidetemByItemId(dataSet.get(getAdapterPosition()).getItemId());
                        }
                        final int currentValue = Integer.parseInt(mBinder.tvQty.getText().toString());
                        final int newValue = currentValue + 1;
                        if (isDispenserItem) {
                            if (newValue <= mainActivity.originalQuantity) {
                                tempModelStock.getFormattedServerItemModel().setQuantity(newValue);
                                mBinder.tvQty.setText("" + newValue);
                                mBinder.tvRemoveItemNotice.setVisibility(View.GONE);
                                confirmQuantity();
                            } else {
                                removeClickListeners();
                                APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
                                TryLockModel model = new TryLockModel();
                                model.setDelta_count(1);
                                service.tryToLockItem("" + tempModelStock.getItemCode(), model)
                                        .subscribeOn(Schedulers.io())
                                        .timeout(3, TimeUnit.SECONDS)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .unsubscribeOn(Schedulers.io())
                                        .subscribe(new Subscriber<ResponseBody>() {

                                            @Override
                                            public void onStart() {
                                            }

                                            @Override
                                            public void onCompleted() {
                                                unsubscribe();
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                e.printStackTrace();
                                                mBinder.tvRemoveItemNotice.setVisibility(View.VISIBLE);
                                                mBinder.tvRemoveItemNotice.setText("Outlet connectivity issues");
                                                confirmQuantity();
                                            }

                                            @Override
                                            public void onNext(ResponseBody responseBody) {
                                                setClickListeners();
                                                try {
                                                    JSONObject object = new JSONObject(responseBody.string());
                                                    Logger.i("Successfully locked item - " + tempModelStock.getFormattedServerItemModel().getItemId());
                                                    if (object.optBoolean("flag")) {
                                                        mBinder.tvQty.setText("" + newValue);
                                                        tempModelStock.getFormattedServerItemModel().setQuantity(newValue);
                                                        mBinder.tvRemoveItemNotice.setVisibility(View.GONE);
                                                    } else {
                                                        mBinder.tvRemoveItemNotice.setVisibility(View.VISIBLE);
                                                        mBinder.tvRemoveItemNotice.setText("Only " + currentValue + " items present.");
                                                        mBinder.tvQty.setText("" + currentValue);
                                                        tempModelStock.getFormattedServerItemModel().setQuantity(currentValue);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                confirmQuantity();
                                            }

                                        });
                            }
                        } else {
                            tempModelOutSide.setQuantity(newValue);
                            mBinder.tvQty.setText("" + newValue);
                            mBinder.tvRemoveItemNotice.setVisibility(View.GONE);
                            confirmQuantity();
                        }
                    }
                }
            };
            setClickListeners();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rlMain:
                    onItemClickListener.selectedItem(dataSet.get(getAdapterPosition()).getItemId(), dataSet.get(getAdapterPosition()).getLocation());
                    break;
                case R.id.ibPlus:
                    break;
                case R.id.ibMinus:
            }
        }


        private void setClickListeners() {
            if (increaseClickListener != null)
                mBinder.ibPlus.setOnClickListener(increaseClickListener);

            if (decreaseClickListener != null)
                mBinder.ibMinus.setOnClickListener(decreaseClickListener);


        }

        private void removeClickListeners() {
            mBinder.ibMinus.setOnClickListener(null);
            mBinder.ibPlus.setOnClickListener(null);
        }


        private void increaseLockByQty(final int itemId, int qty) {
            final APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
            TryLockModel model = new TryLockModel();
            model.setDelta_count(qty);
            model.setDirection("increase");
            service.lockItem("" + itemId, model)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<ResponseBody>() {

                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onCompleted() {
                            unsubscribe();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Logger.e("Error occured while creating the lock for item- " + itemId);
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            Logger.e("Successfully created lock for- " + itemId);
                        }
                    });
        }

        private void decreaseLockByOne(int itemId) {
            decreaseLockByQty(itemId, 1);
        }

        private void decreaseLockByQty(final int itemId, int qty) {
            final APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
            TryLockModel model = new TryLockModel();
            model.setDelta_count(qty);
            model.setDirection("decrease");
            service.lockItem("" + itemId, model)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<ResponseBody>() {

                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onCompleted() {
                            unsubscribe();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Logger.e("Error occured while decreasing lock: " + itemId);
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            Logger.i("Successfully unlocked item - " + itemId);
                        }
                    });
        }

        private void removeLock(int itemId) {
            if (mainActivity.mapCurrentOrder.containsKey(itemId)) {
                mainActivity.mapCurrentOrder.remove(itemId);
            }
        }

        private void updateLock(int itemId, int quantity, int cokeQuantity) {
            String location;
            if (Util.isTestModeItem(itemId)) {
                location = "dispenser";
            } else {
                location = mainActivity.mapPriceData.get(itemId).getLocation();
            }

            CurrentOrderModel model = new CurrentOrderModel();
            model.setQuantity(quantity);
            model.setLocation(location);
            model.setCoke_quantity(cokeQuantity);
            model.setItemId(itemId);
            mainActivity.mapCurrentOrder.put(itemId, model);
        }

        private void confirmQuantity() {
            if (isDispenserItem) {
                int qty = tempModelStock.getFormattedServerItemModel().getQuantity() == null ? 0 : tempModelStock.getFormattedServerItemModel().getQuantity();
                if (qty < mainActivity.originalQuantity) {
                    Logger.e("Confirm Quantity 1");
                    int diffQunatity = mainActivity.originalQuantity - qty;
                    // decreaseLockByQty(tempModelStock.getItemCode(), diffQunatity);
                }
                if (qty > 0) {
                    updateLock(tempModelStock.getItemCode(), qty, 0);
                } else {
                    removeLock(tempModelStock.getItemCode());
                }
            } else {
                if (tempModelOutSide.getQuantity() < mainActivity.originalQuantity) {
                    int diffQunatity = mainActivity.originalQuantity - tempModelOutSide.getQuantity();
                    Logger.e("Confirm Quantity 2");
                    decreaseLockByQty(tempModelOutSide.getItemId(), diffQunatity);
                }
                if (tempModelOutSide.getQuantity() > 0) {
                    updateLock(tempModelOutSide.getItemId(), tempModelOutSide.getQuantity(), 0);
                } else {
                    removeLock(tempModelOutSide.getItemId());
                }
            }
            onOrderUpdated.orderUpdated();
        }

    }

}
