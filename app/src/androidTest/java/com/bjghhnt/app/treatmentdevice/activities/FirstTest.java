package com.bjghhnt.app.treatmentdevice.activities;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.bjghhnt.app.treatmentdevice.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FirstTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void firstTest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_tile_treatment), withText("治疗仪"), isDisplayed()));
        appCompatButton.perform(click());
        
        ViewInteraction treatingMeterView = onView(
allOf(withId(R.id.custom_view_chn_3), isDisplayed()));
        treatingMeterView.perform(click());
        
        ViewInteraction toggleButton = onView(
allOf(withId(R.id.btn_meter_settings_confirm_time), withText("停止"), isDisplayed()));
        toggleButton.perform(click());
        
        ViewInteraction toggleButton2 = onView(
allOf(withId(R.id.btn_meter_settings_confirm_time), withText("开始"), isDisplayed()));
        toggleButton2.perform(click());
        
        ViewInteraction toggleButton3 = onView(
allOf(withId(R.id.btn_meter_settings_confirm_time), withText("停止"), isDisplayed()));
        toggleButton3.perform(click());
        
        ViewInteraction appCompatButton2 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton2.perform(click());
        
        ViewInteraction appCompatButton3 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton3.perform(click());
        
        ViewInteraction appCompatButton4 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton4.perform(click());
        
        ViewInteraction appCompatButton5 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton5.perform(click());
        
        ViewInteraction appCompatButton6 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton6.perform(click());
        
        ViewInteraction appCompatButton7 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton7.perform(click());
        
        ViewInteraction appCompatButton8 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton8.perform(click());
        
        ViewInteraction appCompatButton9 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton9.perform(click());
        
        ViewInteraction appCompatButton10 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton10.perform(click());
        
        ViewInteraction appCompatButton11 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton11.perform(click());
        
        ViewInteraction appCompatButton12 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton12.perform(click());
        
        ViewInteraction appCompatButton13 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton13.perform(click());
        
        ViewInteraction appCompatButton14 = onView(
allOf(withId(R.id.btn_meter_final_confirm), withText("确定"),
withParent(allOf(withId(R.id.linear_layout_level_setter),
withParent(withId(R.id.linear_layout_settings)))),
isDisplayed()));
        appCompatButton14.perform(click());
        
        ViewInteraction appCompatButton15 = onView(
allOf(withId(R.id.btn_meter_settings_back), withText("返回"), isDisplayed()));
        appCompatButton15.perform(click());
        
        ViewInteraction treatingMeterView2 = onView(
allOf(withId(R.id.custom_view_chn_3), isDisplayed()));
        treatingMeterView2.perform(click());
        
        ViewInteraction toggleButton4 = onView(
allOf(withId(R.id.btn_meter_settings_confirm_time), withText("停止"), isDisplayed()));
        toggleButton4.perform(click());
        
        ViewInteraction appCompatButton16 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton16.perform(click());
        
        ViewInteraction appCompatButton17 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton17.perform(click());
        
        ViewInteraction appCompatButton18 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton18.perform(click());
        
        ViewInteraction appCompatButton19 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton19.perform(click());
        
        ViewInteraction appCompatButton20 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton20.perform(click());
        
        ViewInteraction appCompatButton21 = onView(
allOf(withId(R.id.btn_meter_final_confirm), withText("确定"),
withParent(allOf(withId(R.id.linear_layout_level_setter),
withParent(withId(R.id.linear_layout_settings)))),
isDisplayed()));
        appCompatButton21.perform(click());
        
        ViewInteraction appCompatButton22 = onView(
allOf(withId(R.id.btn_meter_settings_back), withText("返回"), isDisplayed()));
        appCompatButton22.perform(click());
        
        ViewInteraction treatingMeterView3 = onView(
allOf(withId(R.id.custom_view_chn_4), isDisplayed()));
        treatingMeterView3.perform(click());
        
        ViewInteraction toggleButton5 = onView(
allOf(withId(R.id.btn_meter_settings_confirm_time), withText("停止"), isDisplayed()));
        toggleButton5.perform(click());
        
        ViewInteraction appCompatButton23 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton23.perform(click());
        
        ViewInteraction appCompatButton24 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton24.perform(click());
        
        ViewInteraction appCompatButton25 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton25.perform(click());
        
        ViewInteraction appCompatButton26 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton26.perform(click());
        
        ViewInteraction appCompatButton27 = onView(
allOf(withId(R.id.btn_meter_final_confirm), withText("确定"),
withParent(allOf(withId(R.id.linear_layout_level_setter),
withParent(withId(R.id.linear_layout_settings)))),
isDisplayed()));
        appCompatButton27.perform(click());
        
        ViewInteraction appCompatButton28 = onView(
allOf(withId(R.id.btn_meter_settings_back), withText("返回"), isDisplayed()));
        appCompatButton28.perform(click());
        
        ViewInteraction appCompatButton29 = onView(
allOf(withId(R.id.btn_setting_go_back), withText("返回"), isDisplayed()));
        appCompatButton29.perform(click());
        
        ViewInteraction appCompatButton30 = onView(
                allOf(withId(R.id.btn_tile_treatment), withText("治疗仪"), isDisplayed()));
        appCompatButton30.perform(click());
        
        ViewInteraction treatingMeterView4 = onView(
allOf(withId(R.id.custom_view_chn_5), isDisplayed()));
        treatingMeterView4.perform(click());
        
        ViewInteraction toggleButton6 = onView(
allOf(withId(R.id.btn_meter_settings_confirm_time), withText("停止"), isDisplayed()));
        toggleButton6.perform(click());
        
        ViewInteraction appCompatButton31 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton31.perform(click());
        
        ViewInteraction appCompatButton32 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton32.perform(click());
        
        ViewInteraction appCompatButton33 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton33.perform(click());
        
        ViewInteraction appCompatButton34 = onView(
allOf(withId(R.id.btn_meter_set_plus), withText("+"), isDisplayed()));
        appCompatButton34.perform(click());
        
        ViewInteraction appCompatButton35 = onView(
allOf(withId(R.id.btn_meter_final_confirm), withText("确定"),
withParent(allOf(withId(R.id.linear_layout_level_setter),
withParent(withId(R.id.linear_layout_settings)))),
isDisplayed()));
        appCompatButton35.perform(click());
        
        ViewInteraction appCompatButton36 = onView(
allOf(withId(R.id.btn_meter_settings_back), withText("返回"), isDisplayed()));
        appCompatButton36.perform(click());
        
        }
}
