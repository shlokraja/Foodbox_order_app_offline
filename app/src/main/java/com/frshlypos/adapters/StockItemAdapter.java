package com.frshlypos.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.frshlypos.FrshlyApp;
import com.frshlypos.R;
import com.frshlypos.databinding.RowDispenserItemBinding;
import com.frshlypos.listeners.OnOrderUpdated;
import com.frshlypos.model.CurrentOrderModel;
import com.frshlypos.model.StockItemModel;
import com.frshlypos.model.requestapimodel.TryLockModel;
import com.frshlypos.retrofitservice.APIService;
import com.frshlypos.retrofitservice.ServiceFactory;
import com.frshlypos.ui.activities.MainActivity;
import com.frshlypos.utils.AppConstants;
import com.frshlypos.utils.DoubleClickListener;
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

public class StockItemAdapter extends RecyclerView.Adapter<StockItemAdapter.ViewHolder> {

    private ProductItemActionListener actionListener;
    private final ArrayList<StockItemModel> dataSet;
    private Context context;
    OnOrderUpdated onOrderUpdated;
    SessionManager sessionManager;
    MainActivity mainActivity;
    ImageButton ibMinus;
    ImageButton ibPlus;
    View.OnClickListener decreaseClickListener;
    View.OnClickListener increaseClickListener;

