package com.apkdv.leanote.adapter;


import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apkdv.leanote.R;
import com.apkdv.leanote.database.AppDataBase;
import com.apkdv.leanote.model.Notebook;
import com.apkdv.leanote.service.AccountService;
import com.apkdv.leanote.utils.CollectionUtils;

import java.util.List;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotebookAdapter extends RecyclerView.Adapter<NotebookAdapter.NotebookHolder> {

    private static final int TYPE_NOTEBOOK = 46;
    private static final int TYPE_ADD = 735;

    private Stack<String> mStack = new Stack<>();
    private List<Notebook> mData;
    private NotebookAdapterListener mListener;
    private boolean mHasAddButton = true;
    private boolean mCanOpenEmpty = true;
    private int currentSelection = -1;



    public NotebookAdapter setListener(NotebookAdapterListener listener) {
        mListener = listener;
        return this;
    }

    public NotebookAdapter setHasAddButton(boolean hasAddButton) {
        mHasAddButton = hasAddButton;
        return this;
    }

    public NotebookAdapter setCanOpenEmpty(boolean canOpenEmpty) {
        mCanOpenEmpty = canOpenEmpty;
        return this;
    }

    public void refresh() {
        getSafeNotebook(mStack);
        notifyDataSetChanged();
    }

    private void getSafeNotebook(Stack<String> stack) {
        if (stack.isEmpty()) {
            //查询主目录
            mData = AppDataBase.getRootNotebooks(AccountService.getCurrent().getUserId());
        } else {
            Notebook parent = AppDataBase.getNotebookByServerId(stack.peek());
            if (parent.isDeleted()) {
                stack.pop();
                getSafeNotebook(stack);
            } else {
                mData = AppDataBase.getChildNotebook(mStack.peek(), AccountService.getCurrent().getUserId());
                mData.add(0, parent);
            }
        }
    }

    public String getCurrentParentId() {
        return mStack.size() == 0 ? "" : mStack.peek();
    }

    @Override
    public int getItemViewType(int position) {
        if (mHasAddButton && position == getItemCount() - 1) {
            return TYPE_ADD;
        } else {
            return TYPE_NOTEBOOK;
        }
    }

    @Override
    public NotebookAdapter.NotebookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_ADD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_notebook, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notebook, parent, false);
        }
        return new NotebookHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotebookAdapter.NotebookHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ADD) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onClickedAddNotebook(getCurrentParentId());
                    }
                }
            });
            return;
        }

        final Notebook notebook = mData.get(position);
        if (!TextUtils.isEmpty(notebook.getParentNotebookId())) {
            holder.itemView.setBackgroundColor(0xFFF7F7F7);
        } else holder.itemView.setBackgroundColor(Color.WHITE);

        holder.titleTv.setText(notebook.getTitle());

        String notebookId = notebook.getNotebookId();
        boolean isSuper = isSuper(notebookId);
        boolean isSuperOrRoot = isSuper | mStack.isEmpty();//判断是否是最后的目录（没有子节点）
        boolean hasChild = hasChild(notebookId);//是否包含子目录
        holder.placeholder.setVisibility(isSuperOrRoot ? View.GONE : View.VISIBLE);
        holder.navigator.setVisibility(mCanOpenEmpty | hasChild ? View.VISIBLE : View.INVISIBLE);
        holder.navigator.setImageResource(R.drawable.icon_notebook_close);
//        holder.navigator.setImageResource(isSuper ? R.drawable.ic_expanding : R.drawable.ic_expandable);
        holder.navigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSuper(notebook.getNotebookId())) {
                    listUpper(notebook);
                    holder.navigator.setImageResource(R.drawable.icon_notebook_close);
                } else {
                    listChild(notebook);
                    holder.navigator.setImageResource(R.drawable.icon_notebook_open);
                }
            }
        });
        holder.titleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
//                    listChild(notebook);
                    //开打相关目录下的笔记
                    mListener.onClickedNotebook(notebook);
                }
            }
        });
    }

    private boolean isSuper(String notebookId) {
        if (mStack.isEmpty()) {
            return false;
        } else {//判断点击的是不是展开的,只要能搜到就表示已经展开
            return mStack.search(notebookId) != -1;

//            return mStack.peek().equals(notebookId);
        }
    }

    private boolean hasChild(String notebookId) {
        return CollectionUtils.isNotEmpty(AppDataBase.getChildNotebook(notebookId, AccountService.getCurrent().getUserId()));
    }

    private void listUpper(Notebook notebook) {
        int index = mData.indexOf(notebook);
//        int childrenSize = mData.size();
//        mData = new ArrayList<>();
//        notifyItemRangeRemoved(0, childrenSize);
        int childrenSize = 0;
        //如果没有ParentNotebookId,则是最顶部的节点，清空栈
        if (TextUtils.isEmpty(notebook.getParentNotebookId())) mStack.clear();
        else
            mStack.pop();//删除栈顶的ID
//
        //点击notebook后，遍历该节点。
        List<Notebook> children = AppDataBase.getChildNotebook(notebook.getNotebookId(), AccountService.getCurrent().getUserId());
        childrenSize = children.size();
        for (Notebook child : children) {
            childrenSize += findChildFromID(child);
            removeDataFromList(child);
        }
        notifyDataSetChanged();
//        notifyItemRangeRemoved(index+1, childrenSize);

    }

    private void listChild(Notebook notebook) {
        int index = mData.indexOf(notebook);

        mStack.push(notebook.getNotebookId());//将该ID推到栈顶
        //获取子目录
        List<Notebook> children = AppDataBase.getChildNotebook(notebook.getNotebookId(), AccountService.getCurrent().getUserId());
        int childrenSize = children.size();
        mData.addAll(index + 1, children);
        notifyItemRangeInserted(index + 1, childrenSize);
    }

    private int findChildFromID(Notebook notebook) {
        List<Notebook> children = AppDataBase.getChildNotebook(notebook.getNotebookId(), AccountService.getCurrent().getUserId());
        if (!children.isEmpty()) {
            for (Notebook child : children)  removeDataFromList(child);
        }
        return children.size();
    }


    private void removeDataFromList(Notebook notebook) {
        for (int i = 0; i < mData.size(); i++) {
            if (TextUtils.equals(notebook.getParentNotebookId(), mData.get(i).getParentNotebookId()) &&
                    TextUtils.equals(notebook.getNotebookId(), mData.get(i).getNotebookId()))
                mData.remove(i);
        }
    }


    @Override
    public int getItemCount() {
        int fixed = mHasAddButton ? 1 : 0;
        return mData == null ? fixed : mData.size() + fixed;
    }

    public interface NotebookAdapterListener {
        void onClickedNotebook(Notebook notebook);

        void onClickedAddNotebook(String parentNotebookId);
    }

    static class NotebookHolder extends RecyclerView.ViewHolder {
        View itemView;
        @BindView(R.id.navigator)
        ImageView navigator;
        @BindView(R.id.tv_title)
        TextView titleTv;
        @Nullable
        @BindView(R.id.placeholder)
        View placeholder;

        public NotebookHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}