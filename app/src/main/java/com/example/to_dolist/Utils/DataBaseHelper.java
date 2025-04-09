// Package declaration for organization
package com.example.to_dolist.Utils;

// Import necessary Android and Java database classes
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.to_dolist.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

// This class helps manage your local SQLite database for storing tasks
public class DataBaseHelper extends SQLiteOpenHelper {

    // Constants for database and table details
    private static final String DATABASE_NAME = "TODO_DATABASE";   // Database name
    private static final String TABLE_NAME = "TODO_TABLE";         // Table name
    private static final String COL_1 = "ID";                      // Column for task ID
    private static final String COL_2 = "TASK";                    // Column for task text
    private static final String COL_3 = "STATUS";                  // Column for task status (0 = not done, 1 = done)

    // Constructor – calls super class constructor with DB name and version
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    // Called when the DB is first created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create the table with columns ID, TASK, and STATUS
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TASK TEXT, STATUS INTEGER)");
    }

    // Called when DB needs to be upgraded (e.g., version change)
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Delete the old table and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a new task into the database
    public void insertTask(ToDoModel model) {
        SQLiteDatabase db = this.getWritableDatabase();  // Open database for writing
        ContentValues contentValues = new ContentValues(); // Key-value pairs for inserting

        contentValues.put(COL_2, model.getTask());  // Add task description
        contentValues.put(COL_3, 0);                 // Default status = 0 (not done)

        db.insert(TABLE_NAME, null, contentValues);  // Insert into database
    }

    // Update a task's description
    public void updateTask(int id, String task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_2, task); // Update only the TASK column

        db.update(TABLE_NAME, contentValues, "ID=?", new String[]{String.valueOf(id)});
    }

    // Update a task's status (done or not)
    public void updateStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_3, status); // 0 = not done, 1 = done

        db.update(TABLE_NAME, contentValues, "ID=?", new String[]{String.valueOf(id)});
    }

    // Delete a task from the database by its ID
    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "ID=?", new String[]{String.valueOf(id)});
    }

    // Retrieve all tasks from the database
    public List<ToDoModel> getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase(); // Open DB for reading
        Cursor cursor = null; // Cursor to loop through DB results
        List<ToDoModel> modelList = new ArrayList<>(); // List to hold task objects

        db.beginTransaction(); // Start transaction for safe reading
        try {
            // Query the table and get all rows
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

            // Check if we have results and start from the first row
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Create a new ToDoModel object
                    ToDoModel toDoModel = new ToDoModel();

                    // Get column indexes
                    int idIndex = cursor.getColumnIndex(COL_1);
                    int taskIndex = cursor.getColumnIndex(COL_2);
                    int statusIndex = cursor.getColumnIndex(COL_3);

                    // Only proceed if all column indexes are valid
                    if (idIndex >= 0 && taskIndex >= 0 && statusIndex >= 0) {
                        // Set the values into the model
                        toDoModel.setId(cursor.getInt(idIndex));
                        toDoModel.setTask(cursor.getString(taskIndex));
                        toDoModel.setStatus(cursor.getInt(statusIndex));

                        // Add the task to the list
                        modelList.add(toDoModel);
                    }

                } while (cursor.moveToNext()); // Move to next row
            }
        } finally {
            db.endTransaction();  // End the transaction
            if (cursor != null) cursor.close(); // Close the cursor to release resources
        }

        // Return the list of tasks
        return modelList;
    }
}
