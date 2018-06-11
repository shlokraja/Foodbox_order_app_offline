package com.frshlypos.utils;

import com.frshlypos.FrshlyApp;
import com.frshlypos.model.FormattedServerItemModel;
import com.frshlypos.model.hqurl.CokeDetails;
import com.frshlypos.model.hqurl.RestaurantDetails;
import com.frshlypos.model.hqurl.RestaurantDetailsCoke;
import com.frshlypos.model.hqurl.ServerItemModelAPI;
import com.frshlypos.model.stockdata.ItemDetail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Akshay.Panchal on 17-Jul-17.
 */

public class Util {
    public static int getIndexOfItemFromArray(String[] items, String value) {
        int index = -1;
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(value)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * This method formats data received from server
     *
     * @param dataSet : Row Data returned from HQ_URL
     * @return
     */
    public static LinkedHashMap<Integer, FormattedServerItemModel> mapServerDataById(ArrayList<ServerItemModelAPI> dataSet) {
        JSONObject formattedObject = new JSONObject();
        LinkedHashMap<Integer, FormattedServerItemModel> mapPriceData = new LinkedHashMap<>();
        FormattedServerItemModel model;
        ServerItemModelAPI itemModel;
        for (int i = 0; i < dataSet.size(); i++) {
            itemModel = dataSet.get(i);
            model = new FormattedServerItemModel();
            model.setMrp(itemModel.getMrp());
            model.setCgstPercent(itemModel.getCgstPercent());
            model.setSgstPercent(itemModel.getSgstPercent());
            model.setMasterId(itemModel.getMasterId());
            model.setName(itemModel.getName());
            model.setItemTag(itemModel.getItemTag());
            model.setVeg(itemModel.getVeg());
            model.setVending(itemModel.getVending());
            model.setSubitem_id(itemModel.getSubitem_id());
            model.setVeg(itemModel.getVeg());
            model.setServiceTaxPercent(itemModel.getServiceTaxPercent());
            model.setAbatementPercent(itemModel.getAbatementPercent());
            model.setVatPercent(itemModel.getVatPercent());
            model.setLocation(itemModel.getLocation());
            model.setSideOrder(itemModel.getSideOrder());
            RestaurantDetails restaurantDetails = new RestaurantDetails();
            restaurantDetails.setId(itemModel.getRId());
            restaurantDetails.setName(itemModel.getRName());
            restaurantDetails.setAddress(itemModel.getRAddress());
            restaurantDetails.setStNo(itemModel.getRStNo());
            restaurantDetails.setPanNo(itemModel.getRPanNo());
            restaurantDetails.setCgstPercent(itemModel.getRCgstPercent());
            restaurantDetails.setSgstPercent(itemModel.getRSgstPercent());
            restaurantDetails.setEntity(itemModel.getREntity());
            restaurantDetails.setTinNo(itemModel.getRTinNo());
            model.setRestaurantDetails(restaurantDetails);
            CokeDetails cokeDetails = new CokeDetails();
            cokeDetails.setId(itemModel.getBId());
            cokeDetails.setName(itemModel.getBName());
            cokeDetails.setMrp(itemModel.getBMrp());
            cokeDetails.setSt(itemModel.getBServiceTaxPercent());
            cokeDetails.setVat(itemModel.getBAbatementPercent());
            cokeDetails.setDiscountPercent(itemModel.getDiscountPercent());
            RestaurantDetailsCoke rDetailsCoke = new RestaurantDetailsCoke();
            rDetailsCoke.setId(itemModel.getBRId());
            rDetailsCoke.setName(itemModel.getBRName());
            rDetailsCoke.setAddress(itemModel.getBRAddress());
            rDetailsCoke.setStNo(itemModel.getRStNo());
            rDetailsCoke.setPanNo(itemModel.getRPanNo());
            rDetailsCoke.setTinNo(itemModel.getBRTinNo());
            cokeDetails.setRestaurantDetails(rDetailsCoke);
            model.setHeatingReqd(itemModel.getHeatingRequired());
            model.setHeatingReduction(itemModel.getHeatingReduction());
            model.setCondimentSlot(itemModel.getCondimentSlot());
            model.setStockQuantity(-1);
            try {
                formattedObject.put("" + itemModel.getId(), model);
                mapPriceData.put(itemModel.getId(), model);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //test mode ids populated into mappricedata
        model = new FormattedServerItemModel();

        model.setMrp(1);
        model.setCgstPercent("1");
        model.setSgstPercent("1");
        model.setMasterId(9001);
        model.setItemId(9001);
        model.setName("Test Item");
        model.setItemTag("TST");
        model.setVeg(true);
        model.setVending("xxx");
        model.setSubitem_id("000");
        model.setServiceTaxPercent("1");
        model.setAbatementPercent("1");
        model.setVatPercent("1");
        model.setLocation("dispenser");
        mapPriceData.put(9001,model);
        return mapPriceData;
    }

    public static boolean shouldItemBeVisible(int item_id) {
        return FrshlyApp.getInstance().sessionManager.getDataByKey("" + item_id + AppConstants.PREF_KEY_ITEM_VISIBILITY, true);
    }

    public static boolean isTestModeItem(int itemCode) {
        return  (itemCode >= 9000 && itemCode <= 9004);
    }

    public static int getStockItemCount(List<ItemDetail> listItemDetails) {
        int count = 0;
        for (int i = 0; i < listItemDetails.size(); i++) {
            if (!listItemDetails.get(i).getExpired() && !listItemDetails.get(i).getSpoiled()) {
                count += listItemDetails.get(i).getCount();
            }
        }
        return count;
    }

    public static boolean isItemInDispenser(int itemCode, String location) {

        if (isTestModeItem(itemCode) && FrshlyApp.getInstance().sessionManager.getDataByKey(AppConstants.PREF_KEY_IS_TEST_MODE, true))
            return true;

        if (location.equalsIgnoreCase("dispenser"))
            return true;
        else
            return false;
    }

    public static String getRoundedValue(double d) {
        if (!FrshlyApp.getInstance().sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTRY_TYPE, "India").equalsIgnoreCase("india")) {
            return String.format("%.2f", d);
        } else {
            return String.valueOf((int) Math.round(d));
        }
    }

    public static String generateUUID() {
        String currentDateOutput = "";
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        currentDateOutput = year + (month < 10 ? "0" + month : "" + month) + (day < 10 ? "0" + day : day);
        currentDateOutput += UUID.randomUUID().toString();
        return currentDateOutput.split("-")[0];
    }

    public static String getHQURL() {
        return FrshlyApp.getInstance().sessionManager.getDataByKey(AppConstants.PREF_KEY_HQ_URL, AppConstants.KEY_DEFAULT_HQ_URL);
    }

    public static String getOutletURL() {
        return FrshlyApp.getInstance().sessionManager.getDataByKey(AppConstants.PREF_KEY_OUTLET_URL,AppConstants.KEY_DEFAULT_OUTLET_URL);
    }

    public static String getWebSocketURL() {
        return FrshlyApp.getInstance().sessionManager.getDataByKey(AppConstants.PREF_KEY_WEB_SOCKET_URL,AppConstants.KEY_DEFAULT_WEB_SOCKET_URL);
    }

    public static String getOutletID() {
        return FrshlyApp.getInstance().sessionManager.getDataByKey(AppConstants.PREF_KEY_OUTLET_ID,AppConstants.KEY_DEFAULT_OUTLET_ID);
    }

    public static String getCounterCode() {
        return FrshlyApp.getInstance().sessionManager.getDataByKey(AppConstants.PREF_KEY_COUNTER_CODE,AppConstants.KEY_DEFAULT_COUNTER_CODE);
    }
}
