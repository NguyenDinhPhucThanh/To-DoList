// Define the app's package name
package com.example.to_dolist;

// Import required Android and Java libraries
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.to_dolist.Adapter.ToDoAdapter;
import com.example.to_dolist.Model.ToDoModel;
import com.example.to_dolist.Utils.DataBaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Main activity of the To-Do app; implements OnDialogCloseListener to refresh the list when dialog closes
public class MainActivity extends AppCompatActivity implements OnDialogCloseListener {

    // Declare UI components and data structures
    private RecyclerView recyclerView;             // Displays the list of tasks
    private FloatingActionButton addButton;        // Button to add a new task
    private DataBaseHelper myDB;                   // Helper class to manage SQLite database operations
    private List<ToDoModel> mList;                 // List to hold all tasks
    private ToDoAdapter adapter;                   // Custom RecyclerView adapter

    // This method is called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enables edge-to-edge display (for modern UI look)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // Set the layout file for this screen

        // Applies padding to avoid overlapping system UI (like status bar or navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Link Java variables to layout views
        recyclerView = findViewById(R.id.recyclerView);
        addButton = findViewById(R.id.addButton);
        myDB = new DataBaseHelper(MainActivity.this); // Initialize database helper
        mList = new ArrayList<>();                    // Initialize empty task list
        adapter = new ToDoAdapter(myDB, MainActivity.this); // Create adapter and pass the DB + context

        // Set up the RecyclerView
        recyclerView.setHasFixedSize(true); // Improves performance when item sizes are fixed
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Show items in a vertical list
        recyclerView.setAdapter(adapter);   // Connect the adapter to the RecyclerView

        // Fetch all tasks from the database
        mList = myDB.getAllTasks();

        // Reverse the list so the newest task appears on top
        Collections.reverse(mList);

        // Pass the list to the adapter to show on screen
        adapter.setTasks(mList);

        // When the user clicks the add button, open the AddNewTask fragment
        addButton.setOnClickListener(view -> {
            AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
        });

        // Enable swipe-to-delete and other touch gestures
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerViewTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // This method is called when the AddNewTask dialog is closed
    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        // Refresh the task list after adding or editing
        mList = myDB.getAllTasks();       // Re-fetch all tasks from database
        Collections.reverse(mList);       // Reverse to keep latest on top
        adapter.setTasks(mList);          // Update adapter's task list
        adapter.notifyDataSetChanged();   // Refresh the RecyclerView
    }
}
