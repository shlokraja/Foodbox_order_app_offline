package com.frshlypos.paymentgateway;

import com.frshlypos.ui.activities.MainActivity;

/**
 * Created by rajamanickam.r on 6/29/2016.
 */
public class PaymentGatewayFactory {
    public static IPaymentGateway paymentGateway(PaymentRequest paymentRequest, PaymentGateway paymentGateway, MainActivity myActivity) {
        switch (paymentGateway) {
            case MSwipeInterface:
                return new MSwipeGateway(myActivity);
            case Ongo:
                return new OngoGateway(paymentRequest,myActivity);
            case SingaporePayment:
                return new SingaporeGateway(paymentRequest,myActivity);
            default:
                return null;
        }
    }
}

