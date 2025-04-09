package com.example.to_dolist;

import android.app.Activity;  // Required to use getActivity() and cast for callbacks
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.to_dolist.Model.ToDoModel;
import com.example.to_dolist.Utils.DataBaseHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

// This class displays a bottom sheet dialog for adding/editing a task
public class AddNewTask extends BottomSheetDialogFragment {

    // Tag used for identifying the dialog
    public static final String TAG = "AddNewTask";

    // UI components
    private EditText mEditText;
    private Button mSaveButton;

    // Database helper instance
    private DataBaseHelper myDB;

    // Factory method to create a new instance of the fragment
    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    // Inflates the bottom sheet layout (add_new_task.xml)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_task, container, false);
    }

    // Called once the view is created. Used to initialize logic.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Connect UI elements
        mEditText = view.findViewById(R.id.editText);
        mSaveButton = view.findViewById(R.id.addButton);

        // Initialize DB helper
        myDB = new DataBaseHelper(getActivity());

        boolean isUpdate = false; // Flag to determine if we are editing an existing task

        // Check if this dialog was called with arguments (used for editing)
        Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task"); // Get existing task text
            mEditText.setText(task); // Populate EditText

            // If task text is already there, disable save until edited
            if (task.length() > 0) {
                mSaveButton.setEnabled(false);
            }
        }

        // Listen for changes in the EditText field
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            // Enable/disable the save button depending on text input
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    mSaveButton.setEnabled(false);
                    mSaveButton.setBackgroundColor(Color.GRAY); // Optional visual cue
                } else {
                    mSaveButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Need to make final to access inside inner class (Java rule)
        boolean finalIsUpdate = isUpdate;

        // Save button clicked
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEditText.getText().toString();

                if (finalIsUpdate) {
                    // Update task in database
                    myDB.updateTask(bundle.getInt("Id"), text);
                } else {
                    // Create new task and insert to DB
                    ToDoModel item = new ToDoModel();
                    item.setTask(text);
                    item.setStatus(0); // Default status = 0 (not completed)
                    myDB.insertTask(item);
                }

                dismiss(); // Close the dialog
            }
        });
    }

    // Called when the dialog is dismissed
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        // Notify MainActivity to refresh the list
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListener) {
            ((OnDialogCloseListener) activity).onDialogClose(dialog);
        }
    }
}
