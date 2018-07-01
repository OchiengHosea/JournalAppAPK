package com.hosea.ochieng.journalapp.DataUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.hosea.ochieng.journalapp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class JournalDateUtils {
    public static String getReadableDate(Date systemDate){
        DateFormat format = new SimpleDateFormat("MMM dd, yyyy HHMM");
        return format.format(systemDate);
    }

    public static boolean validateDetailsAreGiven(TextInputEditText subject, TextView dueDate, EditText content, Date dueD, Context context){
        boolean valid = true;
        if(TextUtils.isEmpty(subject.getText().toString().trim())){
            valid = false;
            alertMessageDialog(context.getString(R.string.entry_error),
                    "You Must provide Subject", context);
            return valid;
        }
        if(TextUtils.isEmpty(dueDate.getText().toString().trim())){
            valid = false;
            alertMessageDialog(context.getString(R.string.entry_error),
                    "You Must provide a due date", context);
            return valid;
        }
        if(TextUtils.isEmpty(content.getText().toString().trim())){
            valid = false;
            alertMessageDialog(context.getString(R.string.entry_error),
                    "You Must provide Content", context);
            return valid;
        }
        if(dueDateIsBeforeToday(dueD)){
            valid = false;
            alertMessageDialog(context.getString(R.string.entry_error),
                    "Due date cannot be before today!", context);
            return valid;
        }
        return valid;
    }

    public static boolean dueDateIsBeforeToday(Date dueDate){
        Calendar c = Calendar.getInstance();

    // set the calendar to start of today
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date today = c.getTime();

        return dueDate.before(today);
    }

    public static void alertMessageDialog(String title, String message, Context context){
        final AlertDialog.Builder closeContinue = new AlertDialog.Builder(context);
        closeContinue.setTitle(title);
        closeContinue.setMessage(message);
        closeContinue.setNeutralButton(android.R.string.no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        closeContinue.show();
    }

    public static void alertSuccessMessageDialog(String title, String message, final Context context){
        final AlertDialog.Builder closeContinue = new AlertDialog.Builder(context);
        closeContinue.setTitle(title);
        closeContinue.setMessage(message);
        closeContinue.setNeutralButton(android.R.string.no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
        closeContinue.show();
    }

    public static String getFirst100Words(String contentDescription){
        String result = contentDescription;
        if(contentDescription.length() > 50){
            result = contentDescription.substring(0,20);
        }

        return result.concat("...");
    }
}
