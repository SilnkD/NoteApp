package com.example.blank;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> implements Filterable {

    private List<Note> notes;
    private List<Note> notesFiltered;
    private OnNoteClickListener noteClickListener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
        void onDeleteClick(Note note);
    }

    public NoteAdapter(List<Note> notes, OnNoteClickListener listener) {
        this.notes = notes;
        this.notesFiltered = new ArrayList<>(notes);
        this.noteClickListener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notesFiltered.get(position);
        holder.titleTextView.setText(note.getHeading());
        holder.contentTextView.setText(note.getDetails());

        holder.itemView.setOnClickListener(v -> noteClickListener.onNoteClick(note));
        holder.deleteImageView.setOnClickListener(v -> noteClickListener.onDeleteClick(note));
    }

    @Override
    public int getItemCount() {
        return notesFiltered.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        TextView positionTextView; // добавим TextView для отображения позиции (если необходимо)
        ImageView deleteImageView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            contentTextView = itemView.findViewById(R.id.textViewContent);
            deleteImageView = itemView.findViewById(R.id.imageViewMenu);
        }
    }

    // Метод для обновления списка заметок и сохранения их позиций
    @SuppressLint("NotifyDataSetChanged")
    public void updateNotes(List<Note> updatedNotes) {
        this.notes = updatedNotes;
        Collections.sort(this.notes, Comparator.comparingInt(n -> (int) n.getPosition()));
        notifyDataSetChanged();
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    notesFiltered = new ArrayList<>(notes);
                } else {
                    List<Note> filteredList = new ArrayList<>();
                    for (Note row : notes) {
                        if (row.getHeading().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getDetails().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    notesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = notesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notesFiltered = (ArrayList<Note>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
