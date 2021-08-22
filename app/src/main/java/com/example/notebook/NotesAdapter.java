package com.example.notebook;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    public final int ACTION_CHANGE = 0;
    public final int ACTION_DELETE = 1;
    private List<NoteEntity> data = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setData(List<NoteEntity> notes) {
        data = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(parent, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    interface OnItemClickListener {
        void onItemClick(int position, int action);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView description;
        private final CardView cardView;
        private NoteEntity noteEntity;

        public NoteViewHolder(@NonNull ViewGroup parent, @Nullable NotesAdapter.OnItemClickListener clickListener) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note, parent, false));
            cardView = (CardView) itemView;
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }

        public void bind(NoteEntity noteEntity) {
            this.noteEntity = noteEntity;
            title.setText(noteEntity.getTitle());
            description.setText(noteEntity.getDescription());

            itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                menu.setHeaderTitle(v.getResources().getString(R.string.context_menu_header) + noteEntity.getTitle());
                menu.add(v.getResources().getString(R.string.change_note)).setOnMenuItemClickListener(item -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition(), ACTION_CHANGE);
                    }
                    return true;
                });
                menu.add(v.getResources().getString(R.string.delete_note)).setOnMenuItemClickListener(item -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition(), ACTION_DELETE);
                    }
                    return true;
                });
            });
            itemView.setOnClickListener(v -> {
                itemView.showContextMenu();
            });
        }
    }
}
