package com.bjghhnt.app.treatmentdevice.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.bean.MediaBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Development_Android on 2016/12/12.
 */

public class PropagandaListViewAdapter extends BaseAdapter {
    List<MediaBean> mMediaList = new ArrayList<>();
    List<File> mBooksList = new ArrayList<>();
    PropagandaChecked mPropagandaChecked = PropagandaChecked.CHECKED_MUSIC;
    Context mContext;

    public PropagandaListViewAdapter(Context context) {
        this.mContext = context;
    }

    public enum PropagandaChecked {
        CHECKED_MUSIC, CHECKED_VIDEO, CHECKED_BOOKS;

    }

    public void setList(PropagandaChecked propagandaChecked, List<MediaBean> list) {
        this.mPropagandaChecked = propagandaChecked;
        this.mMediaList.clear();
        this.mMediaList.addAll(list);
        notifyDataSetChanged();
    }

    public void setBooksList(PropagandaChecked propagandaChecked, List<File> list) {
        this.mPropagandaChecked = propagandaChecked;
        this.mBooksList.clear();
        this.mBooksList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPropagandaChecked.equals(PropagandaChecked.CHECKED_BOOKS) ?
                mBooksList.size() == 0 ? 1 : mBooksList.size() : mMediaList.size() == 0 ? 1 : mMediaList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPropagandaChecked.equals(PropagandaChecked.CHECKED_BOOKS) ?
                mBooksList.get(position) : mMediaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mBooksList.size() == 0 && mPropagandaChecked.equals(PropagandaChecked.CHECKED_BOOKS)){
            TextView textView = new TextView(parent.getContext());
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(50);
            textView.setText("未扫描到文件");
            return textView;
        }
        if (mMediaList.size() == 0){
            if (mPropagandaChecked.equals(PropagandaChecked.CHECKED_MUSIC) || mPropagandaChecked.equals(PropagandaChecked.CHECKED_VIDEO)) {
                TextView textView = new TextView(parent.getContext());
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(50);
                textView.setText("未扫描到文件");
                return textView;
            }
        }
        ViewHolder viewHolder;
        if (convertView == null || convertView instanceof TextView) {
            convertView = View.inflate(mContext, R.layout.propaganda_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mPropagandaChecked.equals(PropagandaChecked.CHECKED_MUSIC)) {
            viewHolder.ivMediaIcon.setBackgroundResource(R.drawable.propaganda_music);
        }
        if (mPropagandaChecked.equals(PropagandaChecked.CHECKED_VIDEO)) {
            viewHolder.ivMediaIcon.setBackgroundResource(R.drawable.propaganda_video);
        }
        if (mPropagandaChecked.equals(PropagandaChecked.CHECKED_BOOKS)) {
            viewHolder.ivMediaIcon.setBackgroundResource(R.drawable.propaganda_books);
        }
        if (mPropagandaChecked.equals(PropagandaChecked.CHECKED_BOOKS)) {
            File file = mBooksList.get(position);
            viewHolder.tvMediaName.setText(file.getName());
            viewHolder.tvAddress.setText(file.getAbsolutePath());
        } else {
            MediaBean mediaBean = mMediaList.get(position);
            viewHolder.tvMediaName.setText(mediaBean.mMediaTitle);
            viewHolder.tvAddress.setText(mediaBean.mMediaData);
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_media_icon)
        ImageView ivMediaIcon;
        @BindView(R.id.tv_media_name)
        TextView tvMediaName;
        @BindView(R.id.tv_address)
        TextView tvAddress;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
