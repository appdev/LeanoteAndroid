package com.apkdv.leanote.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.apkdv.leanote.R;
import com.apkdv.leanote.adapter.MyDecoration;
import com.apkdv.leanote.adapter.NotebookAdapter;
import com.apkdv.leanote.model.Notebook;
import com.apkdv.leanote.service.NotebookService;
import com.apkdv.leanote.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrFrameLayout;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by LengYue on 2017/1/17.
 */

public class FileListFragment extends Fragment implements NotebookAdapter.NotebookAdapterListener {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.ptr_layput)
    PtrFrameLayout mPtrLayput;
    private NotebookAdapter mNotebookAdapter;

    public static FileListFragment newInstance() {
        FileListFragment fragment = new FileListFragment();
        return fragment;
    }

    private MyReceiver mReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_pull_to_refresh, container, false);
        ButterKnife.bind(this, view);
        initNotebookPanel();
        mReceiver = new MyReceiver();
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mReceiver, new IntentFilter(NoteFragment.class.getName()));
        return view;
    }

    private void initNotebookPanel() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new MyDecoration(getActivity()));
        mNotebookAdapter = new NotebookAdapter();
        mNotebookAdapter.setListener(this);
        mRecyclerView.setAdapter(mNotebookAdapter);
        mNotebookAdapter.setCanOpenEmpty(false);
        mNotebookAdapter.setHasAddButton(true);
        mNotebookAdapter.refresh();
    }


    @Override
    public void onClickedNotebook(Notebook notebook) {

    }


    @Override
    public void onClickedAddNotebook(final String parentNotebookId) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sigle_edittext, null);
        final EditText mEdit = (EditText) view.findViewById(R.id.edit);
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_notebook)
                .setView(view)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(mEdit.getText().toString())) {
                            ToastUtils.show(getActivity(), "请输入笔记本的标题");
                        } else {
                            dialog.dismiss();
                            addNotebook(mEdit.getText().toString(), parentNotebookId);
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void addNotebook(final String title, final String parentNotebookId) {
        Observable.create(
                new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        if (!subscriber.isUnsubscribed()) {
                            NotebookService.addNotebook(title, parentNotebookId);
                            subscriber.onNext(null);
                            subscriber.onCompleted();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Void isSucceed) {
                        mNotebookAdapter.refresh();
                    }
                });
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mNotebookAdapter.refresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }
}
