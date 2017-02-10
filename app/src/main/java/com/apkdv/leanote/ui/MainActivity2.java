package com.apkdv.leanote.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tencent.bugly.crashreport.CrashReport;

import com.apkdv.leanote.Constant;
import com.apkdv.leanote.Leamonax;
import com.apkdv.leanote.R;
import com.apkdv.leanote.adapter.NotebookAdapter;
import com.apkdv.leanote.database.AppDataBase;
import com.apkdv.leanote.model.Account;
import com.apkdv.leanote.model.Note;
import com.apkdv.leanote.model.Notebook;
import com.apkdv.leanote.model.User;
import com.apkdv.leanote.service.AccountService;
import com.apkdv.leanote.ui.edit.NoteEditActivity;
import com.apkdv.leanote.ui.fragment.FileListFragment;
import com.apkdv.leanote.ui.fragment.NoteFragment;
import com.apkdv.leanote.ui.fragment.TagListFragment;
import com.apkdv.leanote.utils.DvSharedPreferences;
import com.apkdv.leanote.utils.ToastUtils;
import com.apkdv.leanote.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity2 extends BaseActivity {

    private static final String EXT_SHOULD_RELOAD = "ext_should_reload";
    private static final String TAG_NOTE_FRAGMENT = "tag_note_fragment";

    NoteFragment mNoteFragment;
    NotebookAdapter mNotebookAdapter;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.image_avatar)
    ImageView mImageAvatar;
    @BindView(R.id.text_title)
    TextView mTextTitle;
    @BindView(R.id.image_search)
    ImageView mImageSearch;


    public static Intent getOpenIntent(Context context, boolean shouldReload) {
        Intent intent = new Intent(context, MainActivity2.class);
        intent.putExtra(EXT_SHOULD_RELOAD, shouldReload);
        return intent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_two);
        ButterKnife.bind(this);
        CrashReport.setUserId(AccountService.getCurrent().getUserId());
        InitViewPager();
        refreshInfo();
        fetchInfo();
        initGuide();
    }

    private void initGuide() {
        if (!DvSharedPreferences.getBoolean(Constant.HOME_INDEX, false))
            mImageAvatar.post(new Runnable() {
                @Override
                public void run() {
                    Utils.showGuideView(mImageAvatar, MainActivity2.this, Constant.HOME_INDEX);
                }
            });
    }

    private ArrayList<Fragment> mFragments;

    /*
         * 初始化ViewPager
         */
    public void InitViewPager() {
        mFragments = new ArrayList<>();
        mFragments.add(NoteFragment.newInstance(true));
        mFragments.add(FileListFragment.newInstance());
        mFragments.add(TagListFragment.newInstance());

        //给ViewPager设置适配器
        mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments));
        mViewPager.setCurrentItem(0);//设置当前显示标签页为第一页
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器

        mTabLayout.addTab(mTabLayout.newTab().setText("近期"));
        mTabLayout.addTab(mTabLayout.newTab().setText("笔记本"));
        mTabLayout.addTab(mTabLayout.newTab().setText("标签"));
        mTabLayout.setupWithViewPager(mViewPager);
//        mTabLayout.getTabAt(0).setText("近期");
//        mTabLayout.getTabAt(1).setText("笔记本");
//        mTabLayout.getTabAt(2).setText("标签");
    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshNotebookAndNotes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                ToastUtils.show(this, "再按一次退出");
                firstTime = secondTime;
                return true;
            } else {
                ((Leamonax) getApplication()).exit();
            }

        }
        return super.onKeyUp(keyCode, event);
    }

    private void fetchInfo() {
        AccountService.getInfo(AccountService.getCurrent().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(User user) {
                        AccountService.saveToAccount(user, AccountService.getCurrent().getHost());
                        refreshInfo();
                    }
                });
    }

    private void refreshInfo() {
        Account account = AccountService.getCurrent();
        if (!TextUtils.isEmpty(account.getAvatar())) {
            Glide.with(this)
                    .load(account.getAvatar())
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            mImageAvatar.setImageDrawable(resource);
                        }
                    });
        }
    }


    @OnClick(R.id.fab)
    void clickedFab() {
        Account account = AccountService.getCurrent();
        Note newNote = new Note();
        newNote.setUserId(account.getUserId());
        Notebook recentNotebook = AppDataBase.getRecentNoteBook(AccountService.getCurrent().getUserId());
        if (recentNotebook != null) {
            newNote.setNoteBookId(recentNotebook.getNotebookId());
        }
        newNote.setIsMarkDown(account.getDefaultEditor() == Account.EDITOR_MARKDOWN);
        newNote.save();
        Intent intent = NoteEditActivity.getOpenIntent(this, newNote.getId(), true);
        startActivity(intent);
    }


    private void refreshNotebookAndNotes() {
//        mNotebookAdapter.refresh();
//        if (mNoteFragment.getCurrentMode() == NoteFragment.Mode.NOTEBOOK) {
//            if (TextUtils.isEmpty(mNotebookAdapter.getCurrentParentId())) {
//                mNoteFragment.loadRecentNotes();
//            } else {
//                Notebook notebook = AppDataBase.getNotebookByServerId(mNotebookAdapter.getCurrentParentId());
//                mNoteFragment.loadFromNotebook(notebook.getId());
//            }
//        }
    }

    @OnClick(R.id.image_avatar)
    public void onClickAvatar() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.image_search)
    public void onClickSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
