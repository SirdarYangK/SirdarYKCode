package com.bjghhnt.app.treatmentdevice.utils;

import com.bjghhnt.app.treatmentdevice.BuildConfig;

/**
 * a static class to describe channels
 * Created by Q on 19/01/2016.
 */
public class Channels {

    // the number of channels depends on build flavor
    public static final int TOTAL_CHN_NUM = BuildConfig.CHANNELS_IN_TOTALL;

    public static final int CHN_NULL = 0;

    public static final int CHN_ONE = 1;

    public static final int CHN_TWO = 2;

    public static final int CHN_THREE = 3;

    public static final int CHN_FOUR = 4;

    public static final int CHN_FIVE = 5;

    public static final String KEY_ALL_USE_KNOB = "all_use_knob";

//	"chn_t_1"; "chn_t_2"; "chn_t_3"; "chn_t_4"; "chn_t_5";

    public static final String KEY_CHN_SESSION_TIME_PREFIX = "chn_t_";

//	"chn_l_1"; "chn_l_2"; "chn_l_3"; "chn_l_4"; "chn_l_5";

    public static final String KEY_CHN_LEVEL_PREFIX = "chn_l_";

//	"chn_s_t_1"; "chn_s_t_2"; "chn_s_t_3"; "chn_s_t_4"; "chn_s_t_5";

    public static final String KEY_CHN_START_TIME_PREFIX = "chn_s_t_";

//	"chn_isOpen_1"; "chn_isOpen_2"; "chn_isOpen_3"; "chn_isOpen_4"; "chn_isOpen_5";

    public static final String KEY_CHN_IS_OPEN_PREFIX = "chn_isOpen_";

    public static final String INTENT_KEY_CHANNEL_NUMBER = "ChnNo";

    public static final int CHN_MAX_HRS = 3;

    public static final int CHN_MAX_MIN = 59;

    public static final int CHN_MAX_LEVEL = 30;

    public static final int CHN_MIN_VALUE_OF_EVERYTHING = 0;

    public static final int CHN_MIN_VALUE_ON_EVERYTHING = 99;

    public static final int CHN_RELOCK_NUMBER = 111;
    public static final int SWITCHINPUTMODE_TUCH = 0x122;
    public static final int SWITCHINPUTMODE_KNOB= 0x133;

    public static final String WEB_HOME_INDEX = "http://www.bjghhnt.com/page/zjll/index.php";

    public static final String PLAY_MUSIC_SUCCESS = "Music Play Success";
}
