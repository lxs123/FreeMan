package com.toocms.freeman.ui.util;

import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.widget.EditText;

/**
 * 金额edittext 保留后两位
 * Created by admin on 2017/5/8.
 */

public class MoneyUtils {
    //金融EditText
    public static void setPrice(final EditText editText) {
//        editText.setFilters(new InputFilter[]{new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                if ((source.equals(".") && dest.toString().length() == 0) || (source.equals("0")
//                        && dest.toString().length() == 0)) {
//                    return "0.";
//                }
//                //
//                if (!TextUtils.isEmpty(dest.toString()))
//                    if (dest.toString().contains("-")) {
//                        if ((source.equals(".") && dest.toString().length() == 1) || (source.equals("0")
//                                && dest.toString().length() == 1)) {
//                            return "- 0.";
//                        }
//                    }
//
//                if (dest.toString().contains(".")) {
//                    int index = dest.toString().indexOf(".");
//                    int mlength = dest.toString().substring(index).length();
//                    if (mlength >= 3) {
////                        editText.setText();
//                        return "";
//                    }
//                }
//                return null;
//            }
//        }});
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
//                if (s.toString().length() == 2 && s.toString().trim().substring(1).equals("-")) {
//                    if (s.toString().substring(0, 2).equals("-0")) {
//                        s = s + ".";
//                        editText.setText(s);
//                        editText.setSelection(3);
//                    }
//                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /**
         * 设置edittext里可输入的字符         */
        editText.setKeyListener(new NumberKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_PHONE;
            }

            @Override
            protected char[] getAcceptedChars() {
                String trim = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(trim)) {
                    if (trim.contains(".")) {
                        return new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9',
                                '0'};
                    } else if (TextUtils.equals(trim.substring(1), "-") || trim.length() > 0) {
                        return new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9',
                                '0', '.'};
                    } else {
                        return new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9',
                                '0', '.'};
                    }
                } else {
                    return new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9',
                            '0', '.'};
                }

            }
        });
    }

    //设置带有正负数的编辑框
    public static void setHasPrice(final EditText editMoney) {
        /**
         * 设置edittext里可输入的字符         */
        editMoney.setKeyListener(new NumberKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_PHONE;
            }

            @Override
            protected char[] getAcceptedChars() {
                String trim = editMoney.getText().toString().trim();
                if (!TextUtils.isEmpty(trim)) {
                    if (trim.contains(".")) {
                        return new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9',
                                '0'};
                    } else if (TextUtils.equals(trim.substring(1), "-") || trim.length() > 0) {
                        return new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9',
                                '0', '.'};
                    } else {
                        return new char[]{'-', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                '0', '.', '+'};
                    }
                } else {
                    return new char[]{'-', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                            '0', '.', '+'};
                }

            }
        });
        editMoney.addTextChangedListener(new TextWatcher() {
            int a = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().length() < 2) {
                    a = 0;
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        editMoney.setText(s);
                        editMoney.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editMoney.setText(s);
                    editMoney.setSelection(2);
                }

                if (s.toString().length() == 2 && a == 0) {
                    if (s.toString().substring(0, 2).equals("-0")) {
                        s = s + ".";
                        editMoney.setText(s);
                        editMoney.setSelection(3);
                        a = 1;
                    } else if (s.toString().substring(0, 2).equals("-.")) {
                        s = "-0.";
                        editMoney.setText(s);
                        editMoney.setSelection(3);
                        a = 1;
                    }
                }
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editMoney.setText(s.subSequence(0, 1));
                        editMoney.setSelection(1);
                        return;
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
