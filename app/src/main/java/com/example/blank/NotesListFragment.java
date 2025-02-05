package com.example.blank;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Collections;
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

        // Добавление MenuProvider для поддержки меню
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_notes_list, menu);
                MenuItem searchItem = menu.findItem(R.id.action_search);
                SearchView searchView = (SearchView) searchItem.getActionView();

                if (searchView != null) {
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            noteAdapter.getFilter().filter(newText);
                            return true;
                        }
                    });
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()== R.id.action_sort_ascending) {
                    noteAdapter.sortByTitleAscending();
                    return true;
                } else if (menuItem.getItemId()== R.id.action_sort_descending) {
                    noteAdapter.sortByTitleDescending();
                    return true;
                } else return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(notes, fromPosition, toPosition);
                noteAdapter.notifyItemMoved(fromPosition, toPosition);
                updateNotePositions();
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note note = notes.get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    // Удаление заметки
                    onDeleteClick(note);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Редактирование заметки
                    onNoteClick(note);
                    noteAdapter.notifyItemChanged(position); // Восстановление элемента после свайпа
                }
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDeleteClick(Note note) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.deleteNoteById(note.getId());
        notes.remove(note);
        noteAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Note deleted", Toast.LENGTH_SHORT).show();
    }

    private void updateNotePositions() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            note.setPosition(i);
            databaseHelper.updateNotePosition(note.getId(), i);
        }
    }
}