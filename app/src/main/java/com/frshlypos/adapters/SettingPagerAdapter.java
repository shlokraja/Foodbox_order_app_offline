package com.frshlypos.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.frshlypos.ui.fragments.SettingFragment;

import java.util.ArrayList;

/**
 * Created by Ayman Abbasi on 29-Jul-17
 */

public class SettingPagerAdapter extends FragmentStatePagerAdapter {
    //integer to count number of tabs
    private int tabCount;
    ArrayList<Fragment> fragments = new ArrayList<>();

    //Constructor to the class
    public SettingPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount = tabCount;
        fragments.add(SettingFragment.newInstance(0));

        fragments.add(SettingFragment.newInstance(1));
        fragments.add(SettingFragment.newInstance(2));

    }

    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                ((SettingFragment) fragments.get(0)).setOnClicksListener(new SettingFragment.OnClicks() {
                    @Override
                    public void OnSaveButtonClicked() {
                        ((SettingFragment) getItem(1)).editData();
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnSaveButtonClick();
                        }
                    }

                    @Override
                    public void OnCancelButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnCancelButtonClick();
                        }
                    }

                    @Override
                    public void OnClearAllLocksButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnClearAllLocksButtonClick();
                        }
                    }

                    @Override
                    public void OnEmailLogsButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnEmailLogsButtonClicked();
                        }
                    }

                    @Override
                    public void OnEditButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnEditButtonClicked();
                        }
                    }

                    @Override
                    public void OnAuthenticateButtonClicked(String selectedPaymentMode, String mSwipeUsername, String mSwipePassword) {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnAuthenticateButtonClicked(selectedPaymentMode, mSwipeUsername, mSwipePassword);
                        }
                    }

                    @Override
                    public void OnOnGoAuthenticationButtonClicked(String selectedPaymentMode, String merchantId, String terminalId, String bluetoothName, String bluetoothAddress) {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnOnGoAuthenticationButtonClicked(selectedPaymentMode, merchantId, terminalId, bluetoothName, bluetoothAddress);
                        }
                    }

                    @Override
                    public void OnSingaporeAuthenticationButtonClicked(String selectedPaymentMode, String ipAddress, String portNumber) {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnSingaporeAuthenticationButtonClicked(selectedPaymentMode, ipAddress, portNumber);
                        }
                    }


                    @Override
                    public void OnConnectDeviceButtonClicked() {

                    }

                    @Override
                    public void OnBankSummaryButtonClicked() {

                    }
                });
                return fragments.get(0);
            case 1:
                ((SettingFragment) fragments.get(1)).setOnClicksListener(new SettingFragment.OnClicks() {
                    @Override
                    public void OnSaveButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnSaveButtonClick();
                        }
                    }

                    @Override
                    public void OnCancelButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnCancelButtonClick();
                        }
                    }

                    @Override
                    public void OnClearAllLocksButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnClearAllLocksButtonClick();
                        }
                    }

                    @Override
                    public void OnEmailLogsButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnEmailLogsButtonClicked();
                        }
                    }

                    @Override
                    public void OnEditButtonClicked() {
                        ((SettingFragment) getItem(0)).saveData();
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnEditButtonClicked();
                        }
                    }

                    @Override
                    public void OnAuthenticateButtonClicked(String selectedPaymentMode, String mSwipeUsername, String mSwipePassword) {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnAuthenticateButtonClicked(selectedPaymentMode, mSwipeUsername, mSwipePassword);
                        }
                    }

                    @Override
                    public void OnOnGoAuthenticationButtonClicked(String selectedPaymentMode, String merchantId, String terminalId, String bluetoothName, String bluetoothAddress) {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnOnGoAuthenticationButtonClicked(selectedPaymentMode, merchantId, terminalId, bluetoothName, bluetoothAddress);
                        }
                    }

                    @Override
                    public void OnSingaporeAuthenticationButtonClicked(String selectedPaymentMode, String ipAddress, String portNumber) {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnSingaporeAuthenticationButtonClicked(selectedPaymentMode, ipAddress, portNumber);
                        }
                    }


                    @Override
                    public void OnConnectDeviceButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnConnectDeviceButtonClicked();
                        }
                    }

                    @Override
                    public void OnBankSummaryButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnBankSummaryButtonClicked();
                        }
                    }
                });
                return fragments.get(1);
            case 2:
                ((SettingFragment) fragments.get(2)).setOnClicksListener(new SettingFragment.OnClicks() {
                    @Override
                    public void OnSaveButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnSaveButtonClick();
                        }
                    }

                    @Override
                    public void OnCancelButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnCancelButtonClick();
                        }
                    }

                    @Override
                    public void OnClearAllLocksButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnClearAllLocksButtonClick();
                        }
                    }

                    @Override
                    public void OnEmailLogsButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnEmailLogsButtonClicked();
                        }
                    }

                    @Override
                    public void OnEditButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnEditButtonClicked();
                        }
                    }

                    @Override
                    public void OnAuthenticateButtonClicked(String selectedPaymentMode, String mSwipeUsername, String mSwipePassword) {

                    }

                    @Override
                    public void OnOnGoAuthenticationButtonClicked(String selectedPaymentMode, String merchantId, String terminalId, String bluetoothName, String bluetoothAddress) {

                    }

                    @Override
                    public void OnSingaporeAuthenticationButtonClicked(String selectedPaymentMode, String ipAddress, String portNumber) {

                    }


                    @Override
                    public void OnConnectDeviceButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnConnectDeviceButtonClicked();
                        }
                    }

                    @Override
                    public void OnBankSummaryButtonClicked() {
                        if (onDialogButtonClicksListener != null) {
                            onDialogButtonClicksListener.OnBankSummaryButtonClicked();
                        }
                    }
                });
                return fragments.get(2);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Basic Settings";
            case 1:
                return "Advanced Settings";
            case 2:
                return "Show Logs";
        }
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    // variable of dialog button clicks
    private OnDialogButtonClicks onDialogButtonClicksListener;

    /**
     * method to set on dialog button click listener
     */
    public void setOnDialogButtonClickListener(OnDialogButtonClicks onDialogButtonClicksListener) {
        this.onDialogButtonClicksListener = onDialogButtonClicksListener;
    }

    /**
     * interface to handle clicks
     */
    public interface OnDialogButtonClicks {
        void OnSaveButtonClick();

        void OnCancelButtonClick();

        void OnClearAllLocksButtonClick();

        void OnEmailLogsButtonClicked();

        void OnEditButtonClicked();

        void OnAuthenticateButtonClicked(String selectedPaymentMode, String mSwipeUsername, String mSwipePassword);

        void OnOnGoAuthenticationButtonClicked(String selectedPaymentMode, String merchantId, String terminalId, String bluetoothName, String bluetoothAddress);

        void OnSingaporeAuthenticationButtonClicked(String selectedPaymentMode, String ipAddress, String portNumber);

        void OnConnectDeviceButtonClicked();

        void OnBankSummaryButtonClicked();
    }
}
