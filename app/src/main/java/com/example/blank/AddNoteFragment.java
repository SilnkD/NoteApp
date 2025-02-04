package com.example.blank;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddNoteFragment extends Fragment {
    private EditText editTextTitle;
    private EditText editTextContent;
    private FloatingActionButton buttonSaveNote;
    private long noteId = -1; // По умолчанию, для новой заметки

    public static AddNoteFragment newInstance(long noteId) {
        AddNoteFragment fragment = new AddNoteFragment();
        Bundle args = new Bundle();
        args.putLong("noteId", noteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            noteId = getArguments().getLong("noteId", -1); // Получаем ID заметки для редактирования
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_note, container, false);
        editTextTitle = view.findViewById(R.id.edit_text_title);
        editTextContent = view.findViewById(R.id.edit_text_content);
        buttonSaveNote = view.findViewById(R.id.button_save_note);

        if (noteId != -1) {
            loadNoteDetails(noteId);
        }

        buttonSaveNote.setOnClickListener(v -> saveNote());

        return view;
    }

    private void loadNoteDetails(long noteId) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        Note note = databaseHelper.getNoteById(noteId);
        if (note != null) {
            editTextTitle.setText(note.getHeading());
            editTextContent.setText(note.getDetails());
        }
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString();
        String content = editTextContent.getText().toString();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        if (noteId == -1) {
            long newNoteId = databaseHelper.insertNote(title, content);
            if (newNoteId != -1) {
                String message = String.format("Note \"%s\" saved successfully", newNoteId);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            } else {
                Toast.makeText(getContext(), "Error saving note", Toast.LENGTH_SHORT).show();
            }
        } else {
            boolean updated = databaseHelper.updateNote(noteId, title, content);
            if (updated) {
                Toast.makeText(getContext(), "Note updated successfully", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            } else {
                Toast.makeText(getContext(), "Error updating note", Toast.LENGTH_SHORT).show();
            }
        }
    }
}