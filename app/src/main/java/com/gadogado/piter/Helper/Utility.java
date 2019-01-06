package com.gadogado.piter.Helper;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gadogado.piter.Helper.Model.User;
import com.gadogado.piter.R;
import com.google.gson.Gson;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Utility {

    public interface DialogListener {
        void executeYes();
        void executeNo();
    }

    public interface HashtagListener {
        void performSearch(String hashtag);
    }

    private DialogListener dialogListener;
    private HashtagListener hashtagListener;

    public static User getUserInfo(Context context) {
        return new Gson().fromJson(LocalStorage.getItem(context, LocalStorage.USER_INFO), User.class);
    }

    public static String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Calendar.getInstance().getTime());
    }

    public static String getDateTimeFormat(String dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(dateTime.substring(0, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateTime.substring(5, 7)) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateTime.substring(8, 10)));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dateTime.substring(11, 13)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(dateTime.substring(14, 16)));

        return new SimpleDateFormat("E, dd MMM yyyy (HH:mm)", Locale.US).format(calendar.getTime());
    }

    public static void showAlertDialog(Context context, int title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, null);
        builder.create().show();
    }

    public static void showActionAlertDialog(Context context, int title, int message, boolean cancelable,
                                             final DialogListener dialogListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(cancelable);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogListener.executeYes();
            }
        });
        builder.create().show();
    }

    public static void showOptionAlertDialog(Context context, int title, int message, boolean cancelable,
                                             final DialogListener dialogListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(cancelable);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogListener.executeYes();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogListener.executeNo();
            }
        });
        builder.create().show();
    }

    public static SpannableString setTags(final Context context, String tweetMessage, final HashtagListener listener) {
        SpannableString spannableString = new SpannableString(tweetMessage);

        int start = -1;
        for (int i = 0; i < tweetMessage.length(); i++) {
            if (i != tweetMessage.length() - 1 && start == -1 && tweetMessage.charAt(i) == '#' && tweetMessage.charAt(i + 1) != ' ') {
                // start tag if find '#' but not at the end of the tweet or right before a space
                start = i;
            }
            else if (start != -1 && tweetMessage.charAt(i) == '#') {
                // don't set as tag if the word contains '#'
                start = -1;
            }
            else if (start != -1 && (tweetMessage.charAt(i) == ' ' || i == tweetMessage.length() - 1)) {
                // end tag if meet a space or is the end of the tweet
                if (i == tweetMessage.length() - 1) {
                    i++;
                }

                final String tag = tweetMessage.substring(start, i);

                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        listener.performSearch(tag);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(ContextCompat.getColor(context, R.color.colorLink));
                        ds.setUnderlineText(false);
                    }
                }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = -1;
            }
        }

        return spannableString;
    }

    public static List<String> getTags(String tweetMessage) {
        List<String> list = new ArrayList<>();
        String[] strArr = tweetMessage.split(" ");

        for (String i : strArr) {
            if (i.length() > 1 && i.startsWith("#") && !i.substring(1).contains("#")) {
                list.add(i);
            }
        }

        return list;
    }
}