    public StockItemAdapter(Context context, ArrayList<StockItemModel> dataSet, OnOrderUpdated onOrderUpdated) {
        this.context = context;
        this.dataSet = dataSet;
        this.onOrderUpdated = onOrderUpdated;
        sessionManager = new SessionManager(context);
        mainActivity = ((MainActivity) context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowDispenserItemBinding mBinder = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_dispenser_item, parent, false);
        return new ViewHolder(mBinder);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (Util.isTestModeItem(dataSet.get(position).getItemCode())) {
            holder.mBinder.card.setVisibility(View.VISIBLE);
            holder.mBinder.tvItemCode.setVisibility(View.GONE);
            Glide.with(context).load(Util.getOutletURL() + "/order_app/image/" + dataSet.get(position).getFormattedServerItemModel().getMasterId()).placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder)).into(holder.mBinder.ivItem);

        } else {
            if (sessionManager.getDataByKey(AppConstants.PREF_KEY_SHOW_ITEM_IMAGES, false)) {
                holder.mBinder.card.setVisibility(View.VISIBLE);
                holder.mBinder.llItemCode.setVisibility(View.GONE);
                Glide.with(context).load(Util.getOutletURL() + "/order_app/image/" + dataSet.get(position).getFormattedServerItemModel().getMasterId()).into(holder.mBinder.ivItem);

            } else {
                holder.mBinder.card.setVisibility(View.GONE);
                holder.mBinder.llItemCode.setVisibility(View.VISIBLE);
                holder.mBinder.tvItemCode.setText("" + dataSet.get(position).getFormattedServerItemModel().getItemTag());
                if (dataSet.get(position).getFormattedServerItemModel().getVeg()) {
                    holder.mBinder.ivItemType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_veg));
                } else {
                    holder.mBinder.ivItemType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nonveg));
                }
            }
        }

        if (position == dataSet.size() - 1) {
            holder.mBinder.space.setVisibility(View.VISIBLE);
        } else {
            holder.mBinder.space.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return dataSet.size() > FrshlyApp.getInstance().MAX_MEAL_ITEMS ? FrshlyApp.getInstance().MAX_MEAL_ITEMS : dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowDispenserItemBinding mBinder;
        StockItemModel tempModel;

        public ViewHolder(final RowDispenserItemBinding mBinder) {
            super(mBinder.getRoot());
            this.mBinder = mBinder;

            mBinder.ivItem.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Logger.e("Single Click");
                    if (!sessionManager.getDataByKey(AppConstants.PREF_KEY_DOUBLE_TAP_TO_SHOW_POPUP, true)) {
                        showAddItemDialog(getAdapterPosition(), null);
                    } else {
                        try {
                            tempModel = dataSet.get(getAdapterPosition());

                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                        if (mainActivity.mapCurrentOrder.containsKey(tempModel.getFormattedServerItemModel().getItemId())) {
                            mainActivity.originalQuantity = mainActivity.mapCurrentOrder.get(tempModel.getFormattedServerItemModel().getItemId()).getQuantity();
                            tempModel.getFormattedServerItemModel().setQuantity(mainActivity.mapCurrentOrder.get(tempModel.getFormattedServerItemModel().getItemId()).getQuantity());
                            increaseQuantityOnSingleClick(tempModel, mBinder.ivItem);
                        } else {
                            if (Util.isItemInDispenser(tempModel.getFormattedServerItemModel().getItemId(), tempModel.getFormattedServerItemModel().getLocation())) {
                                APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
                                TryLockModel model = new TryLockModel();
                                model.setDelta_count(1);
                                service.tryToLockItem("" + tempModel.getItemCode(), model)
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
                                                Logger.e("Error occured while trying to acquire lock: " + tempModel.getItemCode());
                                            }

                                            @Override
                                            public void onNext(ResponseBody responseBody) {
                                                try {
                                                    JSONObject object = new JSONObject(responseBody.string());
                                                    Logger.i("Successfully locked item - " + tempModel.getFormattedServerItemModel().getItemId());
                                                    if (object.optBoolean("error")) {
                                                        tempModel.getFormattedServerItemModel().setQuantity(0);
                                                        mainActivity.originalQuantity = 0;
                                                    } else if (object.optBoolean("flag")) {
                                                        tempModel.getFormattedServerItemModel().setQuantity(1);
                                                        mainActivity.originalQuantity = 1;
                                                        confirmQuantity(tempModel, mBinder.ivItem);

                                                    } else {
                                                        tempModel.getFormattedServerItemModel().setQuantity(0);
                                                        mainActivity.originalQuantity = 0;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                            } else {
                                tempModel.getFormattedServerItemModel().setQuantity(1);
                                mainActivity.originalQuantity = 1;
                                increaseQuantityOnSingleClick(tempModel, mBinder.ivItem);
                            }
                        }
                        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_ANIMATED_ITEMS, true))
                            actionListener.onItemTap(mBinder.ivItem);
                    }
                }

                @Override
                public void onDoubleClick(View v) {
                    Logger.e("Double Click");
                    if (sessionManager.getDataByKey(AppConstants.PREF_KEY_DOUBLE_TAP_TO_SHOW_POPUP, true)) {
                        showAddItemDialog(getAdapterPosition(), null);
                    }

                }
            });

            mBinder.llItemCode.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if (!sessionManager.getDataByKey(AppConstants.PREF_KEY_DOUBLE_TAP_TO_SHOW_POPUP, true)) {
                        showAddItemDialog(getAdapterPosition(), null);
                    } else {
                        tempModel = dataSet.get(getAdapterPosition());
                        if (mainActivity.mapCurrentOrder.containsKey(tempModel.getFormattedServerItemModel().getItemId())) {
                            mainActivity.originalQuantity = mainActivity.mapCurrentOrder.get(tempModel.getFormattedServerItemModel().getItemId()).getQuantity();
                            tempModel.getFormattedServerItemModel().setQuantity(mainActivity.mapCurrentOrder.get(tempModel.getFormattedServerItemModel().getItemId()).getQuantity());
                            increaseQuantityOnSingleClick(tempModel, mBinder.ivItem);
                        } else {
                            if (Util.isItemInDispenser(tempModel.getFormattedServerItemModel().getItemId(), tempModel.getFormattedServerItemModel().getLocation())) {
                                APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
                                TryLockModel model = new TryLockModel();
                                model.setDelta_count(1);
                                service.tryToLockItem("" + tempModel.getItemCode(), model)
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
                                                Logger.e("Error occured while trying to acquire lock: " + tempModel.getItemCode());
                                            }

                                            @Override
                                            public void onNext(ResponseBody responseBody) {
                                                try {
                                                    JSONObject object = new JSONObject(responseBody.string());
                                                    Logger.i("Successfully locked item - " + tempModel.getFormattedServerItemModel().getItemId());
                                                    if (object.optBoolean("error")) {
                                                        tempModel.getFormattedServerItemModel().setQuantity(0);
                                                        mainActivity.originalQuantity = 0;
                                                    } else if (object.optBoolean("flag")) {
                                                        tempModel.getFormattedServerItemModel().setQuantity(1);
                                                        mainActivity.originalQuantity = 1;
                                                        confirmQuantity(tempModel, mBinder.ivItem);

                                                    } else {
                                                        tempModel.getFormattedServerItemModel().setQuantity(0);
                                                        mainActivity.originalQuantity = 0;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                            } else {
                                tempModel.getFormattedServerItemModel().setQuantity(1);
                                mainActivity.originalQuantity = 1;
                                increaseQuantityOnSingleClick(tempModel, mBinder.ivItem);
                            }
                        }
                    }
                }

                @Override
                public void onDoubleClick(View v) {
                    if (sessionManager.getDataByKey(AppConstants.PREF_KEY_DOUBLE_TAP_TO_SHOW_POPUP, true)) {
                        showAddItemDialog(getAdapterPosition(), null);
                    }
                }
            });

        }
    }

    public void showAddItemDialogOnOrderClick(int itemId) {
        boolean isItemDeleted = true;
        for (int i = 0; i < dataSet.size(); i++) {
            if (dataSet.get(i).getFormattedServerItemModel().getItemId() == itemId) {
                showAddItemDialog(i, null);
                isItemDeleted = false;
            }
        }
        if (isItemDeleted) {
            for (int i = 0; i < mainActivity.listStockLastRemovedItems.size(); i++) {
                if (mainActivity.listStockLastRemovedItems.get(i).getItemCode() == itemId) {
                    showAddItemDialog(-1, mainActivity.listStockLastRemovedItems.get(i));
                }
            }
        }
    }

    public void showAddItemDialog(int position, StockItemModel stockItemModel) {
        final StockItemModel tempModel;
        if (position == -1)
            tempModel = stockItemModel;
        else
            tempModel = dataSet.get(position);
        ImageView ivItem;
        Button btnCancel, btnConfirm;
        final TextView tvRemoveItemNotice;
        final EditText etQuantity;
        //final ImageButton ibMinus, ibPlus;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.layout_dialog_add_item, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.drawable_border_round_3sdp_transparent));
        dialog.setCancelable(false);
        tvRemoveItemNotice = (TextView) dialogView.findViewById(R.id.tvRemoveItemNotice);
        ivItem = (ImageView) dialogView.findViewById(R.id.ivItem);
        btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        btnConfirm = (Button) dialogView.findViewById(R.id.btnConfirm);
        etQuantity = (EditText) dialogView.findViewById(R.id.etQuantity);
        ibMinus = (ImageButton) dialogView.findViewById(R.id.ibMinus);
        ibPlus = (ImageButton) dialogView.findViewById(R.id.ibPlus);

        if (mainActivity.mapCurrentOrder.containsKey(tempModel.getFormattedServerItemModel().getItemId())) {
            etQuantity.setText("" + mainActivity.mapCurrentOrder.get(tempModel.getFormattedServerItemModel().getItemId()).getQuantity());
            mainActivity.originalQuantity = mainActivity.mapCurrentOrder.get(tempModel.getFormattedServerItemModel().getItemId()).getQuantity();
        } else {
            if (Util.isItemInDispenser(tempModel.getFormattedServerItemModel().getItemId(), tempModel.getFormattedServerItemModel().getLocation())) {
                APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
                TryLockModel model = new TryLockModel();
                model.setDelta_count(1);
                service.tryToLockItem("" + tempModel.getItemCode(), model)
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
                                Logger.e("Error occured while trying to acquire lock: " + tempModel.getItemCode());
                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                try {
                                    JSONObject object = new JSONObject(responseBody.string());
                                    Logger.i("Successfully locked item - " + tempModel.getFormattedServerItemModel().getItemId());
                                    if (object.optBoolean("error")) {
                                        etQuantity.setText("0");
                                        tempModel.getFormattedServerItemModel().setQuantity(0);
                                        mainActivity.originalQuantity = 0;
                                    } else if (object.optBoolean("flag")) {
                                        etQuantity.setText("1");
                                        tempModel.getFormattedServerItemModel().setQuantity(1);
                                        mainActivity.originalQuantity = 1;
                                    } else {
                                        etQuantity.setText("0");
                                        tempModel.getFormattedServerItemModel().setQuantity(0);
                                        mainActivity.originalQuantity = 0;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

            } else {
                etQuantity.setText("1");
                tempModel.getFormattedServerItemModel().setQuantity(1);
                mainActivity.originalQuantity = 1;
            }
        }

        if (Util.isTestModeItem(tempModel.getFormattedServerItemModel().getItemId())) {
            Glide.with(context).load(Util.getOutletURL() + "/order_app/image/").placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder)).into(ivItem);
        } else {
            Glide.with(context).load(Util.getOutletURL() + "/order_app/image/" + tempModel.getFormattedServerItemModel().getMasterId()).placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder)).into(ivItem);
        }


      /*  final View.OnClickListener increaseClickListener = null;
        final View.OnClickListener decreaseClickListener = null;*/

        decreaseClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FrshlyApp.getInstance().isWifiConnected()) {
                    final int currentValue = Integer.parseInt(etQuantity.getText().toString().toString());
                    //int currentValue = tempModel.getFormattedServerItemModel().getQuantity() == null ? 1 : tempModel.getFormattedServerItemModel().getQuantity();
                    if (currentValue <= 1) {
                        tvRemoveItemNotice.setText(R.string.this_will_remove_items_if_exists);
                        tvRemoveItemNotice.setVisibility(View.VISIBLE);
                    } else {
                        tvRemoveItemNotice.setVisibility(View.GONE);
                    }

                    if (currentValue > 0 && (currentValue - 1 >= mainActivity.originalQuantity)) {
                        if (Util.isItemInDispenser(tempModel.getFormattedServerItemModel().getItemId(), tempModel.getFormattedServerItemModel().getLocation())) {
                            decreaseLockByOne(tempModel.getFormattedServerItemModel().getItemId());
                        }
                    }
                    int newValue = currentValue < 1 ? currentValue : currentValue - 1;
                    etQuantity.setText("" + newValue);
                    tempModel.getFormattedServerItemModel().setQuantity(newValue);
                }
            }
        };

        increaseClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FrshlyApp.getInstance().isWifiConnected()) {
                    final int currentValue = Integer.parseInt(etQuantity.getText().toString().toString());
                    // final int currentValue = tempModel.getFormattedServerItemModel().getQuantity() == null ? 1 : tempModel.getFormattedServerItemModel().getQuantity();
                    final int newValue = currentValue + 1;
                    if (Util.isItemInDispenser(tempModel.getFormattedServerItemModel().getItemId(), tempModel.getFormattedServerItemModel().getLocation())) {
                        if (newValue <= mainActivity.originalQuantity) {
                            tempModel.getFormattedServerItemModel().setQuantity(newValue);
                            etQuantity.setText("" + newValue);
                            tvRemoveItemNotice.setVisibility(View.GONE);
                        } else {
                            removeClickListeners();
                            APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
                            TryLockModel model = new TryLockModel();
                            model.setDelta_count(1);
                            service.tryToLockItem("" + tempModel.getItemCode(), model)
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
                                            tvRemoveItemNotice.setVisibility(View.VISIBLE);
                                            tvRemoveItemNotice.setText("Outlet connectivity issues");
                                        }

                                        @Override
                                        public void onNext(ResponseBody responseBody) {
                                            setClickListeners();
                                            try {
                                                JSONObject object = new JSONObject(responseBody.string());
                                                Logger.i("Successfully locked item - " + tempModel.getFormattedServerItemModel().getItemId());
                                                if (object.optBoolean("flag")) {
                                                    etQuantity.setText("" + newValue);
                                                    tempModel.getFormattedServerItemModel().setQuantity(newValue);
                                                    tvRemoveItemNotice.setVisibility(View.GONE);
                                                } else {
                                                    tvRemoveItemNotice.setVisibility(View.VISIBLE);
                                                    tvRemoveItemNotice.setText("Only " + currentValue + " items present.");
                                                    etQuantity.setText("" + currentValue);
                                                    tempModel.getFormattedServerItemModel().setQuantity(currentValue);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    });
                        }
                    } else {
                        tempModel.getFormattedServerItemModel().setQuantity(newValue);
                        etQuantity.setText("" + newValue);
                        tvRemoveItemNotice.setVisibility(View.GONE);
                    }
                }
            }
        };
        setClickListeners();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(etQuantity.getText().toString().trim());
                //int qty = tempModel.getFormattedServerItemModel().getQuantity() == null?1:tempModel.getFormattedServerItemModel().getQuantity();
                if (mainActivity.mapCurrentOrder.containsKey(tempModel.getFormattedServerItemModel().getItemId())) {
                    int diffQuantity = qty - mainActivity.originalQuantity;
                    if (diffQuantity > 0) {
                        decreaseLockByQty(tempModel.getFormattedServerItemModel().getItemId(), diffQuantity);
                    }
                } else {
                    if (mainActivity.originalQuantity != 0) {
                        if (qty == 0) {
                            decreaseLockByOne(tempModel.getFormattedServerItemModel().getItemId());
                        } else {
                            decreaseLockByQty(tempModel.getFormattedServerItemModel().getItemId(), qty);
                        }
                    } else {
                        decreaseLockByQty(tempModel.getFormattedServerItemModel().getItemId(), qty);
                    }
                }
                dialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FrshlyApp.getInstance().isWifiConnected()) {
                    int qty = tempModel.getFormattedServerItemModel().getQuantity() == null ? 0 : tempModel.getFormattedServerItemModel().getQuantity();
                    if (qty < mainActivity.originalQuantity) {
                        int diffQunatity = mainActivity.originalQuantity - qty;
                        decreaseLockByQty(tempModel.getItemCode(), diffQunatity);
                    }
                    if (qty > 0) {
                        updateLock(tempModel.getItemCode(), qty, 0);
                    } else {
                        removeLock(tempModel.getItemCode());
                    }
                    onOrderUpdated.orderUpdated();
                    dialog.dismiss();
                }
            }
        });


        dialog.show();
    }

    private void confirmQuantity(StockItemModel tempModel, ImageView imageView) {
        int qty = tempModel.getFormattedServerItemModel().getQuantity() == null ? 0 : tempModel.getFormattedServerItemModel().getQuantity();
        if (qty < mainActivity.originalQuantity) {
            int diffQunatity = mainActivity.originalQuantity - qty;
            decreaseLockByQty(tempModel.getItemCode(), diffQunatity);
        }
        if (qty > 0) {
            updateLock(tempModel.getItemCode(), qty, 0);
        } else {
            removeLock(tempModel.getItemCode());
        }
        if (imageView != null && sessionManager.getDataByKey(AppConstants.PREF_KEY_ANIMATED_ITEMS, true)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onOrderUpdated.orderUpdated();
                }
            }, 1000);

        } else {
            onOrderUpdated.orderUpdated();
        }

    }

    private void setClickListeners() {
        if (increaseClickListener != null)
            ibPlus.setOnClickListener(increaseClickListener);

        if (decreaseClickListener != null)
            ibMinus.setOnClickListener(decreaseClickListener);


    }

    private void removeClickListeners() {
        ibMinus.setOnClickListener(null);
        ibPlus.setOnClickListener(null);
    }


    private void increaseLockByOne(int itemId) {
        increaseLockByQty(itemId, 1);
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

    private void increaseQuantityOnSingleClick(final StockItemModel tempModel, final ImageView imageView) {
        final int currentValue = tempModel.getFormattedServerItemModel().getQuantity() == null ? 0 : tempModel.getFormattedServerItemModel().getQuantity();
        // final int currentValue = tempModel.getFormattedServerItemModel().getQuantity() == null ? 1 : tempModel.getFormattedServerItemModel().getQuantity();
        final int newValue = currentValue + 1;
        if (Util.isItemInDispenser(tempModel.getFormattedServerItemModel().getItemId(), tempModel.getFormattedServerItemModel().getLocation())) {
            if (newValue <= mainActivity.originalQuantity) {
                tempModel.getFormattedServerItemModel().setQuantity(newValue);
                confirmQuantity(tempModel, imageView);
            } else {
                APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
                TryLockModel model = new TryLockModel();
                model.setDelta_count(1);
                service.tryToLockItem("" + tempModel.getItemCode(), model)
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
                                Toast.makeText(context, "Outlet connectivity issues", Toast.LENGTH_SHORT).show();
                                confirmQuantity(tempModel, imageView);
                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                try {
                                    JSONObject object = new JSONObject(responseBody.string());
                                    Logger.i("Successfully locked item - " + tempModel.getFormattedServerItemModel().getItemId());
                                    if (object.optBoolean("flag")) {
                                        tempModel.getFormattedServerItemModel().setQuantity(newValue);
                                    } else {
                                        tempModel.getFormattedServerItemModel().setQuantity(currentValue);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                confirmQuantity(tempModel, imageView);
                            }

                        });
            }
        } else {
            tempModel.getFormattedServerItemModel().setQuantity(newValue);
            confirmQuantity(tempModel, imageView);
        }
    }

    public void setActionListener(ProductItemActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public interface ProductItemActionListener {
        void onItemTap(ImageView imageView);
    }

}
