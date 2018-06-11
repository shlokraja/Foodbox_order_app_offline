package com.frshlypos.ui.DialogFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.frshlypos.R;
import com.frshlypos.adapters.SettingPagerAdapter;
import com.frshlypos.databinding.LayoutDialogPasswordBinding;
import com.frshlypos.databinding.LayoutDialogSettingNewBinding;
import com.frshlypos.ui.activities.MainActivity;
import com.frshlypos.ui.fragments.SettingFragment;

/**
 * Created by Aiman Abbasi on 7/29/2017
 */


public class SettingDiaFragment extends DialogFragment implements TabLayout.OnTabSelectedListener {

    LayoutDialogSettingNewBinding mBinding;
    MainActivity mainActivity;

    public static SettingDiaFragment newInstance() {
        return new SettingDiaFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_dialog_setting_new, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        int width = getResources().getDimensionPixelSize(R.dimen._400sdp);
        Window _w = getDialog().getWindow();
        if (_w != null) {
            _w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            _w.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
//            _w.setWindowAnimations(R.style.dialog_window_animations_overshoot);
        }
    }

    /**
     * method to setup UI
     */
    private void setupUI() {
        //Adding the tabs using addTab() method
        mBinding.tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        //Creating our pager adapter
        final SettingPagerAdapter adapter = new SettingPagerAdapter(getChildFragmentManager(), 3);

        //Adding adapter to pager
        mBinding.vpSetting.setAdapter(adapter);

        //Adding onTabSelectedListener to swipe views
        mBinding.tabs.addOnTabSelectedListener(this);
        mBinding.tabs.setupWithViewPager(mBinding.vpSetting);

        adapter.setOnDialogButtonClickListener(new SettingPagerAdapter.OnDialogButtonClicks() {
            @Override
            public void OnSaveButtonClick() {
                final SettingFragment settingFragment = (SettingFragment) adapter.getItem(1);
                String selectedPaymentType = settingFragment.mAdvancedBinder.spnPaymentGatewayType.getSelectedItem().toString();
                boolean auth = true;
                if (selectedPaymentType.equalsIgnoreCase("Ongo") && !mainActivity.isOngoAuthPerformed()) {
                    auth = false;
                } else if (selectedPaymentType.equalsIgnoreCase("SingaporePayment") && !mainActivity.isSingaporeAuthPerformed()) {
                    auth = false;
                } else if (selectedPaymentType.equalsIgnoreCase("MSwipeInterface") && !mainActivity.isMswipeAuthPerformed()) {
                    auth = false;
                }
                if (!auth) {
                    final LayoutDialogPasswordBinding binding;
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    binding = DataBindingUtil.inflate(LayoutInflater.from(mainActivity), R.layout.layout_dialog_password, null, false);
                    builder.setView(binding.getRoot());
                    final AlertDialog dialogAuthenticate = builder.create();
                    binding.btnEnter.setText("Authenticate");
                    binding.tilMobile.setVisibility(View.GONE);
                    binding.tvTitle.setVisibility(View.GONE);
                    binding.tvMessage.setVisibility(View.VISIBLE);

                    binding.btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogAuthenticate.dismiss();
                        }
                    });
                    binding.btnEnter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String paymentGateway = settingFragment.mAdvancedBinder.spnPaymentGatewayType.getSelectedItem().toString();
                            String mSwipeUsername = settingFragment.mAdvancedBinder.etMSwipeUsername.getText().toString().trim();
                            String mSwipePassword = settingFragment.mAdvancedBinder.etMSwipePassword.getText().toString().trim();
                            String merchantId = settingFragment.mAdvancedBinder.etMerchantID.getText().toString().trim();
                            String terminalId = settingFragment.mAdvancedBinder.etTerminalID.getText().toString().trim();
                            String bluetoothName = settingFragment.mAdvancedBinder.etBluetoothName.getText().toString().trim();
                            String bluetoothAddress = settingFragment.mAdvancedBinder.etBluetoothAddress.getText().toString().trim();
                            String ipAddress = settingFragment.mAdvancedBinder.etIPAddress.getText().toString().trim();
                            String portNumber = settingFragment.mAdvancedBinder.etPortNumber.getText().toString().trim();
                            if (paymentGateway.equalsIgnoreCase("mswipeinterface")) {
                                onACKListener.OnAuthenticateButtonClicked(paymentGateway, mSwipeUsername, mSwipePassword);
                            } else if (paymentGateway.equalsIgnoreCase("ongo")) {
                                onACKListener.OnOnGoAuthenticationButtonClicked(paymentGateway, merchantId, terminalId, bluetoothName, bluetoothAddress);
                            } else if (paymentGateway.equalsIgnoreCase("singaporepayment")) {
                                onACKListener.OnSingaporeAuthenticationButtonClicked(paymentGateway, ipAddress, portNumber);
                            }
                            if (onACKListener != null) {
                                onACKListener.onSaveClick();
                            }
                            dialogAuthenticate.dismiss();
                            dismiss();

                        }
                    });
                    dialogAuthenticate.setCancelable(false);
                    dialogAuthenticate.show();

                } else {
                    if (onACKListener != null) {
                        onACKListener.onSaveClick();
                    }
                    dismiss();
                }


            }

            @Override
            public void OnCancelButtonClick() {
                if (onACKListener != null) {
                    onACKListener.onCancelClick();
                }
                dismiss();
            }

            @Override
            public void OnClearAllLocksButtonClick() {
                if (onACKListener != null) {
                    onACKListener.onClearAllClick();
                }
                dismiss();
            }

            @Override
            public void OnEmailLogsButtonClicked() {
                if (onACKListener != null) {
                    onACKListener.OnEmailLogsButtonClicked();
                }
            }

            @Override
            public void OnEditButtonClicked() {
                if (onACKListener != null) {
                    onACKListener.OnEditButtonClicked();
                }
                dismiss();
            }

            @Override
            public void OnAuthenticateButtonClicked(String selectedPaymentMode, String mSwipeUsername, String mSwipePassword) {
                if (onACKListener != null) {
                    onACKListener.OnAuthenticateButtonClicked(selectedPaymentMode, mSwipeUsername, mSwipePassword);
                }
            }

            @Override
            public void OnOnGoAuthenticationButtonClicked(String selectedPaymentMode, String merchantId, String terminalId, String bluetoothName, String bluetoothAddress) {
                if (onACKListener != null) {
                    onACKListener.OnOnGoAuthenticationButtonClicked(selectedPaymentMode, merchantId, terminalId, bluetoothName, bluetoothAddress);
                }
            }

            @Override
            public void OnSingaporeAuthenticationButtonClicked(String selectedPaymentMode, String ipAddress, String portNumber) {
                if (onACKListener != null) {
                    onACKListener.OnSingaporeAuthenticationButtonClicked(selectedPaymentMode, ipAddress, portNumber);
                }
            }

            @Override
            public void OnConnectDeviceButtonClicked() {
                if (onACKListener != null) {
                    onACKListener.OnConnectDeviceButtonClicked();
                }
            }

            @Override
            public void OnBankSummaryButtonClicked() {
                if (onACKListener != null) {
                    onACKListener.OnBankSummaryButtonClicked();
                }
            }
        });
    }

    public void show(FragmentManager _manager) {
        show(_manager, SettingDiaFragment.class.getSimpleName());
    }

    @Override
    public void onDestroyView() {
        mBinding.unbind();
        super.onDestroyView();
    }

    private OnACKListener onACKListener;

    public void setOnACKListener(OnACKListener onACKListener) {
        this.onACKListener = onACKListener;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public interface OnACKListener {
        void onSaveClick();

        void onCancelClick();

        void onClearAllClick();

        void OnEmailLogsButtonClicked();

        void OnEditButtonClicked();

        void OnAuthenticateButtonClicked(String selectedPaymentMode, String mSwipeUsername, String mSwipePassword);

        void OnOnGoAuthenticationButtonClicked(String selectedPaymentMode, String merchantId, String terminalId, String bluetoothName, String bluetoothAddress);

        void OnSingaporeAuthenticationButtonClicked(String selectedPaymentMode, String ipAddress, String portNumber);

        void OnConnectDeviceButtonClicked();

        void OnBankSummaryButtonClicked();
    }

    public void setStatusText(String statusText) {
        /*mBinding.tvStatusText.setVisibility(View.VISIBLE);
        mBinding.tvStatusText.setText(statusText);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.tvStatusText.setVisibility(View.GONE);
            }
        }, 5000);*/
        Snackbar.make(mBinding.tvStatusText, statusText, 4000).show();
    }

    public void setStatusText(Spanned statusText) {
        /*mBinding.tvStatusText.setVisibility(View.VISIBLE);
        mBinding.tvStatusText.setText(statusText);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.tvStatusText.setVisibility(View.GONE);
            }
        }, 5000);*/
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog dialog = builder.create();
        dialog.setMessage(statusText);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }


    public void hideStatusText() {
        mBinding.tvStatusText.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }
}
