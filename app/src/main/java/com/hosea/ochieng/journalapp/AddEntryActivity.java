package com.hosea.ochieng.journalapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hosea.ochieng.journalapp.DataUtils.JournalDateUtils;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEntryActivity extends AppCompatActivity implements View.OnClickListener {
    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;

    private TextInputEditText newEntrySubject;
    private Button dueDateBtn;
    private TextView dueDateTv;
    private EditText descriptionTv;
    private Button addNewEntryBtn;
    private Button cancelBtn;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private Calendar dateCalendar;
    int year, month, day, hour, minute;
    int yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        newEntrySubject = findViewById(R.id.new_entry_subject_text);
        dueDateBtn = findViewById(R.id.new_entry_due_date_label);
        dueDateTv = findViewById(R.id.new_entry_due_date);
        addNewEntryBtn = findViewById(R.id.add_entry_button);
        descriptionTv = findViewById(R.id.content_edit_text);
        cancelBtn = findViewById(R.id.cancel_adding_btn);
        dueDateBtn.setOnClickListener(this);
        addNewEntryBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        //Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        databaseReference.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        showToast(user.getUid());
        //initialize dateSetListener

        dateCalendar = Calendar.getInstance();

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                dateCalendar.set(year, month +1, day);
                yearFinal = y;
                monthFinal = m;
                dayFinal = d;

                hour = dateCalendar.get(Calendar.HOUR_OF_DAY);
                minute = dateCalendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEntryActivity.this, timeSetListener,
                        hour, minute, false);
                timePickerDialog.show();
            }
        };

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int min) {
                hourFinal = h;
                minuteFinal = min;
                dateCalendar.set(yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MM, yyy hh:mm");
                Date resultdate = new Date(dateCalendar.getTimeInMillis());
                dueDateTv.setText(sdf.format(resultdate));
            }
        };


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_entry_due_date_label:
                setEntryDueDate();
                break;

            case R.id.add_entry_button:
                addNewEntry(user.getUid(), newEntrySubject.getText().toString(),
                        getCurentDate(), new Date(dateCalendar.getTimeInMillis()),
                        descriptionTv.getText().toString(), false);
                break;

            case R.id.cancel_adding_btn:
                closeAddEntryActivity();

        }
    }

    private void closeAddEntryActivity() {
        this.onBackPressed();
    }

    private Date getCurentDate(){
        Date curDate = Calendar.getInstance().getTime();
        return curDate;
    }

    private void setEntryDueDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int montth = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dateDialog = new DatePickerDialog(
                AddEntryActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener,
                year,  montth, day
        );
        dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dateDialog.show();
    }

    private void addNewEntry(String userId, String subject, Date entryDate, Date dueDate, String desscription, Boolean completed){
        if(JournalDateUtils.validateDetailsAreGiven(newEntrySubject, dueDateTv, descriptionTv, dateCalendar.getTime(), this)) {

            String journalDbName = getString(R.string.journal_db_ref);
            String key = databaseReference.child(journalDbName).push().getKey();

            Journal journal = new Journal(userId, subject, entryDate, dueDate, desscription, completed);
            Map<String, Object> journalValues = journal.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/" + journalDbName + "/" + key, journalValues);
            databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Entry was successful!
                    showCloseContinueDialog();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Entry failed
                            showToast("Entry Failed!");
                        }
                    });
        }
    }

    private void clearWidgets() {
        newEntrySubject.setText("");
        dueDateTv.setText("");
        descriptionTv.setText("");
    }

    private void showCloseContinueDialog(){
        AlertDialog.Builder closeContinue = new AlertDialog.Builder(this);
        closeContinue.setTitle(getString(R.string.entry_successfull_title));
        closeContinue.setMessage(getString(R.string.entry_successfull_message));
        closeContinue.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        clearWidgets();
                    }
                });

        closeContinue.setNegativeButton(android.R.string.no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        closeContinue.show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
