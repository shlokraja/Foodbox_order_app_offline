package com.frshlypos.ui.fragments;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.frshlypos.R;
import com.frshlypos.databinding.LayoutDialogAdvancedSettingsBinding;
import com.frshlypos.databinding.LayoutDialogBasicSettingsBinding;
import com.frshlypos.databinding.LayoutDialogShowLogsBinding;
import com.frshlypos.paymentgateway.MyLogger;
import com.frshlypos.ui.activities.MainActivity;
import com.frshlypos.utils.AppConstants;
import com.frshlypos.utils.Logger;
import com.frshlypos.utils.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {
    public LayoutDialogBasicSettingsBinding mBasicBinder;
    public LayoutDialogAdvancedSettingsBinding mAdvancedBinder;
    public LayoutDialogShowLogsBinding mShowLogBinder;
    int pos = 0;
    String[] arrCountry;
    String[] arrPaymentGateway;
    String[] arrPaymentGatewayIndia;
    String[] arrPaymentGatewaySingapore;
    MainActivity mainActivity;
    MyLogger logger;

    /**
     * method for creating instance of SettingFragment
     */
    public static SettingFragment newInstance(int pos) {
        SettingFragment settingFragment = new SettingFragment();
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        settingFragment.setArguments(b);
        return settingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger = MyLogger.getInstance();
        if (getArguments() != null) {
            if (getArguments().containsKey("pos")) {
                pos = getArguments().getInt("pos");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        arrCountry = getResources().getStringArray(R.array.arr_countries);
        arrPaymentGateway = new String[]{"-Select-"};
        arrPaymentGatewayIndia = getResources().getStringArray(R.array.arr_payment_modes_india);
        arrPaymentGatewaySingapore = getResources().getStringArray(R.array.arr_payment_modes_singapore);

        switch (pos) {
            case 0:
                mBasicBinder = DataBindingUtil.inflate(inflater, R.layout.layout_dialog_basic_settings, container, false);
                setUpBasicUI();
                return mBasicBinder.getRoot();
            case 1:
                mAdvancedBinder = DataBindingUtil.inflate(inflater, R.layout.layout_dialog_advanced_settings, container, false);
                setUpAdvancedUI();
                return mAdvancedBinder.getRoot();
            case 2:
                mShowLogBinder = DataBindingUtil.inflate(inflater, R.layout.layout_dialog_show_logs, container, false);
                setUpShowLogsUI();
                return mShowLogBinder.getRoot();
            default:
                mBasicBinder = DataBindingUtil.inflate(inflater, R.layout.layout_dialog_basic_settings, container, false);
                setUpBasicUI();
                return mBasicBinder.getRoot();
        }
    }

    /**
     * method to set basic setting UI
     */
    private void setUpBasicUI() {
        //setting values
        mBasicBinder.etSettingsPassword.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_SETTINGS_PASSWORD, AppConstants.KEY_DEFAULT_PASSWORD));
        mBasicBinder.etHqURL.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_HQ_URL, AppConstants.KEY_DEFAULT_HQ_URL));
        mBasicBinder.etOutletURL.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_OUTLET_URL, AppConstants.KEY_DEFAULT_OUTLET_URL));
        mBasicBinder.etWebSockURL.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_WEB_SOCKET_URL, AppConstants.KEY_DEFAULT_WEB_SOCKET_URL));
        mBasicBinder.etOutLetID.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_OUTLET_ID, AppConstants.KEY_DEFAULT_OUTLET_ID));
        mBasicBinder.etCounterCode.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTER_CODE, AppConstants.KEY_DEFAULT_COUNTER_CODE));
        mBasicBinder.chkAcceptCard.setChecked(sessionManager.getDataByKey(AppConstants.PREF_KEY_ACCEPT_CREDIT_CARDS, true));
        mBasicBinder.chkAcceptCash.setChecked(sessionManager.getDataByKey(AppConstants.PREF_KEY_ACCEPT_CASH, true));
        mBasicBinder.chkShowSnacks.setChecked(sessionManager.getDataByKey(AppConstants.PREF_KEY_SHOW_SNACKS, true));
        mBasicBinder.chkAcceptOthers.setChecked(sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_OTHERS_MANDATORY, true));
        mBasicBinder.chkMobileNumber.setChecked(sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_MOBILE_MANDATORY, true));
        mBasicBinder.chkShowItemImages.setChecked(sessionManager.getDataByKey(AppConstants.PREF_KEY_SHOW_ITEM_IMAGES, false));
        mBasicBinder.spnCountry.setEnabled(false);
        if (!sessionManager.getDataByKey(AppConstants.PREF_KEY_DOUBLE_TAP_TO_SHOW_POPUP, true)) {
            mBasicBinder.chkAnimateItems.setChecked(false);
            mBasicBinder.chkAnimateItems.setEnabled(false);
            mBasicBinder.tvAnimateItems.setTextColor(ContextCompat.getColor(getContext(), R.color.color_grey_dark));
        }
        mBasicBinder.chkAnimateItems.setChecked(sessionManager.getDataByKey(AppConstants.PREF_KEY_ANIMATED_ITEMS, true));
        mBasicBinder.chkDoubleTap.setChecked(sessionManager.getDataByKey(AppConstants.PREF_KEY_DOUBLE_TAP_TO_SHOW_POPUP, true));

        mBasicBinder.etHqURL.setSelection(mBasicBinder.etHqURL.getText().toString().length());
        mBasicBinder.etOutletURL.setSelection(mBasicBinder.etOutletURL.getText().toString().length());
        mBasicBinder.etWebSockURL.setSelection(mBasicBinder.etWebSockURL.getText().toString().length());
        mBasicBinder.etOutLetID.setSelection(mBasicBinder.etOutLetID.getText().toString().length());
        mBasicBinder.etCounterCode.setSelection(mBasicBinder.etCounterCode.getText().toString().length());

        // if (Util.getIndexOfItemFromArray(arrCountry, sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE,"India")) != -1) {
        mBasicBinder.spnCountry.setSelection(Util.getIndexOfItemFromArray(arrCountry, sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE, "India")));
        //  }

        mBasicBinder.chkDoubleTap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBasicBinder.chkAnimateItems.setEnabled(true);
                    mBasicBinder.tvAnimateItems.setTextColor(ContextCompat.getColor(getContext(), R.color.color_text_default));
                } else {
                    mBasicBinder.chkAnimateItems.setChecked(false);
                    mBasicBinder.chkAnimateItems.setEnabled(false);
                    mBasicBinder.tvAnimateItems.setTextColor(ContextCompat.getColor(getContext(), R.color.color_grey_dark));
                }
            }
        });


        mBasicBinder.spnCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mAdvancedBinder != null) {
                    mAdvancedBinder.spnCountry.setSelection(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //setting onclicks
        mBasicBinder.btnSave.setOnClickListener(this);
        mBasicBinder.btnCancel.setOnClickListener(this);
        mBasicBinder.btnClearAllLocks.setOnClickListener(this);

    }

    /**
     * method to save basic setting's data
     */
    public void saveData() {
        if (mBasicBinder.chkAcceptCard.isChecked())
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_ACCEPT_CREDIT_CARDS, true);
        else
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_ACCEPT_CREDIT_CARDS, false);

        if (mBasicBinder.chkAcceptCash.isChecked())
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_ACCEPT_CASH, true);
        else
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_ACCEPT_CASH, false);

        if (mBasicBinder.chkShowSnacks.isChecked())
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_SHOW_SNACKS, true);
        else
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_SHOW_SNACKS, false);

        if (mBasicBinder.chkAcceptOthers.isChecked())
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_IS_OTHERS_MANDATORY, true);
        else
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_IS_OTHERS_MANDATORY, false);

        if (mBasicBinder.chkMobileNumber.isChecked())
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_IS_MOBILE_MANDATORY, true);
        else
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_IS_MOBILE_MANDATORY, false);

        if (mBasicBinder.chkShowItemImages.isChecked())
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_SHOW_ITEM_IMAGES, true);
        else
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_SHOW_ITEM_IMAGES, false);

        if (mBasicBinder.chkAnimateItems.isChecked())
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_ANIMATED_ITEMS, true);
        else
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_ANIMATED_ITEMS, false);

        if (mBasicBinder.chkDoubleTap.isChecked())
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_DOUBLE_TAP_TO_SHOW_POPUP, true);
        else
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_DOUBLE_TAP_TO_SHOW_POPUP, false);

        // sessionManager.storeDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE, mBasicBinder.spnCountry.getSelectedItem().toString());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_HQ_URL, mBasicBinder.etHqURL.getText().toString().trim());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_OUTLET_URL, mBasicBinder.etOutletURL.getText().toString().trim());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_WEB_SOCKET_URL, mBasicBinder.etWebSockURL.getText().toString().trim());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_SETTINGS_PASSWORD, mBasicBinder.etSettingsPassword.getText().toString().trim());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_OUTLET_ID, mBasicBinder.etOutLetID.getText().toString().trim());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_COUNTER_CODE, mBasicBinder.etCounterCode.getText().toString().trim());
        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE).equalsIgnoreCase("india")) {
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_CURRENCY, AppConstants.RUPPEE);
        } else {
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_CURRENCY, AppConstants.DOLLOR);
        }
    }

    /**
     * method to set advanced setting UI
     */
    private void setUpAdvancedUI() {
        final int[] check = {0};
        //setting values
        mAdvancedBinder.etInspirenetzDigestAuth.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_INSPIRENETZ_DIGEST_AUTH, AppConstants.KEY_DEFAULT_INSPIRENETZ_DIGEST_AUTH));
        mAdvancedBinder.etInspireNetzHttpURL.setText(sessionManager.getDataByKey(AppConstants.KEY_DEFAULT_INSPIRENETZ_HTTP_URL, AppConstants.KEY_DEFAULT_INSPIRENETZ_HTTP_URL));
        mAdvancedBinder.etInspirenetzUsername.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_INSPIRENETZ_USERNAME, AppConstants.KEY_DEFAULT_INSPIRENETZ_USERNAME));
        mAdvancedBinder.etInspireNetzPassword.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_INSPIRENETZ_PASSWORD, AppConstants.KEY_DEFAULT_INSPIRENETZ_PASSWORD));
        mAdvancedBinder.etMSwipeUsername.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_MSWIPE_USERNAME, AppConstants.KEY_DEFAULT_MSWIPE_USERNAME));
        mAdvancedBinder.etMSwipePassword.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_MSWIPE_PASSWORD, AppConstants.KEY_DEFAULT_MSWIPE_PASSWORD));

        mAdvancedBinder.etInspirenetzDigestAuth.setSelection(mAdvancedBinder.etInspirenetzDigestAuth.getText().toString().length());
        mAdvancedBinder.etInspireNetzHttpURL.setSelection(mAdvancedBinder.etInspireNetzHttpURL.getText().toString().length());
        mAdvancedBinder.etInspirenetzUsername.setSelection(mAdvancedBinder.etInspirenetzUsername.getText().toString().length());
        mAdvancedBinder.etInspireNetzPassword.setSelection(mAdvancedBinder.etInspireNetzPassword.getText().toString().length());

        mAdvancedBinder.etTerminalID.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_TERMINAL_ID, ""));
        mAdvancedBinder.etMerchantID.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_MERCHANT_ID, ""));
        mAdvancedBinder.etBluetoothName.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_BLUETOOTH_NAME, ""));
        mAdvancedBinder.etBluetoothAddress.setText(sessionManager.getDataByKey(AppConstants.PREF_KEY_BLUETOOTH_ADDRESS));

        ArrayAdapter<String> arrPayment = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, arrPaymentGateway);
        arrPayment.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mAdvancedBinder.spnPaymentGatewayType.setAdapter(arrPayment);
        ArrayAdapter<String> adapterCountry = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, arrCountry);
        adapterCountry.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mAdvancedBinder.spnCountry.setAdapter(adapterCountry);

        // if (Util.getIndexOfItemFromArray(arrCountry, sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE)) != -1) {
        mAdvancedBinder.spnCountry.setSelection(Util.getIndexOfItemFromArray(arrCountry, sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE, "India")));
        // }
        mAdvancedBinder.spnCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++check[0] > 1) {
                    switch (i) {
                        case 0:
                            ArrayAdapter<String> arrPayment = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, arrPaymentGateway);
                            arrPayment.setDropDownViewResource(R.layout.spinner_dropdown_item);
                            mAdvancedBinder.spnPaymentGatewayType.setAdapter(arrPayment);
                            break;
                        case 1:
                            ArrayAdapter<String> arrPaymentIndia = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, arrPaymentGatewayIndia);
                            arrPaymentIndia.setDropDownViewResource(R.layout.spinner_dropdown_item);
                            mAdvancedBinder.spnPaymentGatewayType.setAdapter(arrPaymentIndia);
                            break;
                        case 2:
                            ArrayAdapter<String> arrPaymentSingapore = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, arrPaymentGatewaySingapore);
                            arrPaymentSingapore.setDropDownViewResource(R.layout.spinner_dropdown_item);
                            mAdvancedBinder.spnPaymentGatewayType.setAdapter(arrPaymentSingapore);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mAdvancedBinder.spnPaymentGatewayType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    mAdvancedBinder.llMSwipeViews.setVisibility(View.GONE);
                    mAdvancedBinder.llOngoViews.setVisibility(View.GONE);
                    mAdvancedBinder.llSingaporePaymentViews.setVisibility(View.GONE);
                    // sessionManager.storeDataByKey(AppConstants.PREF_KEY_PAYMENT_GATEWAY_TYPE, "");
                } else {
                    switch (i) {
                        case 1:
                            if (mAdvancedBinder.spnCountry.getSelectedItemPosition() == 1) {
                                //sessionManager.storeDataByKey(AppConstants.PREF_KEY_PAYMENT_GATEWAY_TYPE, arrPaymentGatewayIndia[i]);
                                mAdvancedBinder.llMSwipeViews.setVisibility(View.VISIBLE);
                                mAdvancedBinder.llOngoViews.setVisibility(View.GONE);
                                mAdvancedBinder.llSingaporePaymentViews.setVisibility(View.GONE);
                            } else {
                                // sessionManager.storeDataByKey(AppConstants.PREF_KEY_PAYMENT_GATEWAY_TYPE, arrPaymentGatewaySingapore[i]);
                                mAdvancedBinder.llMSwipeViews.setVisibility(View.GONE);
                                mAdvancedBinder.llOngoViews.setVisibility(View.GONE);
                                mAdvancedBinder.llSingaporePaymentViews.setVisibility(View.VISIBLE);

                            }
                            break;
                        case 2:
                            mAdvancedBinder.llMSwipeViews.setVisibility(View.GONE);
                            mAdvancedBinder.llOngoViews.setVisibility(View.VISIBLE);
                            mAdvancedBinder.llSingaporePaymentViews.setVisibility(View.GONE);
                            break;
                        case 3:

                            break;
                    }
                }
                togglePaymentGatewayButtons(mAdvancedBinder.spnPaymentGatewayType.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mAdvancedBinder.llMSwipeViews.setVisibility(View.GONE);
                mAdvancedBinder.llOngoViews.setVisibility(View.GONE);
                mAdvancedBinder.llSingaporePaymentViews.setVisibility(View.GONE);
            }
        });


        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE, "India").equalsIgnoreCase("india")) {
            ArrayAdapter<String> arrPaymentIndia = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, arrPaymentGatewayIndia);
            arrPaymentIndia.setDropDownViewResource(R.layout.spinner_dropdown_item);
            mAdvancedBinder.spnPaymentGatewayType.setAdapter(arrPaymentIndia);
            if (Util.getIndexOfItemFromArray(arrPaymentGatewayIndia, sessionManager.getDataByKey(AppConstants.PREF_KEY_PAYMENT_GATEWAY_TYPE)) != -1) {
                mAdvancedBinder.spnPaymentGatewayType.setSelection(Util.getIndexOfItemFromArray(arrPaymentGatewayIndia, sessionManager.getDataByKey(AppConstants.PREF_KEY_PAYMENT_GATEWAY_TYPE)));
                togglePaymentGatewayButtons(sessionManager.getDataByKey(AppConstants.PREF_KEY_PAYMENT_GATEWAY_TYPE));
            }
        } else if (sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE, "India").equalsIgnoreCase("singapore")) {
            ArrayAdapter<String> arrPaymentSingapore = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, arrPaymentGatewaySingapore);
            arrPaymentSingapore.setDropDownViewResource(R.layout.spinner_dropdown_item);
            mAdvancedBinder.spnPaymentGatewayType.setAdapter(arrPaymentSingapore);
            if (Util.getIndexOfItemFromArray(arrPaymentGatewaySingapore, sessionManager.getDataByKey(AppConstants.PREF_KEY_PAYMENT_GATEWAY_TYPE)) != -1) {
                mAdvancedBinder.spnPaymentGatewayType.setSelection(Util.getIndexOfItemFromArray(arrPaymentGatewaySingapore, sessionManager.getDataByKey(AppConstants.PREF_KEY_PAYMENT_GATEWAY_TYPE)));
                togglePaymentGatewayButtons(sessionManager.getDataByKey(AppConstants.PREF_KEY_PAYMENT_GATEWAY_TYPE));
            }
        }


        mAdvancedBinder.btnAuthenticateDevice.setOnClickListener(this);
        mAdvancedBinder.btnEdit.setOnClickListener(this);
        mAdvancedBinder.btnCancel.setOnClickListener(this);
        mAdvancedBinder.btnConnectToDevice.setOnClickListener(this);
        mAdvancedBinder.btnBankSummary.setOnClickListener(this);
    }

    public void togglePaymentGatewayButtons(String paymentGateway) {
        if (paymentGateway.equalsIgnoreCase("-Select-") || paymentGateway.isEmpty()) {
            mAdvancedBinder.btnAuthenticateDevice.setVisibility(View.GONE);
            mAdvancedBinder.btnConnectToDevice.setVisibility(View.GONE);
            mAdvancedBinder.btnBankSummary.setVisibility(View.GONE);
        } else if (paymentGateway.equalsIgnoreCase("mswipeinterface")) {
            mAdvancedBinder.btnAuthenticateDevice.setVisibility(View.VISIBLE);
            mAdvancedBinder.btnConnectToDevice.setVisibility(View.VISIBLE);
            mAdvancedBinder.btnBankSummary.setVisibility(View.VISIBLE);
        } else if (paymentGateway.equalsIgnoreCase("ongo")) {
            mAdvancedBinder.btnAuthenticateDevice.setVisibility(View.VISIBLE);
            mAdvancedBinder.btnConnectToDevice.setVisibility(View.GONE);
            mAdvancedBinder.btnBankSummary.setVisibility(View.GONE);
        } else if (paymentGateway.equalsIgnoreCase("singaporepayment")) {
            mAdvancedBinder.btnAuthenticateDevice.setVisibility(View.VISIBLE);
            mAdvancedBinder.btnConnectToDevice.setVisibility(View.GONE);
            mAdvancedBinder.btnBankSummary.setVisibility(View.GONE);
        }
    }

    /**
     * method to edit advance setting data
     */
    public void editData() {
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE, mAdvancedBinder.spnCountry.getSelectedItem().toString());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_PAYMENT_GATEWAY_TYPE, mAdvancedBinder.spnPaymentGatewayType.getSelectedItem().toString());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_INSPIRENETZ_DIGEST_AUTH, mAdvancedBinder.etInspirenetzDigestAuth.getText().toString().trim());
        sessionManager.storeDataByKey(AppConstants.KEY_DEFAULT_INSPIRENETZ_HTTP_URL, mAdvancedBinder.etInspireNetzHttpURL.getText().toString().trim());
        sessionManager.storeDataByKey(AppConstants.KEY_DEFAULT_INSPIRENETZ_USERNAME, mAdvancedBinder.etInspirenetzUsername.getText().toString().trim());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_INSPIRENETZ_PASSWORD, mAdvancedBinder.etInspireNetzPassword.getText().toString().trim());
        if (sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE).equalsIgnoreCase("india")) {
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_CURRENCY, AppConstants.RUPPEE);
        } else {
            sessionManager.storeDataByKey(AppConstants.PREF_KEY_CURRENCY, AppConstants.DOLLOR);
        }
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_MERCHANT_ID, mAdvancedBinder.etMerchantID.getText().toString().trim());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_TERMINAL_ID, mAdvancedBinder.etTerminalID.getText().toString().trim());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_BLUETOOTH_NAME, mAdvancedBinder.etBluetoothName.getText().toString().trim());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_BLUETOOTH_ADDRESS, mAdvancedBinder.etBluetoothAddress.getText().toString().trim());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_MSWIPE_USERNAME, mAdvancedBinder.etMSwipeUsername.getText().toString().trim());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_MSWIPE_PASSWORD, mAdvancedBinder.etMSwipePassword.getText().toString().trim());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_IP_ADDRESS, mAdvancedBinder.etIPAddress.getText().toString().trim());
        sessionManager.storeDataByKey(AppConstants.PREF_KEY_PORT_NUMBER, mAdvancedBinder.etPortNumber.getText().toString().trim());
    }

    /**
     * method to set show logs UI
     */
    private void setUpShowLogsUI() {
        mShowLogBinder.btnEmailLogs.setOnClickListener(this);
        mShowLogBinder.btnCancel.setOnClickListener(this);
        final String log_content = logger.ReadFromFile(getContext());
        mShowLogBinder.etLogs.setText(log_content);

    }

    // interface variable to handle click events
    private OnClicks onClicksListener;

    /**
     * method to set onclicks listener
     */
    public void setOnClicksListener(OnClicks onClicksListener) {
        this.onClicksListener = onClicksListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                saveData();
                if (onClicksListener != null) {
                    onClicksListener.OnSaveButtonClicked();
                }
                break;
            case R.id.btnCancel:
                if (onClicksListener != null) {
                    onClicksListener.OnCancelButtonClicked();
                }
                break;
            case R.id.btnClearAllLocks:
                if (onClicksListener != null) {
                    onClicksListener.OnClearAllLocksButtonClicked();
                }
                break;
            case R.id.btnEmailLogs:
                if (onClicksListener != null) {
                    onClicksListener.OnEmailLogsButtonClicked();
                }
                break;
            case R.id.btnAuthenticateDevice:
                if (onClicksListener != null) {
                    String paymentGateway = mAdvancedBinder.spnPaymentGatewayType.getSelectedItem().toString();
                    String mSwipeUsername = mAdvancedBinder.etMSwipeUsername.getText().toString().trim();
                    String mSwipePassword = mAdvancedBinder.etMSwipePassword.getText().toString().trim();
                    String merchantId = mAdvancedBinder.etMerchantID.getText().toString().trim();
                    String terminalId = mAdvancedBinder.etTerminalID.getText().toString().trim();
                    String bluetoothName = mAdvancedBinder.etBluetoothName.getText().toString().trim();
                    String bluetoothAddress = mAdvancedBinder.etBluetoothAddress.getText().toString().trim();
                    String ipAddress = mAdvancedBinder.etIPAddress.getText().toString().trim();
                    String portNumber = mAdvancedBinder.etPortNumber.getText().toString().trim();
                    if (paymentGateway.equalsIgnoreCase("mswipeinterface")) {
                        onClicksListener.OnAuthenticateButtonClicked(paymentGateway, mSwipeUsername, mSwipePassword);
                    } else if (paymentGateway.equalsIgnoreCase("ongo")) {
                        onClicksListener.OnOnGoAuthenticationButtonClicked(paymentGateway, merchantId, terminalId, bluetoothName, bluetoothAddress);
                    } else if (paymentGateway.equalsIgnoreCase("singaporepayment")) {
                        onClicksListener.OnSingaporeAuthenticationButtonClicked(paymentGateway, ipAddress, portNumber);
                    }
                }
                break;
            case R.id.btnEdit:
                String selectedPaymentType = mAdvancedBinder.spnPaymentGatewayType.getSelectedItem().toString();
                boolean auth = true;
                if (selectedPaymentType.equalsIgnoreCase("Ongo") && !mainActivity.isOngoAuthPerformed()) {
                    auth = false;
                } else if (selectedPaymentType.equalsIgnoreCase("SingaporePayment") && !mainActivity.isSingaporeAuthPerformed()) {
                    auth = false;
                } else if (selectedPaymentType.equalsIgnoreCase("MSwipeInterface") && !mainActivity.isMswipeAuthPerformed()) {
                    auth = false;
                }
                if (!auth) {
                    mainActivity.showInformationDialog("Please Authenticate device to save data.");
                } else {
                    editData();
                    if (onClicksListener != null) {
                        onClicksListener.OnEditButtonClicked();
                    }
                }
                break;
            case R.id.btnConnectToDevice:
                editData();
                if (onClicksListener != null) {
                    onClicksListener.OnConnectDeviceButtonClicked();
                }
                break;
            case R.id.btnBankSummary:
                Logger.d("Bank summary clicked");
                mAdvancedBinder.btnBankSummary.setTextColor(ContextCompat.getColor(getContext(), R.color.color_grey_dark));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mAdvancedBinder.btnBankSummary.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_button_border_disabled));
                }
                mAdvancedBinder.btnBankSummary.setOnClickListener(null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdvancedBinder.btnBankSummary.setTextColor(ContextCompat.getColor(getContext(), R.color.color_buttons_grey));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mAdvancedBinder.btnBankSummary.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_button_border));
                        }
                        mAdvancedBinder.btnBankSummary.setOnClickListener(SettingFragment.this);
                    }
                }, 4000);
                editData();
                if (onClicksListener != null) {
                    onClicksListener.OnBankSummaryButtonClicked();
                }
                break;
        }
    }

    /**
     * interface to handle on clicks
     */
    public interface OnClicks {
        void OnSaveButtonClicked();

        void OnCancelButtonClicked();

        void OnClearAllLocksButtonClicked();

        void OnEmailLogsButtonClicked();

        void OnEditButtonClicked();

        void OnAuthenticateButtonClicked(String selectedPaymentMode, String mSwipeUsername, String mSwipePassword);

        void OnOnGoAuthenticationButtonClicked(String selectedPaymentMode, String merchantId, String terminalId, String bluetoothName, String bluetoothAddress);

        void OnSingaporeAuthenticationButtonClicked(String selectedPaymentMode, String ipAddress, String portNumber);

        void OnConnectDeviceButtonClicked();

        void OnBankSummaryButtonClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }
}
