package com.bjghhnt.app.treatmentdevice.activities.fragment.propaganda;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.TreatmentApplication;
import com.hw.hwreaderui.HwReaderPlayActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Development_Android on 2016/12/15.
 */

public class PropagandaTxtFragment extends Fragment {

    private static final int READ_BOOK_SUCCESS = 524;
    private static final int READ_BOOK_ERROR = 8956;
    @BindView(R.id.tv_txt)
    TextView mTvTxt;
    @BindView(R.id.btn_start_txt)
    Button mBtnStartTxt;
    private List<File> mBooksList;
    private int mPosition = 0;
    public PropagandaTxtFragmentHandler mHandler;

    private static class PropagandaTxtFragmentHandler extends Handler {

        WeakReference<PropagandaTxtFragment> mFragmentReference;

        PropagandaTxtFragmentHandler(PropagandaTxtFragment propagandaTxtFragment) {
            mFragmentReference = new WeakReference<>(propagandaTxtFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PropagandaTxtFragment propagandaTxtFragment = mFragmentReference.get();
            if (msg.what == READ_BOOK_SUCCESS) {
                String strBooks = (String) msg.obj;
                propagandaTxtFragment.mTvTxt.setText(strBooks);
            }
            if (msg.what == READ_BOOK_ERROR) {
                Toast.makeText(propagandaTxtFragment.getActivity(), "读取失败", Toast.LENGTH_SHORT).show();
            }

        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_propaganda_txt, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler = new PropagandaTxtFragmentHandler(this);

    }


    public String getString(InputStream inputStream) {
        int readLine = 0;
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "gbk");
        } catch (UnsupportedEncodingException e1) {
            mHandler.sendEmptyMessage(READ_BOOK_ERROR);
        }
        if (inputStreamReader == null) {
            return "";
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder("");
        sb.append("文件：" + mBooksList.get(mPosition).getAbsolutePath() + "\n");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            mHandler.sendEmptyMessage(READ_BOOK_ERROR);
        }
        readLine++;
        if (readLine == 30) {
            return sb.toString();
        }
        return sb.toString();
    }


    @OnClick(R.id.btn_start_txt)
    public void onClick() {

        if (!mBooksList.isEmpty()){
            Intent intent = new Intent();
            intent.putExtra("bookname", "testbookname");
            intent.putExtra("bookpath", mBooksList.get(mPosition).getAbsolutePath());
            intent.setClass(getActivity(), HwReaderPlayActivity.class);
            startActivity(intent);
        }
    }

    public void setBooksList(List<File> booksList) {
        this.mBooksList = booksList;
        readAndShowBooks();
    }

    public void setPosition(int position) {
        this.mPosition = position;
        readAndShowBooks();
    }

    private synchronized void readAndShowBooks() {
        new Thread(() -> {
            try {
                if (mBooksList.isEmpty()) {
                    return;
                }
                String string = getString(new FileInputStream(mBooksList.get(mPosition)));
                Message msg = new Message();
                msg.what = READ_BOOK_SUCCESS;
                msg.obj = string;
                mHandler.sendMessage(msg);


//            while ((mimeTypeLine = br.readLine()) != null) {
//                str = str + mimeTypeLine;
//            }
            } catch (Exception e) {
                mHandler.sendEmptyMessage(READ_BOOK_ERROR);
            }
        }).start();
    }
}
