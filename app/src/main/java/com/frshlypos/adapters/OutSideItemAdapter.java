package com.frshlypos.adapters;

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

import com.bumptech.glide.Glide;
import com.frshlypos.FrshlyApp;
import com.frshlypos.R;
import com.frshlypos.databinding.RowOutsideItemBinding;
import com.frshlypos.listeners.OnOrderUpdated;
import com.frshlypos.model.CurrentOrderModel;
import com.frshlypos.model.FormattedServerItemModel;
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

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static java.lang.Integer.parseInt;

/**
 * Created by Akshay.Panchal on 12-Jul-17.
 */

public class OutSideItemAdapter extends RecyclerView.Adapter<OutSideItemAdapter.ViewHolder> {

    private final ArrayList<FormattedServerItemModel> dataSet;
    private Context context;
    OnOrderUpdated onOrderUpdated;
    MainActivity mainActivity;
    SessionManager sessionManager;
    private ProductItemActionListener actionListener;

    public OutSideItemAdapter(Context context, ArrayList<FormattedServerItemModel> dataSet, OnOrderUpdated onOrderUpdated) {
        this.context = context;
        this.dataSet = dataSet;
        this.onOrderUpdated = onOrderUpdated;
        mainActivity = ((MainActivity) context);
        sessionManager = new SessionManager(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowOutsideItemBinding mBinder = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_outside_item, parent, false);
        return new ViewHolder(mBinder);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(context).load(Util.getOutletURL() + "/order_app/image/" + dataSet.get(position).getMasterId()).placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder)).into(holder.mBinder.ivItem);


        holder.mBinder.ivItem.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (!sessionManager.getDataByKey(AppConstants.PREF_KEY_DOUBLE_TAP_TO_SHOW_POPUP, true)) {
                    showAddItemDialog(position);
                } else {

                    if (FrshlyApp.getInstance().isWifiConnected()) {
                        FormattedServerItemModel tempModel = dataSet.get(position);
                        if (mainActivity.mapCurrentOrder.containsKey(tempModel.getItemId())) {
                            mainActivity.originalQuantity = +mainActivity.mapCurrentOrder.get(tempModel.getItemId()).getQuantity();
                            tempModel.setQuantity(mainActivity.mapCurrentOrder.get(tempModel.getItemId()).getQuantity());
                            final int currentValue = tempModel.getQuantity();
                            final int newValue = currentValue + 1;
                            tempModel.setQuantity(newValue);
                        } else {
                            tempModel.setQuantity(1);
                            mainActivity.originalQuantity = 1;
                        }

                        if (tempModel.getQuantity() < mainActivity.originalQuantity) {
                            int diffQunatity = mainActivity.originalQuantity - tempModel.getQuantity();
                            decreaseLockByQty(tempModel.getItemId(), diffQunatity);
                        }
                        if (tempModel.getQuantity() > 0) {
                            updateLock(tempModel.getItemId(), tempModel.getQuantity(), 0);
                        } else {
                            removeLock(tempModel.getItemId());
                        }
                        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_ANIMATED_ITEMS, true))
                            actionListener.onItemTap(holder.mBinder.ivItem);

                        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_ANIMATED_ITEMS, true)) {
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

                }
            }

            @Override
            public void onDoubleClick(View v) {
                Logger.e("Double Click");
                if (sessionManager.getDataByKey(AppConstants.PREF_KEY_DOUBLE_TAP_TO_SHOW_POPUP, true)) {
                    showAddItemDialog(position);
                }
            }
        });


        if (position == dataSet.size() - 1) {
            holder.mBinder.space.setVisibility(View.VISIBLE);
        } else {
            holder.mBinder.space.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size() > FrshlyApp.getInstance().MAX_SNACKS_ITEMS ? FrshlyApp.getInstance().MAX_SNACKS_ITEMS : dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowOutsideItemBinding mBinder;

        public ViewHolder(RowOutsideItemBinding mBinder) {
            super(mBinder.getRoot());
            this.mBinder = mBinder;

        }

    }

    public void showAddItemDialogOnOrderClick(int itemId) {
        for (int i = 0; i < dataSet.size(); i++) {
            if (dataSet.get(i).getItemId() == itemId) {
                showAddItemDialog(i);
            }
        }
    }

    public void showAddItemDialog(int position) {
        final FormattedServerItemModel tempModel = dataSet.get(position);
        ImageView ivItem;
        Button btnCancel, btnConfirm;
        final TextView tvRemoveItemNotice;
        final EditText etQuantity;
        final ImageButton ibMinus, ibPlus;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.layout_dialog_add_item, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.drawable_border_round_3sdp_transparent));
        tvRemoveItemNotice = (TextView) dialogView.findViewById(R.id.tvRemoveItemNotice);
        ivItem = (ImageView) dialogView.findViewById(R.id.ivItem);
        btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        btnConfirm = (Button) dialogView.findViewById(R.id.btnConfirm);
        etQuantity = (EditText) dialogView.findViewById(R.id.etQuantity);
        ibMinus = (ImageButton) dialogView.findViewById(R.id.ibMinus);
        ibPlus = (ImageButton) dialogView.findViewById(R.id.ibPlus);

        if (mainActivity.mapCurrentOrder.containsKey(tempModel.getItemId())) {
            etQuantity.setText("" + mainActivity.mapCurrentOrder.get(tempModel.getItemId()).getQuantity());
            mainActivity.originalQuantity = +mainActivity.mapCurrentOrder.get(tempModel.getItemId()).getQuantity();
        } else {
            if (Util.isItemInDispenser(tempModel.getItemId(), tempModel.getLocation())) {
                APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
                TryLockModel model = new TryLockModel();
                model.setDelta_count(1);
                service.tryToLockItem("" + tempModel.getItemId(), model)
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
                                Logger.e("Error occured while trying to acquire lock: " + tempModel.getItemId());
                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                try {
                                    JSONObject object = new JSONObject(responseBody.string());
                                    Logger.i("Successfully locked item - " + tempModel.getItemId());
                                    if (object.optBoolean("error")) {
                                        etQuantity.setText("0");
                                        tempModel.setQuantity(0);
                                        mainActivity.originalQuantity = 0;
                                    } else if (object.optBoolean("flag")) {
                                        etQuantity.setText("1");
                                        tempModel.setQuantity(1);
                                        mainActivity.originalQuantity = 1;
                                    } else {
                                        etQuantity.setText("0");
                                        tempModel.setQuantity(0);
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
                tempModel.setQuantity(1);
                mainActivity.originalQuantity = 1;
            }
        }

        if (Util.isTestModeItem(tempModel.getItemId())) {
            Glide.with(context).load(Util.getOutletURL() + "/order_app/image/").placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder)).into(ivItem);
        } else {
            Glide.with(context).load(Util.getOutletURL() + "/order_app/image/" + dataSet.get(position).getMasterId()).placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder)).into(ivItem);
        }


        View.OnClickListener increaseClickListener = null;
        View.OnClickListener decreaseClickListener = null;
        decreaseClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FrshlyApp.getInstance().isWifiConnected()) {
                    //int currentValue = tempModel.getQuantity();
                    int currentValue = parseInt(etQuantity.getText().toString().toString());
                    ;
                    if (currentValue <= 1) {
                        tvRemoveItemNotice.setText(R.string.this_will_remove_items_if_exists);
                        tvRemoveItemNotice.setVisibility(View.VISIBLE);
                    } else {
                        tvRemoveItemNotice.setVisibility(View.GONE);
                    }

                    if (currentValue > 0 && (currentValue - 1 >= mainActivity.originalQuantity)) {
                        if (Util.isItemInDispenser(tempModel.getItemId(), tempModel.getLocation())) {
                            decreaseLockByOne(tempModel.getItemId());
                        }
                    }
                    int newValue = currentValue < 1 ? currentValue : currentValue - 1;
                    etQuantity.setText("" + newValue);
                    tempModel.setQuantity(newValue);
                }
            }
        };

        final View.OnClickListener finalIncreaseClickListener = increaseClickListener;
        final View.OnClickListener finalDeceraseClickListener = decreaseClickListener;
        increaseClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FrshlyApp.getInstance().isWifiConnected()) {
                    // final int currentValue = tempModel.getQuantity();
                    final int currentValue = parseInt(etQuantity.getText().toString().toString());
                    final int newValue = currentValue + 1;
                    if (Util.isItemInDispenser(tempModel.getItemId(), tempModel.getLocation())) {
                        if (newValue <= mainActivity.originalQuantity) {
                            tempModel.setQuantity(newValue);
                            etQuantity.setText("" + newValue);
                            tvRemoveItemNotice.setVisibility(View.GONE);
                        } else {
                            ibPlus.setOnClickListener(null);
                            ibMinus.setOnClickListener(null);
                            APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
                            TryLockModel model = new TryLockModel();
                            model.setDelta_count(1);
                            service.tryToLockItem("" + tempModel.getItemId(), model)
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
                                            tvRemoveItemNotice.setVisibility(View.VISIBLE);
                                            tvRemoveItemNotice.setText("Outlet connectivity issues");
                                        }

                                        @Override
                                        public void onNext(ResponseBody responseBody) {
                                            ibPlus.setOnClickListener(finalIncreaseClickListener);
                                            ibMinus.setOnClickListener(finalDeceraseClickListener);
                                            try {
                                                Logger.i("Successfully locked item - " + tempModel.getItemId());
                                                JSONObject object = new JSONObject(responseBody.string());
                                                if (object.optBoolean("flag")) {
                                                    etQuantity.setText("" + newValue);
                                                    tempModel.setQuantity(newValue);
                                                    tvRemoveItemNotice.setVisibility(View.GONE);
                                                } else {
                                                    tvRemoveItemNotice.setVisibility(View.VISIBLE);
                                                    tvRemoveItemNotice.setText("Only " + currentValue + " items present.");
                                                    etQuantity.setText("" + currentValue);
                                                    tempModel.setQuantity(currentValue);
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
                        tempModel.setQuantity(newValue);
                        etQuantity.setText("" + newValue);
                        tvRemoveItemNotice.setVisibility(View.GONE);
                    }
                }

            }
        };
        ibMinus.setOnClickListener(decreaseClickListener);
        ibPlus.setOnClickListener(increaseClickListener);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(etQuantity.getText().toString().toString());
                if (mainActivity.mapCurrentOrder.containsKey(tempModel.getItemId())) {
                    int diffQuantity = qty - mainActivity.originalQuantity;
                    if (diffQuantity > 0) {
                        decreaseLockByQty(tempModel.getItemId(), diffQuantity);
                    }
                } else {
                    if (mainActivity.originalQuantity != 0) {
                        if (qty == 0) {
                            decreaseLockByOne(tempModel.getItemId());
                        } else {
                            decreaseLockByQty(tempModel.getItemId(), qty);
                        }
                    } else {
                        decreaseLockByQty(tempModel.getItemId(), qty);
                    }
                }
                dialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FrshlyApp.getInstance().isWifiConnected()) {
                    if (tempModel.getQuantity() < mainActivity.originalQuantity) {
                        int diffQunatity = mainActivity.originalQuantity - tempModel.getQuantity();
                        decreaseLockByQty(tempModel.getItemId(), diffQunatity);
                    }
                    if (tempModel.getQuantity() > 0) {
                        updateLock(tempModel.getItemId(), tempModel.getQuantity(), 0);
                    } else {
                        removeLock(tempModel.getItemId());
                    }
                    onOrderUpdated.orderUpdated();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
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

    public void setActionListener(ProductItemActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public interface ProductItemActionListener {
        void onItemTap(ImageView imageView);
    }
}
