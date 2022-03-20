package com.nagel.lab5.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.nagel.lab5.R;
import com.nagel.lab5.RecipeAdapter;
import com.nagel.lab5.RecipeViewModel;
import com.nagel.lab5.room.Recipe;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecipeViewModel recipeViewModel;
    public static final int RESULT_SAVE = 100;
    public static final int RESULT_EDIT = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_recipe_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final RecipeAdapter adapter = new RecipeAdapter();
        recyclerView.setAdapter(adapter);

        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        recipeViewModel.getAllRecipes().observe(this, adapter::submitList);

        ((FloatingActionButton) findViewById(R.id.fabNewRecipe)).setOnClickListener(view -> {
            activityResultLauncher.launch(new Intent(MainActivity.this, AddRecipeActivity.class));
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                recipeViewModel.delete(adapter.getRecipePosition(viewHolder. getAbsoluteAdapterPosition()));
                Snackbar.make(findViewById(R.id.myCoordinatorLayoutMain), getString(R.string.one_delete), Snackbar.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnRecipeClickListener(recipe -> {
            Intent intent = new Intent(MainActivity.this, AddRecipeActivity.class);
            intent.putExtra(AddRecipeActivity.EXTRA_TITLE, recipe.getTitle());
            intent.putExtra(AddRecipeActivity.EXTRA_AUTHOR, recipe.getAuthor());
            intent.putExtra(AddRecipeActivity.EXTRA_CONTENT, recipe.getContent());
            intent.putExtra(AddRecipeActivity.EXTRA_TIME, recipe.getTime());
            intent.putExtra(AddRecipeActivity.EXTRA_ID, recipe.getId());
            setResult(RESULT_EDIT, intent);
            activityResultLauncher.launch(intent);
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_SAVE) {
                    Intent resultData = result.getData();
                    if (resultData != null) {
                        String title = resultData.getStringExtra(AddRecipeActivity.EXTRA_TITLE);
                        String author = resultData.getStringExtra(AddRecipeActivity.EXTRA_AUTHOR);
                        int time = resultData.getIntExtra(AddRecipeActivity.EXTRA_TIME, 1);
                        String content = resultData.getStringExtra(AddRecipeActivity.EXTRA_CONTENT);

                        Recipe recipe = new Recipe(title, author, content, time);
                        recipeViewModel.insert(recipe);
                        Snackbar.make(findViewById(R.id.myCoordinatorLayoutMain), getString(R.string.recipe_saved), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(findViewById(R.id.myCoordinatorLayoutMain), getString(R.string.err_save), Snackbar.LENGTH_SHORT).show();
                    }
                } else if (result.getResultCode() == RESULT_EDIT) {
                    Intent resultData = result.getData();
                    if (resultData != null) {
                        int id = resultData.getIntExtra(AddRecipeActivity.EXTRA_ID, -1);
                        if (id == -1) {
                            Snackbar.make(findViewById(R.id.myCoordinatorLayoutMain), getString(R.string.err_up), Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        String title = resultData.getStringExtra(AddRecipeActivity.EXTRA_TITLE);
                        String author = resultData.getStringExtra(AddRecipeActivity.EXTRA_AUTHOR);
                        int time = resultData.getIntExtra(AddRecipeActivity.EXTRA_TIME, 1);
                        String content = resultData.getStringExtra(AddRecipeActivity.EXTRA_CONTENT);

                        Recipe recipe = new Recipe(title, author, content, time);
                        recipe.setId(id);
                        recipeViewModel.update(recipe);
                    } else {
                        Snackbar.make(findViewById(R.id.myCoordinatorLayoutMain), getString(R.string.recipe_updated), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(findViewById(R.id.myCoordinatorLayoutMain), getString(R.string.recipe_closed), Snackbar.LENGTH_SHORT).show();
                }
            });


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_main) {
            recipeViewModel.deleteAllRecipes();
            Snackbar.make(findViewById(R.id.myCoordinatorLayoutMain), getString(R.string.all_delete), Snackbar.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

