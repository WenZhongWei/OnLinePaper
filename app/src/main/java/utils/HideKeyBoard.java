package utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by xx on 2016/5/16.
 * 关闭软键盘方法
 */
public class HideKeyBoard {


    public static void hide(Activity activity)
    {
        //得到InputMethodManager实例
        InputMethodManager imm= (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()&&activity.getCurrentFocus()!=null)
        {
            if (activity.getCurrentFocus().getWindowToken()!=null) {

                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
