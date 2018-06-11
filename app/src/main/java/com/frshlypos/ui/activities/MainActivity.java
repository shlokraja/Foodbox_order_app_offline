package com.frshlypos.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.frshlypos.FrshlyApp;
import com.frshlypos.R;
import com.frshlypos.adapters.CheckoutItemsAdapter;
import com.frshlypos.adapters.OrderAdapter;
import com.frshlypos.adapters.ProductsPagerAdapter;
import com.frshlypos.adapters.StockItemAdapter;
import com.frshlypos.databinding.ActivityMainBinding;
import com.frshlypos.databinding.LayoutDialogCardCheckoutBinding;
import com.frshlypos.databinding.LayoutDialogCardFailureBinding;
import com.frshlypos.databinding.LayoutDialogCheckoutBinding;
import com.frshlypos.databinding.LayoutDialogOrderPlacedCashCheckoutBinding;
import com.frshlypos.databinding.LayoutDialogPaymentTypeBinding;
import com.frshlypos.listeners.OnItemClickListener;
import com.frshlypos.listeners.OnOrderUpdated;
import com.frshlypos.model.CurrentOrderModel;
import com.frshlypos.model.FormattedServerItemModel;
import com.frshlypos.model.StockItemModel;
import com.frshlypos.model.hqurl.RestaurantDetails;
import com.frshlypos.model.hqurl.ServerItemModelAPI;
import com.frshlypos.model.requestapimodel.TryLockModel;
import com.frshlypos.model.stockdata.InitialStockDataModelAPI;
import com.frshlypos.paymentgateway.DeviceState;
import com.frshlypos.paymentgateway.IPaymentGateway;
import com.frshlypos.paymentgateway.MSwipeGateway;
import com.frshlypos.paymentgateway.MyLogger;
import com.frshlypos.paymentgateway.PaymentGateway;
import com.frshlypos.paymentgateway.PaymentGatewayFactory;
import com.frshlypos.paymentgateway.PaymentRequest;
import com.frshlypos.retrofitservice.APIService;
import com.frshlypos.retrofitservice.ServiceFactory;
import com.frshlypos.ui.DialogFragment.SettingDiaFragment;
import com.frshlypos.ui.fragments.ProductsFragment;
import com.frshlypos.utils.AppConstants;
import com.frshlypos.utils.CircleAnimationUtil;
import com.frshlypos.utils.Logger;
import com.frshlypos.utils.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends BaseActivity implements View.OnLongClickListener, View.OnClickListener, OnOrderUpdated, OnItemClickListener, StockItemAdapter.ProductItemActionListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    Toolbar toolbar;
    ProductsPagerAdapter productsPagerAdapter;
    ActivityMainBinding binding;
    public ArrayList<StockItemModel> listStockLastRemovedItems = new ArrayList<>();
    public JSONObject objStockCount;
    public ArrayList<FormattedServerItemModel> listOutSideItems = new ArrayList<>();
    public ArrayList<ServerItemModelAPI> productList = new ArrayList<>();
    public ArrayList<StockItemModel> listStockItems = new ArrayList<>();
    public LinkedHashMap<Integer, CurrentOrderModel> mapCurrentOrder = new LinkedHashMap<>();
    public LinkedHashMap<Integer, CurrentOrderModel> mapOldOrder = new LinkedHashMap<>();
    OrderAdapter orderAdapter;
    int orderTotalMoney;
    public Socket socket;
    public LinkedHashMap<Integer, FormattedServerItemModel> mapPriceData = new LinkedHashMap<>();
    boolean isShowingDispenserItems = true;
    public int originalQuantity;
    boolean isStockCountEventCalledBefore = false;
    public int runCount;

    SettingDiaFragment settingDialogFragment;
    public AlertDialog dialogCardCheckOut;
    public AlertDialog dialogCheckout;
    public LayoutDialogCardCheckoutBinding mBinderCardCheckOut;
    public LayoutDialogCheckoutBinding mBinderCheckOutDialog;
    private WifiManager.WifiLock wifiLock;
    MyLogger logger;
    /*Payment gateway variables*/
    public IPaymentGateway paymentGateway;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_ONGO_GATEWAY = 1;

    boolean isOngoAuthPerformed = false;
    boolean isSingaporeAuthPerformed = false;
    boolean isMswipeAuthPerformed = false;

    /*Temparory variables to hold order details*/
    String paymentMode = "";
    int savings = 0;
    String mobileNo = "";
    String totalAmount = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        intializeViews();
        collectData();
        connectSocket();
        Logger.e("UUID : ", Util.generateUUID());
        wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "LockTag");
        logger = MyLogger.getInstance();
        setUpPaymentGateways();
    }

    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }


    private void setUpPaymentGateways() {
        String selectedGateway = sessionManager.getDataByKey(AppConstants.PREF_KEY_PAYMENT_GATEWAY_TYPE, "");
        if (selectedGateway.equals("Ongo")) {
            if (sessionManager.getDataByKey(AppConstants.PREF_KEY_MERCHANT_ID, "") != null && sessionManager.getDataByKey(AppConstants.PREF_KEY_MERCHANT_ID, "") != "") {
                PaymentRequest request = new PaymentRequest();
                request.Bluetooth_id = sessionManager.getDataByKey(AppConstants.PREF_KEY_BLUETOOTH_ADDRESS, "");
                request.Bluetooth_name = sessionManager.getDataByKey(AppConstants.PREF_KEY_BLUETOOTH_NAME, "");
                request.Merchant_id = sessionManager.getDataByKey(AppConstants.PREF_KEY_MERCHANT_ID, "");
                request.Terminal_id = sessionManager.getDataByKey(AppConstants.PREF_KEY_TERMINAL_ID, "");

                paymentGateway = null;
                PaymentGateway selectedPayment = PaymentGateway.valueOf(selectedGateway);
                paymentGateway = new PaymentGatewayFactory().paymentGateway(request, selectedPayment, this);
                //String sessionkey =  Getsessionkey(request);
                sessionManager.storeDataByKey(AppConstants.PREF_KEY_SESSION_KEY, "");
            }
        } else if (selectedGateway.equals("SingaporePayment")) {
            if (sessionManager.getDataByKey(AppConstants.PREF_KEY_IP_ADDRESS, "") != "") {
                PaymentRequest request = new PaymentRequest();
                request.Ip_Address = sessionManager.getDataByKey(AppConstants.PREF_KEY_IP_ADDRESS, "");
                request.Port_Number = sessionManager.getDataByKey(AppConstants.PREF_KEY_PORT_NUMBER, "");

                paymentGateway = null;
                PaymentGateway selectedPayment = PaymentGateway.valueOf(selectedGateway);
                paymentGateway = new PaymentGatewayFactory().paymentGateway(request, selectedPayment, this);
            }
        } else if (selectedGateway.equals("MSwipeInterface")) {
            paymentGateway = null;
            PaymentGateway selectedPayment = PaymentGateway.valueOf(selectedGateway);
            paymentGateway = new PaymentGatewayFactory().paymentGateway(null, selectedPayment, this);
        }
    }

    private void collectData() {
        if (FrshlyApp.hasNetwork()) {
            getItemsFromHQURL(false);
        } else {
            showToast("No Network");
        }
        getTestModeStatus();
        getDispenserStatus();
        getStopOrderStatus();
        getRunCount(false);
    }


    private void intializeViews() {
        binding.viewOrderSummary.tvOrderSummary.setOnLongClickListener(this);
        binding.btnBuy.setOnClickListener(this);
        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_DOUBLE_TAP_TO_SHOW_POPUP, true)) {
            binding.tvDoubleTap.setVisibility(View.VISIBLE);
        } else {
            binding.tvDoubleTap.setVisibility(View.INVISIBLE);
        }
    }

    private void setUpOutSideItems() {
        /*if (outSideItemAdapter == null) {
            binding.rvSideItems.setLayoutManager(new GridLayoutManager(this, 3));
            outSideItemAdapter = new OutSideItemAdapter(this, listOutSideItems, this);
            binding.rvSideItems.setAdapter(outSideItemAdapter);
        } else {
            outSideItemAdapter.notifyDataSetChanged();
        }*/

    }

    private void setUpStockItems(boolean isSettingsChanged) {
        if (productsPagerAdapter == null || isSettingsChanged) {
            boolean shouldShowSnacks = sessionManager.getDataByKey(AppConstants.PREF_KEY_SHOW_SNACKS, true);
            productsPagerAdapter = new ProductsPagerAdapter(getSupportFragmentManager(), this, listStockItems, listOutSideItems, this, shouldShowSnacks);
            binding.viewpager.setAdapter(productsPagerAdapter);
            binding.tabLayout.setupWithViewPager(binding.viewpager);
        } else {
            productsPagerAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.tvOrderSummary:
                if (FrshlyApp.getInstance().isWifiConnected()) {
                    showPasswordDialog();
                } else {
                    showToast(getString(R.string.no_network));
                }
                return true;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBuy:
                if (FrshlyApp.getInstance().isWifiConnected()) {
                    if (mapCurrentOrder.size() > 0) {
                        showCheckOutDialog();
                        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_ACCEPT_CREDIT_CARDS, false))
                            initiateConnection();
                    } else {
                        showToast(getString(R.string.please_select_item));
                    }
                } else {
                    showToast(getString(R.string.no_network));
                }

                break;
            case R.id.btnClear:
                if (FrshlyApp.getInstance().isWifiConnected()) {
                    listStockLastRemovedItems.clear();
                    removeOrderLock();
                } else {
                    showToast(getString(R.string.no_network));
                }
                break;
        }
    }

    /**
     * Shows dialog and asks for passwords to modify settings
     */
    private void showPasswordDialog() {
        final EditText etPassword;
        Button btnEnter, btnCancel;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.layout_dialog_password, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        etPassword = (EditText) dialogView.findViewById(R.id.etPassword);
        btnEnter = (Button) dialogView.findViewById(R.id.btnEnter);
        //Change made by Aiman
        btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sPassword = etPassword.getText().toString();
                if (sPassword.equals(sessionManager.getDataByKey(AppConstants.PREF_KEY_SETTINGS_PASSWORD, AppConstants.KEY_DEFAULT_PASSWORD))) {
                    dialog.dismiss();
                    //Change made by Aiman
                    isOngoAuthPerformed = false;
                    isSingaporeAuthPerformed = false;
                    isMswipeAuthPerformed = false;
                    showSettingDialogNew();
                } else {
                    etPassword.setText("");
                    showInformationDialog(getString(R.string.password_incorrect));
                }
            }
        });

        //Change made by Aiman
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    //Change made by Aiman

    /**
     * method to show setting's dialog
     */
    private void showSettingDialogNew() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait");
        settingDialogFragment = new SettingDiaFragment();
        settingDialogFragment.setOnACKListener(new SettingDiaFragment.OnACKListener() {
            @Override
            public void onSaveClick() {
                progressDialog.show();
                clearAllLocks();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        reloadData(true);
                        showToast("Settings successfully saved");
                    }
                }, 3000);
            }

            @Override
            public void onCancelClick() {

            }

            @Override
            public void onClearAllClick() {
                progressDialog.show();
                clearAllLocks();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        reloadData(true);
                        showToast("All locks cleared");
                    }
                }, 3000);
            }

            @Override
            public void OnEmailLogsButtonClicked() {
                mailLogs();
            }

            @Override
            public void OnEditButtonClicked() {
                if (progressDialog != null)
                    progressDialog.show();
                clearAllLocks();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        reloadData(true);
                        showToast("Settings successfully saved");
                    }
                }, 3000);
            }

            @Override
            public void OnAuthenticateButtonClicked(String selectedPaymentMode, String mSwipeUsername, String mSwipePassword) {
                paymentGateway = null;
                PaymentGateway selectedPayment = PaymentGateway.valueOf(selectedPaymentMode);
                paymentGateway = new PaymentGatewayFactory().paymentGateway(null, selectedPayment, MainActivity.this);

                MSwipeGateway.wisePadController.AuthenticateMerchant(MSwipeGateway.loginHandler,
                        mSwipeUsername,
                        mSwipePassword);
                isMswipeAuthPerformed = true;
                showInformationDialog(getString(R.string.device_authenticated_successfully));
            }

            @Override
            public void OnOnGoAuthenticationButtonClicked(String selectedPaymentMode, String merchantId, String terminalId, String bluetoothName, String bluetoothAddress) {
                isOngoAuthPerformed = true;
                PaymentRequest request = new PaymentRequest();
                request.Bluetooth_id = bluetoothAddress;
                request.Bluetooth_name = bluetoothName;
                request.Merchant_id = merchantId;
                request.Terminal_id = terminalId;
                setSelectedPaymentGateway(selectedPaymentMode, request);
                showInformationDialog(getString(R.string.device_authenticated_successfully));
            }

            @Override
            public void OnSingaporeAuthenticationButtonClicked(String selectedPaymentMode, String ipAddress, String portNumber) {
                isSingaporeAuthPerformed = true;
                PaymentRequest request = new PaymentRequest();
                request.Ip_Address = ipAddress;
                request.Port_Number = portNumber;
                setSelectedPaymentGateway(selectedPaymentMode, request);
                showInformationDialog(getString(R.string.device_authenticated_successfully));
            }


            @Override
            public void OnConnectDeviceButtonClicked() {
                initiateConnection();
            }

            @Override
            public void OnBankSummaryButtonClicked() {
                showBankSummary();
            }
        });

        settingDialogFragment.show(getSupportFragmentManager());
    }


    private void reloadData(boolean isSettingsChanged) {
        mapCurrentOrder.clear();
        updateOrderSummary();
        removeOrderLock();
        getItemsFromHQURL(isSettingsChanged);
        // getInitialStockData(isSettingsChanged);
        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_DOUBLE_TAP_TO_SHOW_POPUP, true)) {
            binding.tvDoubleTap.setVisibility(View.VISIBLE);
        } else {
            binding.tvDoubleTap.setVisibility(View.INVISIBLE);
        }
        Log.e("In Reload data", "" + socket.connected());
        if (socket != null && socket.connected()) {
            socket.disconnect();
        }
        connectSocket();
       /* if (outSideItemAdapter != null)
            setUpOutSideItems();
        if (stockItemAdapter != null)
            setUpStockItems();*/
    }


    private void showCheckOutDialog() {
        int lastSelectedPaymentMode;
        float gstPercent = 0;
        float totalGST = 0;
        float totalAmount = 0;
        float totalBillAmount = 0;
        float decimalCount = 0;
        float mrpWithoutTax = 0;
        float price = 0;
        String currencySymbol = sessionManager.getDataByKey(AppConstants.PREF_KEY_CURRENCY, AppConstants.RUPPEE);
        ArrayList<CurrentOrderModel> listCurrentOrder = new ArrayList<>();
        for (Map.Entry<Integer, CurrentOrderModel> entry : mapCurrentOrder.entrySet()) {
            Integer key = entry.getKey();
            CurrentOrderModel model = entry.getValue();
            listCurrentOrder.add(model);
            FormattedServerItemModel priceData = mapPriceData.get(model.getItemId());
            gstPercent = Float.valueOf(priceData.getCgstPercent()) + Float.valueOf(priceData.getCgstPercent());
            mrpWithoutTax = (priceData.getMrp() * 100) / (100 + Float.valueOf(priceData.getCgstPercent()) + Float.valueOf(priceData.getSgstPercent()));
            price = mrpWithoutTax * model.getQuantity();
            totalAmount += price;

            totalBillAmount += priceData.getMrp() * model.getQuantity();
            totalGST += mrpWithoutTax * model.getQuantity() * gstPercent / 100;
        }


        CheckoutItemsAdapter adapter = new CheckoutItemsAdapter(this, listCurrentOrder);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mBinderCheckOutDialog = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_dialog_checkout, null, false);
        builder.setView(mBinderCheckOutDialog.getRoot());
        mBinderCheckOutDialog.rvCheckout.setLayoutManager(new LinearLayoutManager(this));
        mBinderCheckOutDialog.rvCheckout.setAdapter(adapter);
        mBinderCheckOutDialog.tvTotalBeforeTax.setText(currencySymbol + " " + Util.getRoundedValue(totalAmount));
        mBinderCheckOutDialog.tvGSTTaxValue.setText(currencySymbol + " " + Util.getRoundedValue(totalGST));
        mBinderCheckOutDialog.tvTotalIncludingTax.setText(currencySymbol + " " + Util.getRoundedValue(totalBillAmount));
        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE, "India").equalsIgnoreCase("india")) {
//            mBinder.etMobile.setHint("99xxxxxxxx");
            mBinderCheckOutDialog.etMobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        } else {
//            mBinder.etMobile.setHint("99xxxxxx");
            mBinderCheckOutDialog.etMobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        }
        mBinderCheckOutDialog.etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE, "India").equalsIgnoreCase("india")) {
                    if (mBinderCheckOutDialog.etMobile.getText().toString().length() > 9) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mBinderCheckOutDialog.etMobile.getWindowToken(), 0);
                        mBinderCheckOutDialog.etMobile.clearFocus();
                    }

                } else {
                    if (mBinderCheckOutDialog.etMobile.getText().toString().length() > 7) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mBinderCheckOutDialog.etMobile.getWindowToken(), 0);
                        mBinderCheckOutDialog.etMobile.clearFocus();
                    }
                }
            }
        });
        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_MOBILE_MANDATORY, true)) {
            mBinderCheckOutDialog.etMobile.requestFocus();

        }
        dialogCheckout = builder.create();
        dialogCheckout.setCancelable(false);
        dialogCheckout.show();

        int width = getResources().getDimensionPixelSize(R.dimen._400sdp);
        Window _w = dialogCheckout.getWindow();
        if (_w != null) {
            _w.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            _w.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
//            _w.setWindowAnimations(R.style.dialog_window_animations_overshoot);
        }

        final float finalTotalBillAmount = totalBillAmount;

        mBinderCheckOutDialog.rbOthers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    String mobile = mBinderCheckOutDialog.etMobile.getText().toString().trim();
                    if (sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_MOBILE_MANDATORY, true)) {
                        if (mobile.isEmpty()) {
                            showInformationDialog(getString(R.string.please_enter_the_mobile_number));
                            return;
                        } else if (sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE, "India").equalsIgnoreCase("india")) {
                            if (mobile.length() < 10) {
                                showInformationDialog(getString(R.string.mobile_number_is_incorrect));
                                return;
                            }
                        } else {
                            if (mobile.length() < 8) {
                                showInformationDialog(getString(R.string.mobile_number_is_incorrect));
                                return;
                            }
                        }
                    }
                    showPaymentTypeDialog(mobile, Util.getRoundedValue(finalTotalBillAmount), dialogCheckout);
                }
            }
        });

        mBinderCheckOutDialog.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = mBinderCheckOutDialog.etMobile.getText().toString().trim();
                if (!sessionManager.getDataByKey(AppConstants.PREF_KEY_ACCEPT_CASH, true) && !sessionManager.getDataByKey(AppConstants.PREF_KEY_ACCEPT_CREDIT_CARDS, true) && !sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_OTHERS_MANDATORY, true)) {
                    showToast(getString(R.string.select_the_payment_options_from_settings));
                } else {
                    if ((mBinderCheckOutDialog.rbCash.getVisibility() == View.VISIBLE) && mBinderCheckOutDialog.rbCash.isChecked()) {
                        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_TEST_MODE, false)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            final AlertDialog dialog = builder.create();
                            dialog.setMessage(getString(R.string.app_is_running_in_test_mode));
                            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog1, int which) {
                                    dialog.dismiss();
                                    submitPayment("cash", mBinderCheckOutDialog.etMobile.getText().toString().trim(), Util.getRoundedValue(finalTotalBillAmount), dialogCheckout);
                                }
                            });
                            dialog.show();
                        } else {
                            if (sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_MOBILE_MANDATORY, true)) {
                                if (mobile.isEmpty()) {
                                    showInformationDialog(getString(R.string.please_enter_the_mobile_number));
                                    return;
                                } else if (sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE, "India").equalsIgnoreCase("india")) {
                                    if (mobile.length() < 10) {
                                        showInformationDialog(getString(R.string.mobile_number_is_incorrect));
                                        return;
                                    }
                                } else {
                                    if (mobile.length() < 8) {
                                        showInformationDialog(getString(R.string.mobile_number_is_incorrect));
                                        return;
                                    }
                                }
                            }
                            submitPayment("cash", mBinderCheckOutDialog.etMobile.getText().toString().trim(), Util.getRoundedValue(finalTotalBillAmount), dialogCheckout);
                        }
                    } else if (mBinderCheckOutDialog.rbCredit.isChecked()) {
                        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_TEST_MODE, false)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            final AlertDialog dialog = builder.create();
                            dialog.setMessage(getString(R.string.app_is_running_in_test_mode));
                            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog1, int which) {
                                    proceedCardPayment("card", mBinderCheckOutDialog.etMobile.getText().toString().trim(), Util.getRoundedValue(finalTotalBillAmount), dialog);
                                }
                            });
                            dialog.show();
                        } else {
                            //proceedCardPayment("card", mBinderCheckOutDialog.etMobile.getText().toString().trim(), Util.getRoundedValue(finalTotalBillAmount), dialogCheckout);
                            proceedCardPayment("card", mBinderCheckOutDialog.etMobile.getText().toString().trim(), Util.getRoundedValue(finalTotalBillAmount), dialogCheckout);
                        }
                    } else if (mBinderCheckOutDialog.rbOthers.isChecked()) {
                        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_MOBILE_MANDATORY, true)) {
                            if (mobile.isEmpty()) {
                                showInformationDialog(getString(R.string.please_enter_the_mobile_number));
                                return;
                            } else if (sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE, "India").equalsIgnoreCase("india")) {
                                if (mobile.length() < 10) {
                                    showInformationDialog(getString(R.string.mobile_number_is_incorrect));
                                    return;
                                }
                            } else {
                                if (mobile.length() < 8) {
                                    showInformationDialog(getString(R.string.mobile_number_is_incorrect));
                                    return;
                                }
                            }
                        }
                        showPaymentTypeDialog(mobile, Util.getRoundedValue(finalTotalBillAmount), dialogCheckout);
                    } else {
                        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_MOBILE_MANDATORY, true)) {
                            if (mobile.isEmpty()) {
                                showInformationDialog(getString(R.string.please_enter_the_mobile_number));
                                return;
                            } else if (sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE, "India").equalsIgnoreCase("india")) {
                                if (mobile.length() < 10) {
                                    showInformationDialog(getString(R.string.mobile_number_is_incorrect));
                                    return;
                                }
                            } else {
                                if (mobile.length() < 8) {
                                    showInformationDialog(getString(R.string.mobile_number_is_incorrect));
                                    return;
                                }
                            }
                        }
                        showToast("Please select payment mode");
                    }
                }


            }
        });

        mBinderCheckOutDialog.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCheckout.dismiss();
                if (sessionManager.getDataByKey(AppConstants.PREF_KEY_ACCEPT_CREDIT_CARDS, false)) {
                    disconnectDevice();
                }
            }
        });
        mBinderCheckOutDialog.rbCash.setSelected(false);
        mBinderCheckOutDialog.rbCredit.setSelected(false);
        mBinderCheckOutDialog.rbOthers.setSelected(false);


        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_ACCEPT_CASH, true)) {
            mBinderCheckOutDialog.rbCash.setSelected(true);
        } else if (sessionManager.getDataByKey(AppConstants.PREF_KEY_ACCEPT_CREDIT_CARDS, true)) {
            mBinderCheckOutDialog.rbCredit.setSelected(true);
        } else {
            mBinderCheckOutDialog.rbOthers.setSelected(true);
        }
        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_ACCEPT_CASH, true))
            mBinderCheckOutDialog.rbCash.setVisibility(View.VISIBLE);
        else
            mBinderCheckOutDialog.rbCash.setVisibility(View.GONE);

        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_ACCEPT_CREDIT_CARDS, true))
            mBinderCheckOutDialog.rbCredit.setVisibility(View.VISIBLE);
        else
            mBinderCheckOutDialog.rbCredit.setVisibility(View.GONE);

        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_OTHERS_MANDATORY, true))
            mBinderCheckOutDialog.rbOthers.setVisibility(View.VISIBLE);
        else
            mBinderCheckOutDialog.rbOthers.setVisibility(View.GONE);

        if (!sessionManager.getDataByKey(AppConstants.PREF_KEY_ACCEPT_CASH, true) && !sessionManager.getDataByKey(AppConstants.PREF_KEY_ACCEPT_CREDIT_CARDS, true) && !sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_OTHERS_MANDATORY, true)) {
            mBinderCheckOutDialog.rgPaymentMethod.setVisibility(View.INVISIBLE);
            mBinderCheckOutDialog.tvSelectPaymentOption.setVisibility(View.VISIBLE);
        } else {
            mBinderCheckOutDialog.rgPaymentMethod.setVisibility(View.VISIBLE);
            mBinderCheckOutDialog.tvSelectPaymentOption.setVisibility(View.INVISIBLE);
        }

    }

    private void showPaymentTypeDialog(final String mobile, final String total, final AlertDialog dialog) {
        final LayoutDialogPaymentTypeBinding binding;
        final String[] selectedGateway = {""};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_dialog_payment_type, null, false);
        builder.setView(binding.getRoot());
        final AlertDialog dialogPaymentType = builder.create();
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPaymentType.dismiss();
            }
        });
        binding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_TEST_MODE, false)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    final AlertDialog dialog1 = builder.create();
                    dialog1.setMessage(getString(R.string.app_is_running_in_test_mode));
                    dialog1.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog1, int which) {
                            submitPayment(selectedGateway[0], mobile, total, dialog);
                            dialog1.dismiss();
                            dialog.dismiss();
                            dialogPaymentType.dismiss();
                        }
                    });
                    dialog1.show();
                } else {
                    switch (binding.toggleButtonGroup.getCheckedRadioButtonId()) {
                        case R.id.rbSodexoCard:
                            selectedGateway[0] = "Sodexo Card";
                            break;
                        case R.id.rbSodexoCoupon:
                            selectedGateway[0] = "Sodexo Coupon";
                            break;
                        case R.id.rbGPRSCard:
                            selectedGateway[0] = "GPRS Card";
                            break;
                        case R.id.rbWallet:
                            selectedGateway[0] = "Wallet";
                            break;
                        case R.id.rbCredit:
                            selectedGateway[0] = "Credit";
                            break;
                        default:
                            showToast("Please select payment option");
                            return;
                    }
                    submitPayment(selectedGateway[0], mobile, total, dialog);
                    dialogPaymentType.dismiss();
                }
            }
        });
        dialogPaymentType.setCancelable(false);
        dialogPaymentType.show();
    }


    private void getItemsFromHQURL(final boolean isSettingsChanged) {
        final APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getHQURL());
        Log.e("HQ URL USED", Util.getHQURL());
        //service.getItems(sessionManager.getDataByKey(AppConstants.PREF_KEY_OUTLET_ID, AppConstants.KEY_DEFAULT_OUTLET_ID))
        service.getItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {

                    @Override
                    public void onStart() {
                        showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        unsubscribe();
                        hideProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        hideProgressDialog();
                        mapPriceData.clear();
                        updateOrderSummary();
                        populateSideItems(isSettingsChanged);
                    }

                    @Override
                    public void onNext(ResponseBody jsonArray) {
                        Type listType = new TypeToken<ArrayList<ServerItemModelAPI>>() {
                        }.getType();
                        try {
                            productList = new GsonBuilder().create().fromJson(jsonArray.string(), listType);
                            Collections.sort(productList, new Comparator<ServerItemModelAPI>() {
                                @Override
                                public int compare(ServerItemModelAPI lhs, ServerItemModelAPI rhs) {
                                    if (lhs.getId() == rhs.getId()) return 0;
                                    else if (lhs.getId() > rhs.getId()) return 1;
                                    return -1;
                                }
                            });
                            mapPriceData.clear();
                            mapPriceData.putAll(Util.mapServerDataById(productList));
                            updateOrderSummary();
                            populateSideItems(isSettingsChanged);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });
    }

    private void populateSideItems(boolean isSettingsChanged) {
        listOutSideItems.clear();
        if (mapPriceData.size() < 1)
            return;
        for (Map.Entry<Integer, FormattedServerItemModel> entry : mapPriceData.entrySet()) {
            Integer key = entry.getKey();
            Logger.i("ITEM ID FROM HASH MAP= ", "" + key);
            FormattedServerItemModel model = entry.getValue();
            model.setItemId(key);
            if (model.getLocation().equalsIgnoreCase("outside") && Util.shouldItemBeVisible(key))
                listOutSideItems.add(model);
        }

        // setUpOutSideItems();
        getInitialStockData(isSettingsChanged);
    }

    @Override
    public void orderUpdated() {
        updateOrderSummary();
    }

    private void updateOrderSummary() {
        int totalAmount = 0;
        int price;
        ArrayList<CurrentOrderModel> listCurrentOrder = new ArrayList<>();
        for (Map.Entry<Integer, CurrentOrderModel> entry : mapCurrentOrder.entrySet()) {
            Integer key = entry.getKey();
            CurrentOrderModel model = entry.getValue();
            listCurrentOrder.add(model);

            if (Util.isTestModeItem(key)) {
                price = 1;
            } else {
                price = mapPriceData.get(key).getMrp() * model.getQuantity();
            }
            totalAmount += price;
        }
        orderAdapter = new OrderAdapter(this, listCurrentOrder, this, this);
        binding.viewOrderSummary.rvOrderedItems.setLayoutManager(new LinearLayoutManager(this));
        binding.viewOrderSummary.rvOrderedItems.setAdapter(orderAdapter);
        if (totalAmount != 0)
            binding.viewOrderSummary.tvTotal.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_CURRENCY, AppConstants.RUPPEE) + " " + Util.getRoundedValue(totalAmount));
        else
            binding.viewOrderSummary.tvTotal.setText("");

        if (listCurrentOrder.size() > 0) {
            binding.viewOrderSummary.rvOrderedItems.scrollToPosition(listCurrentOrder.size() - 1);
            binding.viewOrderSummary.rvOrderedItems.setVisibility(View.VISIBLE);
            binding.viewOrderSummary.llEmptyCart.setVisibility(View.GONE);
            binding.viewOrderSummary.rlTotal.setVisibility(View.VISIBLE);
        } else {
            binding.viewOrderSummary.rvOrderedItems.setVisibility(View.GONE);
            binding.viewOrderSummary.llEmptyCart.setVisibility(View.VISIBLE);
            binding.viewOrderSummary.rlTotal.setVisibility(View.GONE);
        }


    }

    @Override
    public void selectedItem(int itemID, String location) {
        if (location.equalsIgnoreCase("dispenser") && ((ProductsFragment) productsPagerAdapter.getItem(0)).stockItemAdapter != null) {
            ((ProductsFragment) productsPagerAdapter.getItem(0)).stockItemAdapter.showAddItemDialogOnOrderClick(itemID);
        } else if (((ProductsFragment) productsPagerAdapter.getItem(1)).outSideItemAdapter != null) {
            ((ProductsFragment) productsPagerAdapter.getItem(1)).outSideItemAdapter.showAddItemDialogOnOrderClick(itemID);
        }
    }

    private void connectSocket() {
        try {
            IO.Options options = new IO.Options();
            options.timeout = 3000;
            socket = IO.socket(Util.getWebSocketURL(), options);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Logger.i("Socket Connection :", "Established");
                }
            }).on("stock_count", new Emitter.Listener() {
                @Override
                public void call(Object args[]) {
                    if (isStockCountEventCalledBefore) {
                        try {
                            if (args[0] == null)
                                return;
                            final JSONObject jsonObject = new JSONObject(args[0].toString());
                            Logger.i("STOCK COUNT : ", jsonObject.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mapPriceData != null && mapPriceData.size() > 0)
                                        handleStockData(jsonObject, false);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    isStockCountEventCalledBefore = true;
                }
            }).on("stop_orders", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Logger.i("Received order taking signal - " + args[0].toString());
                    final boolean isOrderingStopped = Boolean.valueOf(args[0].toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleStopOrderLayout(isOrderingStopped);
                        }
                    });

                }
            }).on("reconnect", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Logger.i("Reconnected to outlet.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getRunCount(true);
                            getTestModeStatus();
                            getDispenserStatus();
                            getStopOrderStatus();
                        }
                    });

                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("Socket Connection :", "Disconnected");
                }
            }).on("beverage_items", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args[0] == null)
                        return;
                    JSONArray arrItems;
                    try {
                        arrItems = new JSONArray(args[0].toString());
                        for (int i = 0; i < arrItems.length(); i++) {
                            JSONObject obj = arrItems.optJSONObject(i);
                            FrshlyApp.getInstance().sessionManager.storeDataByKey(obj.optString("id") + "_visibility", obj.optBoolean("visible"));
                        }
                        populateSideItems(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).on("test_mode", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Logger.i("Received the test mode signal -" + args[0].toString());
                    sessionManager.storeDataByKey(AppConstants.PREF_KEY_IS_TEST_MODE, args[0].toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                            //reloadData(true);
                        }
                    });

                }
            }).on("dispenser_empty", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        boolean emptyFlag = Boolean.parseBoolean(args[0].toString());
                        Logger.i("Received dispenser empty signal -" + args[0].toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).on("order_delay", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final boolean orderFlag = Boolean.parseBoolean(args[0].toString());
                        Logger.i("Received order delay signal - " + args[0].toString());
                        sessionManager.storeDataByKey(AppConstants.PREF_KEY_IS_ORDER_DELAY, orderFlag);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toggleDispenserStatusLayout(orderFlag);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private void getTestModeStatus() {
        final APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
        service.getTestModeStatus()
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

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String testMode = responseBody.string();
                            Logger.i("Received initial test mode flag as - " + testMode);
                            sessionManager.storeDataByKey(AppConstants.PREF_KEY_IS_TEST_MODE, Boolean.valueOf(testMode));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * get Dispenser status
     */
    private void getDispenserStatus() {
        final APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
        service.getDispenserStatus()
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

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String dispenserStatus = responseBody.string();
                            Logger.i("Received dispenser status as - ", dispenserStatus);
                            if (dispenserStatus.equalsIgnoreCase("working")) {
                                sessionManager.storeDataByKey(AppConstants.PREF_KEY_IS_ORDER_DELAY, false);
                            } else if (dispenserStatus.equalsIgnoreCase("loading")) {
                                sessionManager.storeDataByKey(AppConstants.PREF_KEY_IS_ORDER_DELAY, true);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * get status whether ordering is stopped or not
     */
    private void getStopOrderStatus() {
        final APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
        service.getStopOrdersState()
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
                        toggleStopOrderLayout(false);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String stopOrderStatus = responseBody.string();
                            Logger.i("Received stop order status - ", stopOrderStatus);
                            toggleStopOrderLayout(Boolean.valueOf(responseBody.string()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getRunCount(final boolean isReconnect) {
        final APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
        service.getRunCount()
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
                        Logger.e("Getting run count failed");
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject object = new JSONObject(responseBody.string());
                            Logger.i("Got Run count : " + object.optInt("run_count"));
                            runCount = object.optInt("run_count");

                            if (isReconnect) {
                                if (runCount != object.optInt("run_count") && mapCurrentOrder.size() != 0) {
                                    removeOrderLock();
                                    showInformationDialog("System is restarting. Please order from scratch.");
                                }
                            } else {
                                runCount = object.optInt("run_count");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void removeOrderLock() {
        Logger.e("CURRENT ORDER------" + mapCurrentOrder.toString());
        ArrayList<Integer> itemsToBeRemoved = new ArrayList<>();
        for (Map.Entry<Integer, CurrentOrderModel> entry : mapCurrentOrder.entrySet()) {
            final Integer key = entry.getKey();
            CurrentOrderModel orderModel = entry.getValue();

            if (Util.isTestModeItem(key)) {
                final APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
                TryLockModel model = new TryLockModel();
                model.setDelta_count(orderModel.getQuantity());
                model.setDirection("decrease");
                service.lockItem("" + key, model)
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
                                Logger.e("Error occured while removing the lock for item- " + key);
                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                Logger.e("Successfully removed lock for- " + key);
                            }
                        });

                return;
            }
            if (orderModel.getLocation().equalsIgnoreCase("dispenser")) {
                final APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
                TryLockModel model = new TryLockModel();
                model.setDelta_count(orderModel.getQuantity());
                model.setDirection("decrease");
                service.lockItem("" + key, model)
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
                                Logger.e("Error occured while removing the lock for item- " + key);
                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                Logger.e("Successfully removed lock for- " + key);
                            }
                        });
            }
            itemsToBeRemoved.add(key);
        }
        for (int i = 0; i < itemsToBeRemoved.size(); i++) {
            mapCurrentOrder.remove(itemsToBeRemoved.get(i));
        }
        updateOrderSummary();

    }


    /**
     * gets stock data from local server
     */
    private void getInitialStockData(final boolean isSettingsChanged) {
        final APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
        service.getInitialStockData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {

                    @Override
                    public void onStart() {
                        Logger.d("OUTLET URL", Util.getOutletURL());
                    }

                    @Override
                    public void onCompleted() {
                        unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject object = new JSONObject(responseBody.string());
                            handleStockData(object, isSettingsChanged);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void handleStockData(JSONObject object, boolean isSettingsChanged) {
        objStockCount = object;
        Map sortedObject = sortStockData(object);
        Logger.i("sorted Object:" + sortedObject);
        Iterator<String> iter = new ArrayList<>(sortedObject.keySet()).iterator();
        Logger.i("Returned  Keys" + iter);

        listStockItems.clear();
        listStockLastRemovedItems.clear();
        while (iter.hasNext()) {
            StockItemModel stockItemModel = new StockItemModel();
            String key = iter.next();
            Logger.i("Keys sorted:" + key);
            try {
                InitialStockDataModelAPI model = (InitialStockDataModelAPI) sortedObject.get(key);
                int displayableCount = 0;
                if (Util.getStockItemCount(model.getItemDetails()) > model.getLockedCount()) {
                    displayableCount = Util.getStockItemCount(model.getItemDetails()) - model.getLockedCount();
                }
                stockItemModel.setItemCode(Integer.parseInt(key));
                if (displayableCount > 0) {

                    if (Util.isTestModeItem(Integer.parseInt(key)) && sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_TEST_MODE, false) && displayableCount > 0) {
                        FormattedServerItemModel formattedServerItemModel = new FormattedServerItemModel();
                        formattedServerItemModel.setStockQuantity(displayableCount);
                        formattedServerItemModel.setItemId(Integer.parseInt(key));
                        formattedServerItemModel.setLocation("dispenser");
                        formattedServerItemModel.setMrp(1);
                        formattedServerItemModel.setCgstPercent("1");
                        formattedServerItemModel.setSgstPercent("1");
                        stockItemModel.setFormattedServerItemModel(formattedServerItemModel);
                        listStockItems.add(stockItemModel);
                    } else {

                        FormattedServerItemModel formattedServerItemModel = mapPriceData.get(Integer.parseInt(key));
                        formattedServerItemModel.setStockQuantity(displayableCount);
                        stockItemModel.setFormattedServerItemModel(formattedServerItemModel);
                        if (!Util.isTestModeItem(Integer.parseInt(key))) {
                            listStockItems.add(stockItemModel);
                        }
                    }
                } else {
                    FormattedServerItemModel formattedServerItemModel = mapPriceData.get(Integer.parseInt(key));
                    formattedServerItemModel.setStockQuantity(displayableCount);
                    stockItemModel.setFormattedServerItemModel(formattedServerItemModel);
                    listStockLastRemovedItems.add(stockItemModel);
                    //stockItemModelLastRemoved = stockItemModel;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setUpStockItems(isSettingsChanged);
    }

    private Map sortStockData(JSONObject stockData) {
        JSONObject jObject = new JSONObject();
        InitialStockDataModelAPI initialStockDataModelAPI;
        Map resultArray = new LinkedHashMap();
        try {
            ArrayList<JSONObject> result = SortComboListing(stockData);

            Logger.i("SORTED STOCK  SORT COMBOLISTING " + result);
            for (int i = 0; i < result.size(); i++) {
                JSONObject json = result.get(i);
                String key = json.getString("item_id");
                Type listType = new TypeToken<InitialStockDataModelAPI>() {
                }.getType();
                JSONObject object = (JSONObject) stockData.get(key);
                initialStockDataModelAPI = new GsonBuilder().create().fromJson(object.toString(), listType);
                Logger.i("SORTED STOCK DATA" + key);
                resultArray.put("" + key, initialStockDataModelAPI);
                jObject.put("" + key, initialStockDataModelAPI);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.i("Returned  STOCK DATA" + resultArray);
        return resultArray;
    }

    // Sorting for Combo Listing
    public ArrayList<JSONObject> SortComboListing(JSONObject stockData) {
        ArrayList<JSONObject> array = new ArrayList<JSONObject>();
        Iterator x = stockData.keys();
        JSONArray jsonArray = new JSONArray();
        try {

            while (x.hasNext()) {
                String key = (String) x.next();
                JSONObject json = stockData.getJSONObject(key);
                json.put("item_id", key);
                array.add(json);
                // Logger.i("SORTED STOCK json object lhs Key:"+key+"  " +stockData.getJSONObject(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(array, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                // TODO Auto-generated method stub

                try {
                    Logger.i("SORTED STOCK DATA2222222222 lhs " + lhs.getDouble("conversion"));
                    Logger.i("SORTED STOCK DATA2222222222 rhs" + rhs.getDouble("conversion"));

                    // return  Float.valueOf(lhs.getDouble("conversion").compareTo(rhs.getDouble("conversion")));
                    /*if (lhs.getDouble("conversion") != rhs.getDouble("conversion")) {
                        if (lhs.getDouble("conversion") > rhs.getDouble("conversion")) {
                            return 0;
                        } else {
                            return -1;
                        }
                    } else if (lhs.getInt("stockqty") != rhs.getInt("stockqty")) {
                        if (lhs.getInt("stockqty") > rhs.getInt("stockqty")) {
                            return -1;
                        } else

                        {
                            return 0;
                        }
                    }*/
                    if (lhs.getDouble("conversion") != rhs.getDouble("conversion")) {
                        if (lhs.getDouble("conversion") < rhs.getDouble("conversion")) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                    else
                    if (lhs.getInt("soldqty") != rhs.getInt("soldqty")) {
                        if (lhs.getInt("soldqty") > rhs.getInt("stockqty")) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                    else
                    if (lhs.getInt("stockqty") != rhs.getInt("stockqty")) {
                        if (lhs.getInt("stockqty") > rhs.getInt("stockqty")) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                    else if (lhs.getInt("sold") != rhs.getInt("sold")) {
                        if (lhs.getInt("sold") < rhs.getInt("sold")) {
                            return -1;
                        } else {
                            return 1;
                        }
                    } else if (lhs.getInt("veg") != rhs.getInt("veg")) {
                        if (lhs.getInt("veg") < rhs.getInt("veg")) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else {
                        return lhs.getInt("item_id") - rhs.getInt("item_id");
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 0;
                }
               // return 0;
            }
        });


        return array;
    }

    public void toggleStopOrderLayout(boolean state) {
        if (state) {
            binding.tvOrderStop.setVisibility(View.VISIBLE);
            binding.btnBuy.setOnClickListener(null);
            binding.btnClear.setOnClickListener(null);
            binding.cardButtons.setVisibility(View.GONE);
        } else {
            binding.tvOrderStop.setVisibility(View.GONE);
            binding.btnBuy.setOnClickListener(this);
            binding.btnClear.setOnClickListener(this);
            binding.cardButtons.setVisibility(View.VISIBLE);
        }
    }

    // new function for showing dispenser status
    public void toggleDispenserStatusLayout(boolean state) {
        if (state) {
            binding.dispenserLoading.setVisibility(View.VISIBLE);
            binding.btnBuy.setOnClickListener(null);
            binding.btnClear.setOnClickListener(null);
            binding.cardButtons.setVisibility(View.GONE);
        } else {
            binding.dispenserLoading.setVisibility(View.GONE);
            binding.btnBuy.setOnClickListener(this);
            binding.btnClear.setOnClickListener(this);
            binding.cardButtons.setVisibility(View.VISIBLE);
        }
    }

    private void proceedCardPayment(String paymentMode, String mobile, String total, AlertDialog dialog) {
        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_MOBILE_MANDATORY, true)) {
            if (mobile.isEmpty()) {
                showInformationDialog(getString(R.string.please_enter_the_mobile_number));
                return;
            } else if (sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE, "India").equalsIgnoreCase("india")) {
                if (mobile.length() < 10) {
                    showInformationDialog(getString(R.string.mobile_number_is_incorrect));
                    return;
                }
            } else {
                if (mobile.length() < 8) {
                    showInformationDialog(getString(R.string.mobile_number_is_incorrect));
                    return;
                }
            }
        }
        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_PAYMENT_GATEWAY_TYPE, "--Select--").equalsIgnoreCase("--Select--")) {
            showInformationDialog("Please select Payment Gateway from the settings");
            return;
        }
        savings = 0;
        this.paymentMode = paymentMode;
        mobileNo = mobile;
        totalAmount = total;
        showCardCheckOutDialog();
        dialog.dismiss();

    }

    private void submitPayment(String paymentMode, String mobile, String total, AlertDialog dialog) {
        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_MOBILE_MANDATORY, true)) {
            if (mobile.isEmpty()) {
                showInformationDialog(getString(R.string.please_enter_the_mobile_number));
            } else if (sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE, "India").equalsIgnoreCase("india")) {
                if (mobile.length() < 10) {
                    showInformationDialog(getString(R.string.mobile_number_is_incorrect));
                    return;
                }

            } else {
                if (mobile.length() < 8) {
                    showInformationDialog(getString(R.string.mobile_number_is_incorrect));
                    return;
                }
            }
        }
        savings = 0;
        mobileNo = mobile;
        this.paymentMode = paymentMode;
        totalAmount = total;
        pushOrder(paymentMode, 0, mobile, total, "", "");
        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_ACCEPT_CREDIT_CARDS, false)) {
            disconnectDevice();
        }
        dialog.dismiss();
    }

    private void pushOrder(String paymentMode, int savings, String mobile, final String total, String creditCardNo, String cardHolderName) {
        Logger.i("Push order called...................");
        ArrayList<Integer> itemsToBeRemoved = new ArrayList<>();
        JsonObject orderToSend = new JsonObject();
        JsonObject orderToSendSides = new JsonObject();
        JsonObject finalOrder = new JsonObject();
        mapOldOrder.putAll(mapCurrentOrder);
        for (Map.Entry<Integer, CurrentOrderModel> entry : mapCurrentOrder.entrySet()) {
            Integer key = entry.getKey();
            CurrentOrderModel model = entry.getValue();
            if (model.getLocation().equalsIgnoreCase("dispenser")) {
                if (Util.isTestModeItem(key)) {
                    JsonObject object = new JsonObject();
                    try {
                        object.addProperty("count", model.getQuantity());
                        object.addProperty("price", 1 * model.getQuantity());
                        object.addProperty("heating_flag", false);
                        object.addProperty("heating_reduction", 1);
                        object.addProperty("name", "" + key);
                        JsonObject objRestaurant = new JsonObject();
                        objRestaurant.addProperty("id", 1);
                        objRestaurant.addProperty("tin_no", 1212);
                        objRestaurant.addProperty("st_no", 1212);
                        objRestaurant.addProperty("name", "dummy");
                        objRestaurant.addProperty("cgst_percent", "1");
                        objRestaurant.addProperty("sgst_percent", "1");
                        object.add("restaurant_details", objRestaurant);
                        object.addProperty("side_order", "no side order");
                        object.addProperty("veg", mapPriceData.get(key).getVeg());
                        object.addProperty("vending", mapPriceData.get(key).getVending());
                        object.addProperty("subitem_id", mapPriceData.get(key).getSubitem_id());
                        orderToSend.add("" + key, object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    JsonObject object = new JsonObject();
                    try {
                        object.addProperty("count", model.getQuantity());
                        object.addProperty("price", mapPriceData.get(key).getMrp() * model.getQuantity());
                        object.addProperty("heating_flag", mapPriceData.get(key).getHeatingReqd());
                        object.addProperty("heating_reduction", mapPriceData.get(key).getHeatingReduction());
                        object.addProperty("condiment_slot", mapPriceData.get(key).getCondimentSlot());
                        object.addProperty("name", mapPriceData.get(key).getName());
                        object.add("restaurant_details", getRestaurantDetailsJson(mapPriceData.get(key).getRestaurantDetails()));
                        object.addProperty("side_order", mapPriceData.get(key).getSideOrder());
                        object.addProperty("veg", mapPriceData.get(key).getVeg());
                        object.addProperty("vending", mapPriceData.get(key).getVending());
                        object.addProperty("subitem_id", mapPriceData.get(key).getSubitem_id());
                        orderToSend.add("" + key, object);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                try {
                    JsonObject objectSides = new JsonObject();
                    objectSides.addProperty("name", mapPriceData.get(key).getName());
                    objectSides.addProperty("count", model.getQuantity());
                    objectSides.addProperty("price", mapPriceData.get(key).getMrp() * model.getQuantity());
                    objectSides.add("restaurant_details", getRestaurantDetailsJson(mapPriceData.get(key).getRestaurantDetails()));
                    objectSides.addProperty("side_order", mapPriceData.get(key).getSideOrder());
                    objectSides.addProperty("veg", mapPriceData.get(key).getVeg());
                    objectSides.addProperty("vending", mapPriceData.get(key).getVending());
                    objectSides.addProperty("subitem_id", mapPriceData.get(key).getSubitem_id());
                    orderToSendSides.add("" + key, objectSides);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            itemsToBeRemoved.add(key);
        }
        for (int i = 0; i < itemsToBeRemoved.size(); i++) {
            mapCurrentOrder.remove(itemsToBeRemoved.get(i));
        }
        updateOrderSummary();
        finalOrder.add("order", orderToSend);
        finalOrder.add("sides", orderToSendSides);
        finalOrder.addProperty("counter_code", sessionManager.getValueFromKey(AppConstants.PREF_KEY_COUNTER_CODE, AppConstants.KEY_DEFAULT_COUNTER_CODE));
        finalOrder.addProperty("mode", paymentMode);
        finalOrder.addProperty("from_counter", false);
        finalOrder.addProperty("savings", savings);
        finalOrder.addProperty("mobile_num", mobile);
        finalOrder.addProperty("credit_card_no", creditCardNo);
        finalOrder.addProperty("cardholder_name", cardHolderName);
        finalOrder.addProperty("test_mode", sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_TEST_MODE, false));
        finalOrder.addProperty("unique_Random_Id", Util.generateUUID());
        finalOrder.addProperty("countrytype", sessionManager.getValueFromKey(AppConstants.PREF_KEY_COUNTRY_TYPE, "India"));

        final APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
        service.pushOrder(finalOrder)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {

                    @Override
                    public void onStart() {
                        showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        unsubscribe();
                        hideProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        hideProgressDialog();
                        mapCurrentOrder = mapOldOrder;
                        updateOrderSummary();
                        showFailureScreen("Outlet seems to have connectivity issues");
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            showOrderSuccessDialog(jsonObject.optInt("bill_no"), total);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


    }

    private void showOrderSuccessDialog(int billNo, String totalAmount) {
        LayoutDialogOrderPlacedCashCheckoutBinding binding;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        binding = DataBindingUtil.inflate(LayoutInflater.from(MainActivity.this), R.layout.layout_dialog_order_placed_cash_checkout, null, false);
        builder.setView(binding.getRoot());
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.drawable_border_round_3sdp_transparent));


        SpannableStringBuilder sb = new SpannableStringBuilder("Your order no. " + billNo + " was successful !");
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(bss, 14, 15 + (String.valueOf(billNo).length()), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(new AbsoluteSizeSpan(getResources().getDimensionPixelSize(R.dimen._20sdp)), 14, 15 + (String.valueOf(billNo).length()), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        binding.tvOrderNumber.setText(sb);

        //String s = "Your order no. " + billNo + " was successful !";
        //SpannableString ss1 = new SpannableString(s);
        //ss1.setSpan(new RelativeSizeSpan(getResources().getDimension(R.dimen.font_24)), 14, 14 + (String.valueOf(billNo).length()), 0); // set size
        //ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 14, 14 + (String.valueOf(billNo).length()), 0);// set color
        // binding.tvOrderNumber.setText(ss1);
        //binding.tvOrderNumber.setText("Your order no. " + billNo + " was successful !");
        //binding.tvProceedPayment.setText("Please Proceed the payment! " + sessionManager.getDataByKey(AppConstants.PREF_KEY_CURRENCY, AppConstants.DOLLOR) + " " + totalAmount);
        binding.tvPrice.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_CURRENCY, AppConstants.RUPPEE) + " " + totalAmount);
        dialog.setCancelable(false);
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 5000);
    }

    private JsonObject getRestaurantDetailsJson(RestaurantDetails details) {
        Gson gson = new Gson();
        return new Gson().fromJson(gson.toJson(details), JsonObject.class);
    }

    private void clearAllLocks() {
        final APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
        service.getInitialStockData()
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

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject object = new JSONObject(responseBody.string());
                            Iterator<String> iter = object.keys();
                            while (iter.hasNext()) {
                                final String key = iter.next();
                                int lockedCount = object.optJSONObject(key).optInt("locked_count");
                                String mobileLockedCount = object.optJSONObject(key).optString("mobile_locked_count");
                                int deltaCount = lockedCount - (mobileLockedCount.matches("\\d+(?:\\.\\d+)?") ? Integer.parseInt(mobileLockedCount) : 0);
                                final APIService service = ServiceFactory.createRetrofitService(APIService.class, Util.getOutletURL());
                                TryLockModel model = new TryLockModel();
                                model.setDelta_count(deltaCount);
                                model.setDirection("decrease");
                                service.lockItem("" + key, model)
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
                                                Logger.e("Error occured while removing the lock for item- " + key);
                                            }

                                            @Override
                                            public void onNext(ResponseBody responseBody) {
                                                Logger.e("Successfully removed lock for- " + key);
                                            }
                                        });
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


    }

    public StockItemModel getStockItemByItemId(int itemId) {
        for (int i = 0; i < listStockItems.size(); i++) {
            if (itemId == listStockItems.get(i).getItemCode()) {
                return listStockItems.get(i);
            }
        }
        return null;
    }

    public StockItemModel getRemovedStockItemByItemId(int itemId) {
        for (int i = 0; i < listStockLastRemovedItems.size(); i++) {
            if (itemId == listStockLastRemovedItems.get(i).getItemCode()) {
                return listStockLastRemovedItems.get(i);
            }
        }
        return null;
    }

    public FormattedServerItemModel getOutSidetemByItemId(int itemId) {
        for (int i = 0; i < listOutSideItems.size(); i++) {
            if (itemId == listOutSideItems.get(i).getItemId()) {
                return listOutSideItems.get(i);
            }
        }
        return null;
    }


    @Override
    public void onItemTap(ImageView imageView) {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            makeFlyAnimation(imageView);
        }


    }

    private void makeFlyAnimation(ImageView targetView) {
        new CircleAnimationUtil().attachActivity(this).setTargetView(targetView).setMoveDuration(400).setDestView(binding.viewOrderSummary.llEmptyCart).startAnimation();
    }


    @Override
    public void onStart() {
        // acquiring the wifilock
        wifiLock.acquire();
        super.onStart();
    }

    @Override
    public void onDestroy() {
        disconnectDevice();
        // releasing the wifilock
        wifiLock.release();
        logger.clearLog(this);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        //TODO: verify this change, else revert
        if (keycode == KeyEvent.KEYCODE_BACK) {
            if (paymentGateway != null) {
                paymentGateway.RevertTransaction();
            }
        }
        return super.onKeyDown(keycode, event);
    }

    // The callback when an exterior activity fulfils its result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            logger.debug(TAG, "RequestCode" + requestCode + " resultCode" + resultCode + "Data from Mpos" + data, this);
            switch (requestCode) {
                case REQUEST_ENABLE_BT:
                    if (resultCode == RESULT_OK) {
                        logger.debug(TAG, "Successfully made a bluetooth connection", this);
                        connectToDevice(false);
                    }
                case REQUEST_ONGO_GATEWAY:
                    if (resultCode == 2) {
                        final String resResult = data.getExtras().getString("Result");
                        logger.debug(TAG, "Failure transaction result" + resResult, this);
                        showFailureScreen(resResult);
                        //showCardFailureScreen(resResult);
                    } else if (resultCode == 0) {
                        String resResult = data.getExtras().getString("Result");
                        if (resResult.contains("Approved")) {
                            logger.debug(TAG, "Swipe card Transaction Successful" + resResult, this);
                            //showSuccessScreen();
                            showSuccessScreen("", "Swipe card Transaction");
                        } else {
                            logger.debug(TAG, "Failure transaction result" + resResult, this);
                            showFailureScreen(resResult);
                        }
                    } else if (resultCode == -1) {
                        String Card_Holder_Name = data.getExtras().getString("Card_Holder_Name");
                        String cardNo = data.getExtras().getString("Result").substring(24, 40);
                        logger.debug(TAG, "Card transaction Successful Card_Holder_Name" + Card_Holder_Name + " Result" + data.getExtras().getString("Result"), this);
                        showSuccessScreen(cardNo, Card_Holder_Name);
                    } else if (resultCode == 3) {
                        String resResult = data.getExtras().getString("Result");
                        logger.debug(TAG, "Failure transaction result" + resResult, this);
                        showFailureScreen(resResult);
                    }
            }
        } catch (Exception e) {
            logger.info(TAG, "Transaction Result Error " + resultCode, this);
        }
    }

    // Helper functions
    // This is called from the UI when user initiates a card transaction
    public void checkCard(String total_money) {
        logger.debug(TAG, "total_money" + total_money, this);
        if (paymentGateway != null) {
            String selectedGateway = sessionManager.getDataByKey(AppConstants.PREF_KEY_PAYMENT_GATEWAY_TYPE, "");
            if (selectedGateway.equals("Ongo")) {
                String sessionkey = sessionManager.getDataByKey(AppConstants.PREF_KEY_SESSION_KEY, "");
                logger.debug(TAG, "Shared Preference session Key " + sessionkey, this);
                if (sessionkey == "" || sessionkey == null || sessionkey == "No Response From Server") {
                    if (sessionManager.getDataByKey(AppConstants.PREF_KEY_MERCHANT_ID, "") != "") {
                        PaymentRequest request = new PaymentRequest();
                        request.Bluetooth_id = sessionManager.getDataByKey(AppConstants.PREF_KEY_BLUETOOTH_ADDRESS, "");
                        request.Bluetooth_name = sessionManager.getDataByKey(AppConstants.PREF_KEY_BLUETOOTH_NAME, "");
                        request.Merchant_id = sessionManager.getDataByKey(AppConstants.PREF_KEY_MERCHANT_ID, "");
                        request.Terminal_id = sessionManager.getDataByKey(AppConstants.PREF_KEY_TERMINAL_ID, "");

                        paymentGateway = null;
                        PaymentGateway selectedPayment = PaymentGateway.valueOf(selectedGateway);
                        paymentGateway = new PaymentGatewayFactory().paymentGateway(request, selectedPayment, this);
                        sessionkey = Getsessionkey(request);
                        logger.debug(TAG, "NEW SESSION KEY FIRST: " + sessionkey, this);
                        sessionManager.storeDataByKey("sessionkey", sessionkey);
                    }
                }
                logger.debug(TAG, "session Key " + sessionkey, this);
                paymentGateway.Pay(total_money, sessionkey);
            } else {
                paymentGateway.Pay(total_money, "");
            }
        }
    }

    public void cancelCheckCard() {
        if (paymentGateway != null) {
            paymentGateway.CancelCheckCard();
        }
    }

    public void setSelectedPaymentGateway(String selectedPaymentGateway, PaymentRequest paymentRequest) {
        paymentGateway = null;
        PaymentGateway selectedPayment = PaymentGateway.valueOf(selectedPaymentGateway);
        paymentGateway = new PaymentGatewayFactory().paymentGateway(paymentRequest, selectedPayment, this);
    }

    // Reading the log file and mailing all the contents through the
    // installed email client
    public void mailLogs() {
        String log_content = logger.ReadFromFile(this);
        log_content = log_content.replaceAll("\\\\n", "\n");
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"atchayamindia@atchayam.in"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Order app logs");
        i.putExtra(Intent.EXTRA_TEXT, log_content);
        try {
            logger.info(TAG, "Sending mail..", this);
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            logger.error(TAG, "There are no mail clients installed", this);
        }
        logger.info(TAG, "Successfully sent mail", this);
    }

    public void showCardSuccessScreen(String cardNo, final String cardholderName) {
        paymentGateway.ShowCardSuccessScreen(cardNo, cardholderName);
    }

    // This is called when some error occurs in the card transaction
    public void showCardFailureScreen(String displayMsg) {
        paymentGateway.ShowCardFailureScreen(displayMsg);
    }

    public void showBankSummary() {
        setUpPaymentGateways();
        if (paymentGateway != null)
            paymentGateway.ShowBankSummary();
    }

    public boolean connectToDevice(boolean duringTransaction) {
        String selectedGateway = sessionManager.getDataByKey(AppConstants.PREF_KEY_PAYMENT_GATEWAY_TYPE, "");
        if (paymentGateway == null)
            return false;
        if (selectedGateway == "MswipeInterface") {
            if (MSwipeGateway.deviceState != DeviceState.CONNECTING
                    && !MSwipeGateway.wisePadController.isDevicePresent()) {
                return paymentGateway.connectToDevice(duringTransaction);
            }
        } else {
            return paymentGateway.connectToDevice(duringTransaction);
        }
        return false;
    }

    public void disconnectDevice() {
        // stopping the bluetooth connection
        if (paymentGateway != null) {
            paymentGateway.DisconnectDevice();
        }
    }

    public void initiateConnection() {
        logger.debug(TAG, "Initiating connection", this);
        logger.debug(TAG, "Connecting to device result is - " + connectToDevice(false), this);
    }


    public String Getsessionkey(PaymentRequest data) {
        String sessionkey = paymentGateway.Getsessionkey(data);
        return sessionkey;
    }

    private void showCardCheckOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mBinderCardCheckOut = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_dialog_card_checkout, null, false);
        builder.setView(mBinderCardCheckOut.getRoot());
        dialogCardCheckOut = builder.create();
        mBinderCardCheckOut.tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.debug(TAG, "Initiating cancel CheckCard", MainActivity.this);
                cancelCheckCard();
                dialogCardCheckOut.dismiss();
            }
        });
        String path = "android.resource://" + getPackageName() + "/" + R.raw.swipe;
        mBinderCardCheckOut.videoView.setVideoURI(Uri.parse(path));
        mBinderCardCheckOut.videoView.start();
        initiateCheckCard(totalAmount, sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_TEST_MODE, false), mobileNo);
        dialogCardCheckOut.setCancelable(false);
        dialogCardCheckOut.show();
    }

    public void initiateCheckCard(String total_money, boolean test_mode, String mobile_no) {
        // XXX: bad hack. Lack of time
        MSwipeGateway.test_mode = test_mode;
        logger.debug(TAG, "Value of test_mode is " + test_mode, this);
        logger.debug(TAG, "Storing the mobile no " + mobile_no, this);
       /* SharedPreferences sharedPref = ((MyActivity)mContext).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MSwipeGateway.WISEPAD_MOBILE_NO, "+91"+mobile_no).commit();*/
        sessionManager.storeDataByKey(MSwipeGateway.WISEPAD_MOBILE_NO, "+91" + mobile_no);
        checkCard(total_money);
    }

    public void showSuccessScreen(String creditCardNo, String cardHolderName) {
        logger.debug(TAG, "-------------------------------showSuccessScreen called " + creditCardNo + " " + cardHolderName, this);
        pushOrder(paymentMode, savings, mobileNo, totalAmount, creditCardNo, cardHolderName);
        if (dialogCardCheckOut != null && dialogCardCheckOut.isShowing())
            dialogCardCheckOut.dismiss();

    }

    public void showFailureScreen(String displayMessage) {
        LayoutDialogCardFailureBinding binding;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_dialog_card_failure, null, false);
        builder.setView(binding.getRoot());
        final AlertDialog dialogCardFailure = builder.create();
        binding.tvTitle.setText("TRANSACTION STATUS");
        binding.tvFailureReason.setText(displayMessage);
        binding.tvOrderNo.setText(" " + sessionManager.getDataByKey(AppConstants.PREF_KEY_CURRENCY, AppConstants.RUPPEE) + " " + totalAmount + " failed.");
        if (dialogCardCheckOut != null && dialogCardCheckOut.isShowing()) {
            dialogCardCheckOut.dismiss();
        }
        binding.tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCardFailure.dismiss();
            }
        });
        dialogCardFailure.setCancelable(false);
        dialogCardFailure.show();
    }

    public void setSettingsStatusText(String text) {
        if (settingDialogFragment != null) {
            settingDialogFragment.setStatusText(text);
        }
    }

    public void showSummary(String saleAmount, String saleCount, String summaryDate) {
        String html = "<div>Sale amount - " + saleAmount + "</div>"
                + "<div>Sale count -" + saleCount + "</div>"
                + "<div>Summary Date -" + summaryDate + "</div>";
        if (settingDialogFragment != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                settingDialogFragment.setStatusText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
            } else {
                settingDialogFragment.setStatusText(Html.fromHtml(html));
            }
        }
    }


    public boolean isOngoAuthPerformed() {
        return isOngoAuthPerformed;
    }

    public boolean isSingaporeAuthPerformed() {
        return isSingaporeAuthPerformed;
    }

    public boolean isMswipeAuthPerformed() {
        return isMswipeAuthPerformed;
    }

}