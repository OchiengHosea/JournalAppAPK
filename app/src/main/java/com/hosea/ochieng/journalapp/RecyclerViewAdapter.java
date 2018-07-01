package com.hosea.ochieng.journalapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hosea.ochieng.journalapp.DataUtils.JournalDateUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.JournalViewHolder>{
    @NonNull
    ArrayList<Journal> allJournals = new ArrayList<>();
    Context context;

    public RecyclerViewAdapter(@NonNull ArrayList<Journal> allJournals, Context context) {
        this.allJournals = allJournals;
        this.context = context;
    }

    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_item, parent, false);
        JournalViewHolder journalViewHolder = new JournalViewHolder(view);
        return journalViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        final Journal journal = allJournals.get(position);
        holder.itemSubjectText.setText(journal.getSubject());
        holder.itemDueDateText.setText(context.getString(R.string.due_date_prepend_text) +
                JournalDateUtils.getReadableDate(journal.getDueDate()));
        holder.itemEntryDateText.setText(context.getString(R.string.created_date_prepend_label)+
                JournalDateUtils.getReadableDate(journal.getEntryDate()));
        holder.itemDescriptionText.setText(JournalDateUtils.getFirst100Words(journal.getDescription()));
        if(journal.getCompleted()){
            holder.itemClearedMark.setBackgroundColor(context.getResources().getColor(R.color.journalCompleted));
        }else{
            holder.itemClearedMark.setBackgroundColor(context.getResources().getColor(R.color.journalNotCompleted));
        }

        holder.parentFrameLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startJournalDetailActivity(journal);
            }
        });
    }

    public void setCompletedOrNotCompleted(){

    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void startJournalDetailActivity(Journal journal) {
        Intent intent = new Intent(context, JournalDetail.class);
        intent.putExtra("selectedJournal", journal);
        context.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return allJournals.size();
    }

    public class JournalViewHolder extends RecyclerView.ViewHolder{
        TextView itemSubjectText;
        TextView itemEntryDateText;
        TextView itemDueDateText;
        TextView itemDescriptionText;
        ImageView itemClearedMark;
        CardView parentFrameLayout;


        public JournalViewHolder(View itemView) {
            super(itemView);
            itemSubjectText = itemView.findViewById(R.id.item_subject_text_view);
            itemEntryDateText = itemView.findViewById(R.id.item_date_created_text_view);
            itemDueDateText = itemView.findViewById(R.id.item_date_due_text_view);
            itemDescriptionText = itemView.findViewById(R.id.item_content_textView);
            itemClearedMark = itemView.findViewById(R.id.item_cleared_indicator);
            parentFrameLayout = itemView.findViewById(R.id.item_parent_layout);
        }
    }
}
