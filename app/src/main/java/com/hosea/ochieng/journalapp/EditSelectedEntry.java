package com.hosea.ochieng.journalapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditSelectedEntry extends AppCompatActivity implements View.OnClickListener {
    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;

    private TextInputEditText subject;
    private TextView dueDAte;
    private Button editDueDateBtn;
    private CheckBox completed;
    private TextView description;
    private Button cancelBtn;
    private Button saveEditsBtn;
    private Button deleteBtn;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Journal selectedJournal = null;
    FirebaseAuth.AuthStateListener mAuthListener;

    private Calendar dateCalendar;

    int year, month, day, hour, minute;
    int yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_selected_entry);

        Intent intent = getIntent();
        if(intent.hasExtra("selectedJournal")){
            showToast("found instance");

            dateCalendar = Calendar.getInstance();
            subject = findViewById(R.id.entry_textinput_edit);
            dueDAte = findViewById(R.id.detail_edit_due_date_tv);
            editDueDateBtn = findViewById(R.id.detail_due_date_edit);
            completed = findViewById(R.id.detail_completed_check);
            description = findViewById(R.id.editText4);
            cancelBtn = findViewById(R.id.edit_cancel_button);
            saveEditsBtn = findViewById(R.id.save_edits_btn);
            deleteBtn = findViewById(R.id.delete_btn);

            selectedJournal = (Journal)intent.getSerializableExtra("selectedJournal");
            subject.setText(selectedJournal.getSubject());
            dueDAte.setText(selectedJournal.getDueDate().toString());
            completed.setChecked(selectedJournal.getCompleted());
            description.setText(selectedJournal.getDescription());

            saveEditsBtn.setOnClickListener(this);
            cancelBtn.setOnClickListener(this);
            editDueDateBtn.setOnClickListener(this);
            deleteBtn.setOnClickListener(this);

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    updateUi(firebaseAuth.getCurrentUser());
                }
            };

            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.keepSynced(true);
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();


            dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                    dateCalendar.set(year, month +1, day);
                    yearFinal = y;
                    monthFinal = m;
                    dayFinal = d;

                    hour = dateCalendar.get(Calendar.HOUR_OF_DAY);
                    minute = dateCalendar.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(EditSelectedEntry.this, timeSetListener,
                            hour, minute, true);
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
                    dueDAte.setText(sdf.format(resultdate));
                }
            };
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_edits_btn:
                saveEntryEdits();
                break;

            case R.id.edit_cancel_button:
                cancelViewingEntry();
                break;

            case R.id.detail_due_date_edit:
                showDateTimePicker();
                break;
            case R.id.delete_btn:
                showDeleteDialog();
        }
    }

    private void showDeleteDialog(){
        AlertDialog.Builder closeContinue = new AlertDialog.Builder(this);
        closeContinue.setTitle(getString(R.string.confirm_delete_text));
        closeContinue.setMessage(getString(R.string.confirm_delete_message));
        closeContinue.setPositiveButton(getString(R.string.delete_label),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteJournal();
                    }
                });

        closeContinue.setNegativeButton(android.R.string.no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        closeContinue.show();
    }

    private void deleteJournal() {
        databaseReference.child(getString(R.string.journal_db_ref))
                .child(selectedJournal.getJournalID()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                alertSuccessMessageDialog("Deleted", "The Entry Has been Deleted", EditSelectedEntry.this);
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
            public void onFailure(@NonNull Exception e) {
                // Edit failed
                showToast("Delete Failed!");
            }
        });
    }

    public static void alertSuccessMessageDialog(String title, String message, final Context context){
        final AlertDialog.Builder closeContinue = new AlertDialog.Builder(context);
        closeContinue.setTitle(title);
        closeContinue.setMessage(message);
        closeContinue.setNeutralButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
        closeContinue.show();
    }

    private void showDateTimePicker() {
        showToast("pick date");
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dateDialog = new DatePickerDialog(
                EditSelectedEntry.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener,
                year,  month, day
        );
        dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dateDialog.show();
    }



    private void saveEntryEdits() {
        selectedJournal.setSubject(subject.getText().toString());
        selectedJournal.setDueDate(new Date(dateCalendar.getTimeInMillis()));
        selectedJournal.setCompleted(completed.isChecked());
        selectedJournal.setDescription(description.getText().toString());
        databaseReference.child(getString(R.string.journal_db_ref)).child(selectedJournal.getJournalID())
                .setValue(selectedJournal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Edit was successful!
                    showToast("Edit  Successful!");
                    goToEntriesActivity();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Edit failed
                    showToast("Edit Failed!");
                }
        });

    }

    private void cancelViewingEntry() {
        this.onBackPressed();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void goToEntriesActivity(){
        /*finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);*/
        finish();
    }

    private void updateUi(FirebaseUser user){
        if(user != null){
            showToast("Editing Started!");
        }else{
            finish();
            mAuth.signOut();
        }
    }


}
