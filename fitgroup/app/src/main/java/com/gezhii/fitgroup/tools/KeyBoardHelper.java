package com.gezhii.fitgroup.tools;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyBoardHelper {

    public static void hideKeyBoard(Context context, EditText mEditText) {
        if (mEditText == null || context == null)
            return;
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && mEditText.isFocusable()) {
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }
    }

    public static void showKeyBoard(Context context, EditText v) {
        if (v == null || context == null)
            return;
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


}