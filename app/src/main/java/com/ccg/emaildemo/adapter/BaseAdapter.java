package com.ccg.emaildemo.adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder> {

    private static final String TAG = "BaseAdapter";

    private Context mContext;
    private List<T> mDatalist;


    public BaseAdapter(Context context) {
        this(context,null);
    }

    public BaseAdapter(Context context, List<T> datas) {
        this.mContext = context;
        if (datas == null){
            mDatalist = new ArrayList<>();
        }else{
            mDatalist = datas;
        }
    }

    @NonNull
    @Override
    public BaseAdapter.BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(getItemLayoutId(viewType),parent,false);
        return new BaseViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BaseAdapter.BaseViewHolder holder, int position) {
        if (position < mDatalist.size()){
            T data = mDatalist.get(position);
            bindView(holder,data,position);
        }
    }

    @Override
    public int getItemCount() {
        if (mDatalist == null){
            mDatalist = new ArrayList<>();
        }
        return mDatalist.size();
    }

    protected abstract int getItemLayoutId(int viewType);
    protected abstract void bindView(BaseViewHolder holder , T data,int position);


    public Context getContext(){
        return mContext;
    }


    //==========================数据更新方法  start==============================

    /**
     * 设置数据，直接替换原来的数据
     * @param datas
     */
    public void setDatas(List<T> datas){
        if (datas == null){
            throw new RuntimeException("adapter setDatas(List<T> datas),datas is null.");
        }
        if (mDatalist == null){
            mDatalist = new ArrayList<>();
        }
        int size = mDatalist.size();
        mDatalist.clear();
        notifyItemRangeRemoved(0,size);
        mDatalist.addAll(datas);
        notifyItemRangeInserted(0,datas.size());
    }

    /**
     * 更新某一个item的数据
     * @param position item所处的位置索引
     * @param data 要替换的数据
     */
    public void updateData(int position,T data){
        if (mDatalist == null){
            return;
        }
        if (position > mDatalist.size()){
            throw new ArrayIndexOutOfBoundsException("updateData(int position,T data),position = "+position+",datas length:"+mDatalist.size());
        }
        mDatalist.remove(position);
        mDatalist.add(position,data);
        notifyItemChanged(position);
    }

    /**
     * 获取适配器中的所有数据
     * @return 所有数据
     */
    public List<T> getDatas(){
        if (mDatalist == null){
            return new ArrayList<>();
        }
        return mDatalist;
    }

    /**
     * 添加数据，在原本已有的数据基础上，继续加入数据
     * @param records 要加入的数据
     */
    public void addDatas(List<T> records){
        Log.e(TAG, "addDatas: "+records.size() );
        if (this.mDatalist == null){
            mDatalist = new ArrayList<>();
        }
        int start = mDatalist.size();
        this.mDatalist.addAll(records);
        notifyItemRangeInserted(start,records.size());
    }

    /**
     * 移除掉某个item
     * @param position
     */
    public void removeData(int position){
        if (mDatalist == null){
            return;
        }
        if (position < mDatalist.size()){
            mDatalist.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeAll(){
        if (mDatalist == null){
            return;
        }
        int size = mDatalist.size();
        mDatalist.clear();
        notifyItemRangeRemoved(0,size);
    }

    /**
     * 获取某个item的数据
     * @param position
     * @return
     */
    public T getItemData(int position){
        if (mDatalist == null || position >= mDatalist.size()){
            return null;
        }
        return mDatalist.get(position);
    }

    //==========================数据更新方法  end==============================

  public class BaseViewHolder extends RecyclerView.ViewHolder{

       private SparseArray<View> mViewList;

        BaseViewHolder(@NonNull View itemView) {
            super(itemView);
            mViewList = new SparseArray<>();
        }

        public <V extends View> V getView(int viewId){
            View view = mViewList.get(viewId);
            if (view == null){
                view = itemView.findViewById(viewId);
                mViewList.put(viewId,view);
            }
            return (V) view;
        }

        public View getContentView(){
            return itemView;
        }
    }
}
