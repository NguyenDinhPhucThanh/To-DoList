package com.example.to_dolist;  // Define the package (your app's namespace)

import android.app.AlertDialog;  // Used to show confirmation dialogs
import android.content.DialogInterface;
import android.graphics.Canvas;  // Used for drawing swipe decorations

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;  // Helps with accessing color resources
import androidx.recyclerview.widget.ItemTouchHelper;  // Enables swipe and drag on RecyclerView items
import androidx.recyclerview.widget.RecyclerView;

import com.example.to_dolist.Adapter.ToDoAdapter;  // Import your custom adapter
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;  // Library for swipe visuals

// This class handles swipe actions (edit & delete) for RecyclerView items
public class RecyclerViewTouchHelper extends ItemTouchHelper.SimpleCallback {

    ToDoAdapter adapter;  // Reference to your to-do adapter

    // Constructor accepts the adapter so we can call its methods (delete/edit)
    public RecyclerViewTouchHelper(ToDoAdapter adapter) {
        // 0 = no drag directions, allow swipe left and right
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    // Disable move functionality (we only care about swipe)
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;  // No dragging, only swipe
    }

    // This method gets called when an item is swiped
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getBindingAdapterPosition();  // Get swiped item's position

        // If swiped to the right → show delete confirmation
        if (direction == ItemTouchHelper.RIGHT) {
            // Show AlertDialog asking user to confirm deletion
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
            builder.setTitle("Delete Task");

            // "Yes" button deletes the task from DB and list
            builder.setPositiveButton("Yes", (dialog, which) -> adapter.deleteTask(position));

            // "Cancel" button puts the item back (no deletion)
            builder.setNegativeButton("Cancel", (dialog, which) -> adapter.notifyItemChanged(position));

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            // If swiped to the left → open edit screen for that task
            adapter.editItem(position);
        }
    }

    // This method customizes how the swipe looks (icons and background)
    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState,
                            boolean isCurrentlyActive) {

        // Use the decorator to style left and right swipes
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                // Left swipe (Edit): green background + edit icon
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(adapter.getContext(), R.color.green))
                .addSwipeLeftActionIcon(R.drawable.ic_edit)

                // Right swipe (Delete): red background + trash icon
                .addSwipeRightBackgroundColor(ContextCompat.getColor(adapter.getContext(), R.color.red))
                .addSwipeRightActionIcon(R.drawable.ic_delete)

                .create()  // Create decorator
                .decorate();  // Apply styling to canvas

        // Call super to handle swipe animation and positioning
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
