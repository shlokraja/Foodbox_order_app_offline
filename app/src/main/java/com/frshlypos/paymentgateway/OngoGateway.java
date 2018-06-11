package com.frshlypos.paymentgateway;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.frshlypos.ui.activities.MainActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.List;

/**
 * Created by rajamanickam.r on 6/28/2016.
 */
public class OngoGateway implements IPaymentGateway {
    private static final int REQUEST_ONGO_GATEWAY = 1;
    public static boolean isMopInstalled = false;
    private static Intent intent;
    public Context mContext;
    private MyLogger logger;
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "pos.indiatransact.com:7600/ISO8583Service.asmx";
    private static int timeout;

    static {
        timeout = 30000;
    }


    public String getDataFromUrl(PaymentRequest data) {
        String xml = null;
        try {
            logger.info(MainActivity.TAG,"New Session Key Start",mContext);
            SoapObject request = new SoapObject(NAMESPACE, "getSessionKey");
            request.addProperty("terminal_id", data.Terminal_id);
            request.addProperty("merchant_id", data.Merchant_id);
            request.addProperty("platform_type", "foodboxpos");
            request.addProperty("app_ver", "v2.3");

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            SSLConection.allowAllSSL();
            try {
                new HttpTransportSE(URL).call(NAMESPACE + "getSessionKey", envelope);
            } catch (Exception e30) {
                e30.printStackTrace();
            }
            Object result = envelope.getResponse();
            if (result != null) {
                xml = result.toString();
            }
        } catch (Exception e302) {
            e302.printStackTrace();
        }
        logger.info(MainActivity.TAG,"New Session Key End" + xml,mContext);
        return  xml == null ? "No Response From Server" : xml;
    }

    public OngoGateway(PaymentRequest paymentRequest, MainActivity myActivity) {
        mContext = myActivity;
        logger = MyLogger.getInstance();
        try {

            //logger.info(MyActivity.TAG,sessionkeytest,mContext);
            logger.info(MainActivity.TAG,"OngoGateway Started",mContext);
            final PackageManager packageManager = ((MainActivity) mContext).getPackageManager();
            intent = new Intent("com.agsindia.mpos_integration.ActivitySwipe");
            List resolveInfo =
                    packageManager.queryIntentActivities(intent,
                            PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfo.size() > 0) {
                isMopInstalled = true;
                intent.putExtra("serviceType", "sale");
                intent.putExtra("mer_id", paymentRequest.Merchant_id);
                intent.putExtra("ter_id", paymentRequest.Terminal_id);
                intent.putExtra("bt_address", paymentRequest.Bluetooth_id);
                intent.putExtra("bt_name", paymentRequest.Bluetooth_name);
                logger.info(MainActivity.TAG,"OngoGateway End",mContext);
            } else {
                isMopInstalled = false;
                logger.info(MainActivity.TAG,"Mpos Not Installed",mContext);
                Toast.makeText(((MainActivity) mContext), " mpos apk is Not installed.", Toast.LENGTH_LONG).show();
            }
        } catch (ActivityNotFoundException e) {
            logger.info(MainActivity.TAG,"Mpos Not Installed",mContext);
            Toast.makeText(((MainActivity) mContext), " mpos apk is Not installed.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void Pay(String payment_amount, String sessionkey) {
        logger.info(MainActivity.TAG,"Ongo Payment Start",mContext);
        if (isMopInstalled) {
            intent.putExtra("amount", payment_amount);
            intent.putExtra("sessionkey",sessionkey);
            logger.info(MainActivity.TAG,"Ongo Payment Activity Start",mContext);
            ((MainActivity) mContext).startActivityForResult(intent, REQUEST_ONGO_GATEWAY);
        } else {
            Toast.makeText(((MainActivity) mContext), " mpos apk is Not installed.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public String Getsessionkey(PaymentRequest data){
        String sessionkey = getDataFromUrl(data);
        return sessionkey;
    }

    @Override
    public boolean connectToDevice(boolean duringTransaction) {
        return false;
    }

    @Override
    public void DisconnectDevice() {

    }

    @Override
    public void RevertTransaction() {

    }

    @Override
    public void CancelCheckCard() {

    }

    @Override
    public void ShowCardSuccessScreen(String cardNo, final String cardholderName) {
        try {

            logger.info(MainActivity.TAG,"Ongo Payment Activity End",mContext);
            logger.info(MainActivity.TAG,"Ongo Success screen  Start",mContext);
            final String last4Digits;
            if (cardNo != "" && cardNo != null) {
                int ilen = cardNo.length();
                if (ilen >= 4)
                    last4Digits = cardNo.substring(ilen - 4, ilen);
                else
                    last4Digits = cardNo;
            } else {
                last4Digits = "";
            }
           // ((MainActivity) mContext).showSuccessScreen(last4Digits,cardholderName);
            /*((MainActivity) mContext).myWebView.post(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) mContext).myWebView.evaluateJavascript("showSuccessScreen('" + last4Digits + "','" + cardholderName + "')", null);
                }
            });*/
            logger.info(MainActivity.TAG,"Ongo Success screen  End",mContext);
        }
        catch (Exception e) {
            logger.info(MainActivity.TAG,"Ongo Success screen  Error",mContext);
            Toast.makeText(((MainActivity) mContext), "Show Card Success Error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void ShowCardFailureScreen(String displayMsg) {
        try {
            logger.info(MainActivity.TAG,"Ongo Payment Activity End",mContext);
            logger.info(MainActivity.TAG,"Ongo Failure screen  Start",mContext);
            final String msgToShow = displayMsg;
           // ((MainActivity) mContext).showCardFailureScreen(msgToShow);
            logger.info(MainActivity.TAG,"Ongo Failure screen  End",mContext);
        }
        catch (Exception e) {
            logger.info(MainActivity.TAG,"Ongo Failure screen  Error",mContext);
            Toast.makeText(((MainActivity) mContext), "Show Card Failure Error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void ShowBankSummary() {

    }
}
