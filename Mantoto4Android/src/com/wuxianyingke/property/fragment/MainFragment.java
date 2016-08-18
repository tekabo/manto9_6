package com.wuxianyingke.property.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mantoto.property.R;

/**
 * Created by Administrator on 2016/7/31 0031.
 */
public class MainFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  mView = inflater.inflate(R.layout.fragment_main,null);
    }

    @Override
    protected void initViews() {//没用
        super.initViews();
    }

    @Override
    protected void onLoadData() {//没用
        super.onLoadData();
    }
}
