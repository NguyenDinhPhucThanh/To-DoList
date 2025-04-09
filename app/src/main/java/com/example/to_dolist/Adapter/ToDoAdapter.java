package com.example.to_dolist.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.to_dolist.AddNewTask;
import com.example.to_dolist.MainActivity;
import com.example.to_dolist.Model.ToDoModel;
import com.example.to_dolist.R;
import com.example.to_dolist.Utils.DataBaseHelper;

import java.util.List;

// This adapter class handles how data is shown in the RecyclerView
public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    // List of tasks (model objects) that will be displayed
    private List<ToDoModel> mList;

    // Reference to MainActivity to access context and fragment manager
    private MainActivity activity;

    // Reference to the database helper for performing updates
    private DataBaseHelper myDB;

    // Constructor that initializes the adapter with a DB instance and MainActivity
    public ToDoAdapter(DataBaseHelper myDB, MainActivity activity){
        this.activity = activity;
        this.myDB = myDB;
    }

    // Called when RecyclerView needs a new view holder
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the list (task_layout.xml)
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new MyViewHolder(v);
    }

    // Binds the data to the view (CheckBox) at the specified position
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ToDoModel item = mList.get(position); // Get current task

        holder.checkBox.setText(item.getTask()); // Set text
        holder.checkBox.setChecked(toBoolean(item.getStatus())); // Set checkbox based on status

        // Listen for when user checks/unchecks the checkbox
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    myDB.updateStatus(item.getId(), 1); // Checked → Completed
                } else {
                    myDB.updateStatus(item.getId(), 0); // Unchecked → Incomplete
                }
            }
        });
    }

    // Helper method to convert integer status (0 or 1) to boolean
    public boolean toBoolean(int num){
        return num != 0;
    }

    // Returns context of the adapter (via activity)
    public Context getContext(){
        return activity;
    }

    // Returns total number of items in the list
    @Override
    public int getItemCount() {
        return mList.size();
    }

    // This method updates the list of tasks and notifies the adapter to refresh the view
    public void setTasks(List<ToDoModel> mList){
        this.mList = mList;
        notifyDataSetChanged(); // Refreshes the entire list
    }

    // Deletes a task from both the database and the list
    public void deleteTask(int position){
        ToDoModel item = mList.get(position);
        myDB.deleteTask(item.getId()); // Remove from DB
        mList.remove(position); // Remove from list
        notifyItemRemoved(position); // Notify adapter
    }

    // Edits a task – opens the AddNewTask fragment with existing task data
    public void editItem(int position){
        ToDoModel item = mList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("Id", item.getId());
        bundle.putString("task", item.getTask());

        AddNewTask task = new AddNewTask();
        task.setArguments(bundle);
        task.show(activity.getSupportFragmentManager(), task.getTag());
    }

    // ViewHolder class holds the layout views for each item
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox); // Link to CheckBox in task_layout.xml
        }
    }
}
