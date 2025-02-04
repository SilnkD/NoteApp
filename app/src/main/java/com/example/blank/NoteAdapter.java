package com.example.blank;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;
    private OnNoteClickListener noteClickListener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
        void onDeleteClick(Note note);
    }

    public NoteAdapter(List<Note> notes, OnNoteClickListener listener) {
        this.notes = notes;
        this.noteClickListener = listener;
        this.notes.sort(Comparator.comparingInt(n -> (int) n.getPosition()));
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.titleTextView.setText(note.getHeading());
        holder.contentTextView.setText(note.getDetails());
        //holder.positionTextView.setText(String.valueOf(note.getPosition())); // отобразим позицию заметки (если необходимо)

        holder.itemView.setOnClickListener(v -> noteClickListener.onNoteClick(note));
        holder.deleteImageView.setOnClickListener(v -> noteClickListener.onDeleteClick(note));
    }

    @Override
    public int getItemCount() {
        return notes.size();
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
            //positionTextView = itemView.findViewById(R.id.textViewPosition);
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
}