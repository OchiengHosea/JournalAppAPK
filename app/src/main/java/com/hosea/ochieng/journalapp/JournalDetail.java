package com.hosea.ochieng.journalapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class JournalDetail extends AppCompatActivity implements View.OnClickListener {//Firebase
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Journal selectedJournal = null;
    private FirebaseAuth.AuthStateListener mAuthListener;


    //widgets
    private Button cancelBtn;
    private Button editBtn;
    private TextView subject;
    private TextView entryDate;
    private TextView dueDAte;
    private ImageView completed;
    private TextView description;
    private TextView status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_detail);

        editBtn = findViewById(R.id.detail_edit_btn);
        subject = findViewById(R.id.detail_entry_subject_tv);
        entryDate = findViewById(R.id.detail_entry_date);
        dueDAte = findViewById(R.id.detail_due_date);
        completed = findViewById(R.id.detail_completedCheck);
        description = findViewById(R.id.content_desc);
        cancelBtn = findViewById(R.id.detail_cancel_button);
        status = findViewById(R.id.status_label);

        Intent intent = getIntent();
        if(intent.hasExtra("selectedJournal")){
            selectedJournal = (Journal)intent.getSerializableExtra("selectedJournal");
            subject.setText(selectedJournal.getSubject());
            entryDate.setText(selectedJournal.getEntryDate().toString());
            dueDAte.setText(selectedJournal.getDueDate().toString());
            if(selectedJournal.getCompleted()){
                completed.setBackgroundColor(getResources().getColor(R.color.journalCompleted));
                status.setText("Completed");
            }else{
                completed.setBackgroundColor(getResources().getColor(R.color.journalNotCompleted));
                status.setText("Not Completed");
            }
            description.setText(selectedJournal.getDescription());

            editBtn.setOnClickListener(this);
            cancelBtn.setOnClickListener(this);

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    updateUi(firebaseAuth.getCurrentUser());
                }
            };

            databaseReference = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
        }else{
            showToast("Error retrieving the entry");
            cancelViewingEntry();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.journal_delail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_logout){
            mAuth.signOut();
            return true;
        }else if(id == R.id.action_edit_entry){
            startEditActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detail_edit_btn:
                startEditActivity();
                break;

            case R.id.detail_cancel_button:
                cancelViewingEntry();
        }
    }

    private void cancelViewingEntry() {
        finish();
    }

    private void startEditActivity() {
        Intent entryRditIntent = new Intent(this, EditSelectedEntry.class);
        entryRditIntent.putExtra("selectedJournal", selectedJournal);
        startActivity(entryRditIntent);

    }

    private void updateUi(FirebaseUser user){
        if(user != null){
            showToast("Viewing a jornal...");
        }else{
            cancelViewingEntry();
        }
    }
}
