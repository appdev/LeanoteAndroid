package com.apkdv.leanote.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apkdv.leanote.R;
import com.apkdv.leanote.adapter.MyDecoration;
import com.apkdv.leanote.adapter.TagAdapter;
import com.apkdv.leanote.model.Tag;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by LengYue on 2017/1/17.
 */

public class TagListFragment extends Fragment implements TagAdapter.TagAdapterListener {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.ptr_layput)
    PtrFrameLayout mPtrLayput;

    public static TagListFragment newInstance() {
        TagListFragment fragment = new TagListFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_pull_to_refresh, container, false);
        ButterKnife.bind(this, view);
        initTagPanel();
        return view;
    }

    private void initTagPanel() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new MyDecoration(getActivity()));
        TagAdapter tagAdapter = new TagAdapter();
        tagAdapter.setListener(this);
        mRecyclerView.setAdapter(tagAdapter);
        tagAdapter.toggle();
//        mTagTriangle.setTag(false);
    }


    @Override
    public void onClickedTag(Tag tag) {

    }
}
