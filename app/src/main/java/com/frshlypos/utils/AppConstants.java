package com.frshlypos.utils;

/**
 * Created by Akshay.Panchal on 14-Jul-17.
 */

public class AppConstants {

    public static final String PREF_KEY_CURRENCY = "currency";
    public static final String DOLLOR = "$";
    public static final String RUPPEE = "₹";

    /*Settings popup preferences constants*/
    public static final String PREF_KEY_HQ_URL = "hq_url";
    public static final String PREF_KEY_OUTLET_URL = "outlet_url";
    public static final String PREF_KEY_OUTLET_ID = "outlet_id";
    public static final String PREF_KEY_COUNTER_CODE = "counter_code";
    public static final String PREF_KEY_WEB_SOCKET_URL = "web_socket_url";
    public static final String PREF_KEY_ACCEPT_CREDIT_CARDS = "accept_credit_cards";
    public static final String PREF_KEY_ACCEPT_CASH = "accept_cash";
    public static final String PREF_KEY_IS_MOBILE_MANDATORY = "is_mobile_mandatory";
    public static final String PREF_KEY_IS_OTHERS_MANDATORY = "is_others_mandatory";
    public static final String PREF_KEY_COUNTRY_TYPE = "country_type";
    public static final String PREF_KEY_PAYMENT_GATEWAY_TYPE = "payment_gateway_type";
    public static final String PREF_KEY_SHOW_SNACKS = "show_snacks";
    public static final String PREF_KEY_SHOW_LOGS = "show_logs";
    public static final String PREF_KEY_SHOW_ITEM_IMAGES = "show_item_images";
    public static final String PREF_KEY_MSWIPE_USERNAME = "mswipe_username";
    public static final String PREF_KEY_MSWIPE_PASSWORD = "mswipe_password";
    public static final String PREF_KEY_MERCHANT_ID = "merchant_id";
    public static final String PREF_KEY_TERMINAL_ID = "terminal_id";
    public static final String PREF_KEY_BLUETOOTH_NAME = "bluetooth_name";
    public static final String PREF_KEY_BLUETOOTH_ADDRESS = "bluetooth_address";
    public static final String PREF_KEY_IP_ADDRESS = "ip_address";
    public static final String PREF_KEY_PORT_NUMBER = "port_number";
    public static final String PREF_KEY_SESSION_KEY = "session_key";


    public static final String PREF_KEY_INSPIRENETZ_DIGEST_AUTH = "inspirenetz_digest_auth";
    public static final String PREF_KEY_INSPIRENETZ_USERNAME = "inspirenetz_username";
    public static final String PREF_KEY_INSPIRENETZ_PASSWORD = "inspirenetz_password";
    public static final String PREF_KEY_INSPIRENETZ_HTTP_URL = "inspirenetz_http_url";
    public static final String PREF_KEY_SETTINGS_PASSWORD = "settings_password";
    public static final String KEY_DEFAULT_PASSWORD = "123";
    //public static final String KEY_DEFAULT_HQ_URL = "http://1.23.70.170:8009";
    public static final String KEY_DEFAULT_HQ_URL = "http://192.168.0.127:9159";
    //public static final String KEY_DEFAULT_OUTLET_URL = "http://192.168.1.249:8000";
    public static final String KEY_DEFAULT_OUTLET_URL = "http://192.168.0.127:9159";
    //public static final String KEY_DEFAULT_OUTLET_URL = "http://192.168.1.114:6378";
    public static final String KEY_DEFAULT_WEB_SOCKET_URL = KEY_DEFAULT_OUTLET_URL.substring(0, KEY_DEFAULT_OUTLET_URL.length() - 5) + ":8000";
    public static final String KEY_DEFAULT_OUTLET_ID = "14";
    public static final String KEY_DEFAULT_COUNTER_CODE = "1";
    public static final String KEY_DEFAULT_INSPIRENETZ_DIGEST_AUTH = "www.inspirenetz.com";
    public static final String KEY_DEFAULT_INSPIRENETZ_HTTP_URL = "http://www.inspirenetz.com/api/0.8/json";
    public static final String KEY_DEFAULT_INSPIRENETZ_USERNAME = "atc_api_user1";
    public static final String KEY_DEFAULT_INSPIRENETZ_PASSWORD = "@tcap1@741";
    public static final String KEY_DEFAULT_MSWIPE_USERNAME = "9444126325";
    public static final String KEY_DEFAULT_MSWIPE_PASSWORD = "mswipe";


    //Added by Aiman
    public static final String PREF_KEY_ANIMATED_ITEMS = "animated_items";
    public static final String PREF_KEY_DOUBLE_TAP_TO_SHOW_POPUP = "double_tap_to_show_item_popup";

    public static final String PREF_KEY_IS_TEST_MODE = "is_test_mode";
    public static final String PREF_KEY_IS_ORDER_DELAY = "is_order_delay";
    public static final String PREF_KEY_ITEM_VISIBILITY = "_visibility";
}