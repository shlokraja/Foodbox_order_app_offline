package com.frshlypos.retrofitservice;

import com.frshlypos.FrshlyApp;
import com.frshlypos.model.requestapimodel.TryLockModel;
import com.frshlypos.utils.AppConstants;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface APIService {

    //String HQ_URL = "http://1.23.70.170:8009";
    //String OUTLET_URL = "http://192.168.1.249:8000";
    String HQ_URL = FrshlyApp.getInstance().sessionManager.getDataByKey(AppConstants.PREF_KEY_HQ_URL,AppConstants.KEY_DEFAULT_HQ_URL);
    String OUTLET_URL = FrshlyApp.getInstance().sessionManager.getDataByKey(AppConstants.PREF_KEY_OUTLET_URL,AppConstants.KEY_DEFAULT_OUTLET_URL);
    String WEB_SOCKET_URL = FrshlyApp.getInstance().sessionManager.getDataByKey(AppConstants.PREF_KEY_WEB_SOCKET_URL,AppConstants.KEY_DEFAULT_WEB_SOCKET_URL);
    String OUTLET_ID = FrshlyApp.getInstance().sessionManager.getDataByKey(AppConstants.PREF_KEY_OUTLET_ID,AppConstants.KEY_DEFAULT_OUTLET_ID);
    String COUNTER_CODE = FrshlyApp.getInstance().sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTER_CODE,AppConstants.KEY_DEFAULT_COUNTER_CODE);
    @FormUrlEncoded
    @POST("qls/bqlsquestionlist")
    Observable<ResponseBody> addQLSTracking(@Field("userid") String userid,
                                            @Field("question_id") String question_id,
                                            @Field("score") String score,
                                            @Field("encrypted_data") String encrypted_data);

   @GET("/order_app/getmenuitems")
    Observable<ResponseBody> getItems();

    /*@GET("/food_item/price_info/{id}")
    Observable<ResponseBody> getItems(@Path("id") String id);*/

    @GET("/order_app/test_mode/")
    Observable<ResponseBody> getTestModeStatus();

    @GET("/menu_display/dispenser_status/")
    Observable<ResponseBody> getDispenserStatus();

    @GET("/order_app/stop_orders_state/")
    Observable<ResponseBody> getStopOrdersState();

    @GET("/order_app/run_count/")
    Observable<ResponseBody> getRunCount();

    @GET("/menu_display/stock_initial/")
    Observable<ResponseBody> getInitialStockData();

    @POST("/order_app/try_lock/{item_id}")
    Observable<ResponseBody> tryToLockItem(
            @Path("item_id") String item_id,
            @Body TryLockModel model);

    @POST("/order_app/lock_item/{item_id}")
    Observable<ResponseBody> lockItem(
            @Path("item_id") String item_id,
            @Body TryLockModel model);

    @POST("/order_app/place_order")
    Observable<ResponseBody> pushOrder(@Body JsonObject body);

}
