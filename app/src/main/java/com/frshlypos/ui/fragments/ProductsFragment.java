package com.frshlypos.ui.fragments;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.frshlypos.databinding.LayoutProductsBinding;
import com.frshlypos.R;
import com.frshlypos.adapters.OutSideItemAdapter;
import com.frshlypos.adapters.StockItemAdapter;
import com.frshlypos.listeners.OnOrderUpdated;
import com.frshlypos.model.FormattedServerItemModel;
import com.frshlypos.model.StockItemModel;
import com.frshlypos.ui.activities.MainActivity;
import com.frshlypos.utils.AppConstants;

import java.util.ArrayList;


public class ProductsFragment extends BaseFragment implements OnOrderUpdated {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private int type;
    private ArrayList<StockItemModel> listStockItems;
    ArrayList<FormattedServerItemModel> listOutSideItems;
    LayoutProductsBinding binding;
    public OutSideItemAdapter outSideItemAdapter;
    public StockItemAdapter stockItemAdapter;
    MainActivity mainActivity;


    public ProductsFragment() {
    }


    public static ProductsFragment newInstance(int param1, ArrayList<StockItemModel> param2, ArrayList<FormattedServerItemModel> param3) {
        ProductsFragment fragment = new ProductsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putSerializable(ARG_PARAM2, param2);
        args.putSerializable(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_PARAM1);
            if (type == 0)
                listStockItems = (ArrayList<StockItemModel>) getArguments().getSerializable(ARG_PARAM2);
            else
                listOutSideItems = (ArrayList<FormattedServerItemModel>) getArguments().getSerializable(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_products, container, false);

        if (type == 0) {
            binding.rvItems.setLayoutManager(new GridLayoutManager(getActivity(), sessionManager.getDataByKey(AppConstants.PREF_KEY_SHOW_ITEM_IMAGES, true) ? 2 : 4));
            stockItemAdapter = new StockItemAdapter(getContext(), listStockItems, this);
            stockItemAdapter.setActionListener(new StockItemAdapter.ProductItemActionListener() {
                @Override
                public void onItemTap(ImageView imageView) {
                    mainActivity.onItemTap(imageView);
                }
            });
            binding.rvItems.setAdapter(stockItemAdapter);
            if (listStockItems == null || listStockItems.size() < 1) {
                binding.tvNoItems.setVisibility(View.VISIBLE);
            } else {
                binding.tvNoItems.setVisibility(View.GONE);
            }
        } else {
            binding.rvItems.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            outSideItemAdapter = new OutSideItemAdapter(getActivity(), listOutSideItems, this);
            outSideItemAdapter.setActionListener(new OutSideItemAdapter.ProductItemActionListener() {
                @Override
                public void onItemTap(ImageView imageView) {
                    mainActivity.onItemTap(imageView);
                }
            });
            binding.rvItems.setAdapter(outSideItemAdapter);
        }

        return binding.getRoot();
    }

    @Override
    public void orderUpdated() {
        mainActivity.orderUpdated();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

}
