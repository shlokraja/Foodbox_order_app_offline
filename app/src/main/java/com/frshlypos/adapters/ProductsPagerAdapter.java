package com.frshlypos.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.frshlypos.listeners.OnOrderUpdated;
import com.frshlypos.model.FormattedServerItemModel;
import com.frshlypos.model.StockItemModel;
import com.frshlypos.ui.fragments.ProductsFragment;
import com.frshlypos.utils.SessionManager;

import java.util.ArrayList;

/**
 * Created by Akshay.Panchal on 31-Jul-17.
 */

public class ProductsPagerAdapter extends FragmentStatePagerAdapter {

    Context context;
    ArrayList<FormattedServerItemModel> listOutSideItems;
    ArrayList<StockItemModel> listStockItems;
    OutSideItemAdapter outSideItemAdapter;
    StockItemAdapter stockItemAdapter;
    SessionManager sessionManager;
    OnOrderUpdated onOrderUpdated;
    String tabTitles[] = new String[]{"MEALS", "SNACKS & DRINKS"};
    ArrayList<Fragment> fragments = new ArrayList<>();
    boolean shouldShowSnacks;


    public ProductsPagerAdapter(FragmentManager fragmentManager, Context context, ArrayList<StockItemModel> listStockItems, ArrayList<FormattedServerItemModel> listOutSideItems, OnOrderUpdated onOrderUpdated, boolean shouldShowSnacks) {
        super(fragmentManager);
        this.context = context;
        this.listOutSideItems = listOutSideItems;
        this.listStockItems = listStockItems;
        sessionManager = new SessionManager(context);
        this.onOrderUpdated = onOrderUpdated;
        this.shouldShowSnacks = shouldShowSnacks;
        fragments.add(ProductsFragment.newInstance(0, listStockItems, null));
        fragments.add(ProductsFragment.newInstance(1, null, listOutSideItems));

    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return shouldShowSnacks ? tabTitles.length : tabTitles.length - 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        try{
            super.finishUpdate(container);
        } catch (NullPointerException nullPointerException){
            System.out.println("Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
        }
    }

}
