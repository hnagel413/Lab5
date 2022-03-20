package com.nagel.lab5;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.nagel.lab5.room.*;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends ListAdapter<Recipe,RecipeAdapter.RecipeHolder> {
    public RecipeAdapter() { super(DIFF_CALLBACK); }
    private static final DiffUtil.ItemCallback<Recipe> DIFF_CALLBACK = new DiffUtil.ItemCallback<Recipe>() {
        @Override
        public boolean areItemsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getAuthor().equals(newItem.getAuthor()) &&
                    oldItem.getTime() == newItem.getTime() &&
                    oldItem.getContent().equals(newItem.getContent());
        }
    };

    private onRecipeClickListener listener;
    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_recipe,parent,false);
        return new RecipeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        Recipe viewableRecipe = getItem(position);
        holder.txtTitle.setText(viewableRecipe.getTitle());
        holder.txtAuthor.setText(viewableRecipe.getAuthor());
        holder.txtTime.setText(String.format("Cooking time: %s minutes", viewableRecipe.getTime()));
        holder.txtContent.setText(viewableRecipe.getContent());
    }

    public Recipe getRecipePosition(int position){ return getItem(position); }

    public interface onRecipeClickListener{
        void onRecipeClick(Recipe recipe);
    }

    public void setOnRecipeClickListener(onRecipeClickListener listener){
        this.listener = listener;
    }

    class RecipeHolder extends RecyclerView.ViewHolder{
        private final TextView txtTitle;
        private final TextView txtAuthor;
        private final TextView txtTime;
        private final TextView txtContent;

        public RecipeHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtContent = itemView.findViewById(R.id.txtContent);

            itemView.setOnClickListener(view -> {
                int position = getAbsoluteAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION){
                    listener.onRecipeClick(getItem(position));
                }
            });
        }
    }
}