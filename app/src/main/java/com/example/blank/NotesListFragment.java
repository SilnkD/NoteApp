package com.example.blank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class NotesListFragment extends Fragment implements NoteAdapter.OnNoteClickListener {

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> notes;
    private FloatingActionButton fabAddNote;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fabAddNote = requireActivity().findViewById(R.id.button_add_note);

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        notes = databaseHelper.getAllNotes();

        if (notes.isEmpty()) {
            TextView textViewEmpty = view.findViewById(R.id.textViewEmpty);
            textViewEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noteAdapter = new NoteAdapter(notes, this);
            recyclerView.setAdapter(noteAdapter);
        }

        return view;
    }

    private void openAddNoteFragment(long noteId) {
        AddNoteFragment addNoteFragment = AddNoteFragment.newInstance(noteId);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, addNoteFragment)
                .addToBackStack(null)
                .commit();

        // Скрыть кнопку при открытии фрагмента
        fabAddNote.setVisibility(View.GONE);
    }

    @Override
    public void onNoteClick(Note note) {
        // Открытие фрагмента для редактирования заметки
        openAddNoteFragment(note.getId());
    }

    @Override
    public void onDeleteClick(Note note) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.deleteNoteById(note.getId());
        notes.remove(note);
        noteAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Note deleted", Toast.LENGTH_SHORT).show();
    }
}