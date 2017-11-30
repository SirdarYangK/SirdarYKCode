package com.bjghhnt.app.treatmentdevice.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bjghhnt.app.treatmentdevice.Adapters.PropagandaListViewAdapter;
import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.service.PropagandaMusicService;
import com.bjghhnt.app.treatmentdevice.TreatmentApplication;
import com.bjghhnt.app.treatmentdevice.activities.fragment.propaganda.PropagandaAudioFragment;
import com.bjghhnt.app.treatmentdevice.activities.fragment.propaganda.PropagandaTxtFragment;
import com.bjghhnt.app.treatmentdevice.activities.fragment.propaganda.PropagandaVideoFragment;
import com.bjghhnt.app.treatmentdevice.bean.MediaBean;
import com.bjghhnt.app.treatmentdevice.utils.MusicUtils;
import com.bjghhnt.app.treatmentdevice.utils.serials.SerialHandler;
import com.bjghhnt.app.treatmentdevice.utils.serials.SimpleHandler;
import com.bjghhnt.app.treatmentdevice.views.NoScrollViewPager;
import com.squareup.otto.Subscribe;


import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 宣传片界面
 *
 */

public class PropagandaActivity extends MinorActivity implements
        RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener {
    private static final int BOOKS_SUCCESS = 500;
    @BindView(R.id.rb_propaganda_music)
    RadioButton mRbPropagandaMusic;
    @BindView(R.id.rb_propaganda_video)
    RadioButton mRbPropagandaVideo;
    @BindView(R.id.rb_propaganda_books)
    RadioButton mRbPropagandaBooks;
    @BindView(R.id.rb_propaganda_back)
    RadioButton mRbPropagandaBack;
    @BindView(R.id.rg_propaganda)
    RadioGroup mRgPropaganda;
    @BindView(R.id.vp_propaganda_detail)
    NoScrollViewPager mVpPropagandaDetail;
    @BindView(R.id.lv_propaganda_list)
    ListView mLvPropagandaList;
    @BindView(R.id.ll_propaganda)
    LinearLayout mLlPropaganda;
    @BindView(R.id.tv_time)
    TextView tvTimePropaganda;
    private List<MediaBean> mAudioList = new ArrayList<>();
    private List<MediaBean> mVideoList = new ArrayList<>();
    private List<File> mFileList = new ArrayList<>();
    private List<File> mBooksList = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();
    private PropagandaListViewAdapter mPropagandaAdapter;
    private ProgressDialog mProgressDialog;
    private PropagandaAudioFragment mPropagandaAudioFragment;
    private PropagandaMusicService mPropagandaMusicService;
    private PropagandaVideoFragment mPropagandaVideoFragment;
    private static final int VIDEO_SUCCESS = 101;
    private PropagandaTxtFragment mPropagandaTxtFragment;
    private static int FIRST_START = 1;
    private final SimpleHandler mSimHandler = new SimpleHandler(this);
    private Observer<Integer> obHandler = new Observer<Integer>() {
        @Override
        public void onNext(Integer integer) {
            if (integer.equals(FIRST_START)){
                mPropagandaAdapter.setList(PropagandaListViewAdapter.PropagandaChecked.CHECKED_MUSIC,
                        mAudioList);
                mPropagandaMusicService.setMusicListAndInitMediaPlayer(mAudioList);
                mPropagandaAudioFragment.setmAudioList(mAudioList);
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
            if (integer.equals(VIDEO_SUCCESS)){
                mPropagandaVideoFragment.setVideoList(mVideoList);
            }
            if (integer.equals(BOOKS_SUCCESS)){
                mPropagandaTxtFragment.setBooksList(mBooksList);
            }
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable t) {

        }

    };

    //更新时间
    @Override
    protected void upDateTime() {
        tvTimePropaganda.setText(formatTime);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propaganda);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected SerialHandler setHandler() {

        return mSimHandler;
    }

    private void init() {
        TreatmentApplication.getBusInstance().register(this);
        Intent intent = new Intent(PropagandaActivity.this, PropagandaMusicService.class);
        startService(intent);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在加载....");
        mProgressDialog.show();
        mProgressDialog.setCanceledOnTouchOutside(false);
        mVpPropagandaDetail.setOffscreenPageLimit(3);
        mPropagandaAdapter = new PropagandaListViewAdapter(PropagandaActivity.this);
        mRgPropaganda.setOnCheckedChangeListener(this);
        mRbPropagandaMusic.setChecked(true);
        FIRST_START = 1;
        mLvPropagandaList.setAdapter(mPropagandaAdapter);
        mLvPropagandaList.setOnItemClickListener(this);

        initBackground();
    }

    private void initBackground() {
        Observable
                .create((OnSubscribe<Bitmap>) e -> {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 8;
                    Bitmap backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.propaganda_background, options);
                    e.onNext(backgroundBitmap);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {

                    @Override
                    public void onNext(Bitmap bitmap) {
                        mLlPropaganda.setBackground(new BitmapDrawable(getResources()
                                , bitmap));

                        initDate();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                });

    }

    private void initDate() {
        Observable
                .create((OnSubscribe<Integer>) e -> {
                    initAudio();
                    e.onNext(FIRST_START);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(obHandler);
        Observable
                .create((OnSubscribe<Integer>) e -> {
                    initVideo();
                    e.onNext(VIDEO_SUCCESS);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(obHandler);
        Observable
                .create((OnSubscribe<Integer>) e -> {
                    initBooks();
                    e.onNext(BOOKS_SUCCESS);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(obHandler);
    }

    private void initBooks() {
        //通过反射获取所有可用的储存信息
        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            for (int i = 0; i < ((String[]) invoke).length; i++) {
//                System.out.println(((String[])invoke)[i]);
                //获得SD卡空间的信息
//              File path=Environment.getExternalStorageDirectory();
//              StatFs statFs=new StatFs(path.getPath());
                StatFs statFs = new StatFs(((String[]) invoke)[i]);
                long blocksize = statFs.getBlockSize();
                long totalblocks = statFs.getBlockCount();
                long availableblocks = statFs.getAvailableBlocks();

                //计算SD卡的空间大小
                long totalsize = blocksize * totalblocks;
                long availablesize = availableblocks * blocksize;

                //转化为可以显示的字符串
                String totalsize_str = Formatter.formatFileSize(this, totalsize);
                String availablesize_strString = Formatter.formatFileSize(this, availablesize);
                if (!totalsize_str.equals("0.00 B")) {
//                        System.out.println(((String[]) invoke)[i] + ":" + totalsize_str);
                    getAllFiles(new File(((String[]) invoke)[i]));
                }
//                System.out.println(((String[]) invoke)[i] + "--;totalsize:" + totalsize_str + "--;availablesize:" + availablesize_strString);
            }
            for (File file : mFileList) {
                if (file.getAbsolutePath().endsWith(".txt")) {
                    mBooksList.add(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initVideo() {
        Cursor cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.ARTIST,
                        MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DATA}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                MediaBean mediaBean = new MediaBean();
                mediaBean.mMediaTitle = cursor.getString(0);
                mediaBean.mMediaDuration = cursor.getString(1);
                mediaBean.mMediaArtist = cursor.getString(2);
                mediaBean.mMedia_ID = cursor.getLong(3);
                mediaBean.mMediaDispalyName = cursor.getString(4);
                mediaBean.mMediaData = cursor.getString(5);
                mVideoList.add(mediaBean);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void initAudio() {
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ALBUM_ID}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                MediaBean mediaBean = new MediaBean();
                mediaBean.mMediaTitle = cursor.getString(0);
                mediaBean.mMediaDuration = cursor.getString(1);
                mediaBean.mMediaArtist = cursor.getString(2);
                long audio_ID = cursor.getLong(3);
                mediaBean.mMedia_ID = audio_ID;
                mediaBean.mMediaDispalyName = cursor.getString(4);
                mediaBean.mMediaData = cursor.getString(5);
                long albumid = cursor.getLong(6);
                Bitmap artwork = MusicUtils.getArtwork(PropagandaActivity.this, audio_ID, albumid, true);
                if (artwork != null) {
                    mediaBean.album = artwork;
                }
                mAudioList.add(mediaBean);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    @Subscribe
    public void getMusicService(PropagandaMusicService musicService) {
        this.mPropagandaMusicService = musicService;
        initFragment(musicService);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        TreatmentApplication.getBusInstance().unregister(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (mRgPropaganda.getCheckedRadioButtonId()) {
            case R.id.rb_propaganda_music:
                mPropagandaAudioFragment.playMusic(position);
                mPropagandaAudioFragment.playOrPauseMusic();
                break;
            case R.id.rb_propaganda_video:
                mPropagandaVideoFragment.playVideo(position);
                break;
            case R.id.rb_propaganda_books:
                mPropagandaTxtFragment.setPosition(position);
//                Intent intent = new Intent();
//                intent.putExtra("bookname", "testbookname");
//                intent.putExtra("bookpath", mBooksList.get(position).getAbsolutePath());
//                intent.setClass(PropagandaActivity.this, HwReaderPlayActivity.class);
//                startActivity(intent);
                break;
        }
    }

    private void initFragment(PropagandaMusicService musicService) {
        mPropagandaAudioFragment = new PropagandaAudioFragment(musicService);
        mPropagandaVideoFragment = new PropagandaVideoFragment();
        mPropagandaTxtFragment = new PropagandaTxtFragment();
        mFragmentList.add(mPropagandaAudioFragment);
        mFragmentList.add(mPropagandaVideoFragment);
        mFragmentList.add(mPropagandaTxtFragment);
        mVpPropagandaDetail.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        });
    }


    private void getAllFiles(File root) {

        File files[] = root.listFiles();

        if (files != null)
            for (File f : files) {

                if (f.isDirectory()) {
                    getAllFiles(f);
                } else {
                    this.mFileList.add(f);
                }
            }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_propaganda_music:
                mPropagandaAdapter.setList(PropagandaListViewAdapter.PropagandaChecked.CHECKED_MUSIC,
                        mAudioList);
                mVpPropagandaDetail.setCurrentItem(0);
                break;
            case R.id.rb_propaganda_video:
                mPropagandaAdapter.setList(PropagandaListViewAdapter.PropagandaChecked.CHECKED_VIDEO,
                        mVideoList);
                mVpPropagandaDetail.setCurrentItem(1);
                break;
            case R.id.rb_propaganda_books:
                mPropagandaAdapter.setBooksList(PropagandaListViewAdapter.PropagandaChecked.CHECKED_BOOKS,
                        mBooksList);
                mVpPropagandaDetail.setCurrentItem(2);
                break;
            case R.id.rb_propaganda_back:
                finish();
                break;
        }
    }


}
