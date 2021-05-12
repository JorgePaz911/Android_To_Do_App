package com.bawp.todoister;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bawp.todoister.model.Priority;
import com.bawp.todoister.model.SharedViewModel;
import com.bawp.todoister.model.Task;
import com.bawp.todoister.model.TaskViewModel;
import com.bawp.todoister.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Calendar;
import java.util.Date;

//fragment class. piece of screen can put plugged into any part of the app6
public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener{

    private EditText enterTodo;
    private ImageButton calendarButton;
    private ImageButton priorityButton;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectedRadioButton;
    private int selectedButtonId;
    private ImageButton saveButton;
    private CalendarView calendarView;
    private Group calendarGroup;
    private Date dueDate;
    Calendar calendar = Calendar.getInstance();
    private SharedViewModel sharedViewModel;
    private boolean isEdit;
    private Priority priority;


    public BottomSheetFragment(){//Must have empty public constructor
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        calendarGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        calendarButton = view.findViewById(R.id.today_calendar_button);
        enterTodo = view.findViewById(R.id.enter_todo_et);
        saveButton = view.findViewById(R.id.save_todo_button);
        priorityButton = view.findViewById(R.id.priority_todo_button);
        priorityRadioGroup = view.findViewById(R.id.radioGroup_priority);

        //these chips will only be used here so they don't need to be global.
        Chip todayChip = view.findViewById(R.id.today_chip);
        Chip tomorrowChip = view.findViewById(R.id.tomorrow_chip);
        Chip nextWeekChip = view.findViewById(R.id.next_week_chip);

        todayChip.setOnClickListener(this);
        tomorrowChip.setOnClickListener(this);
        nextWeekChip.setOnClickListener(this);



        return view;
    }

    @Override
    public void onResume() { //called when the fragment begins interacting with the user.
        super.onResume();
        if(sharedViewModel.getSelectedItem().getValue() != null){ //This is done to fill the fragment with the current information that was clicked.
            isEdit = sharedViewModel.getIsEdit();
            Task task = sharedViewModel.getSelectedItem().getValue();
            enterTodo.setText(task.getTask());
        }
    }

    // the moment that a view is created and everything gets set up. Then this method is called.
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarGroup.setVisibility(calendarGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                Utils.hideSoftKeyboard(v);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.clear();
                calendar.set(year, month, dayOfMonth);
                dueDate = calendar.getTime();
            }
        });

        priorityButton.setOnClickListener(v -> {
            Utils.hideSoftKeyboard(v);
            priorityRadioGroup.setVisibility(priorityRadioGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            priorityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    if(priorityRadioGroup.getVisibility() == View.VISIBLE){
                        selectedButtonId = checkedId;
                        selectedRadioButton = view.findViewById(selectedButtonId);
                        if(selectedRadioButton.getId() == R.id.radioButton_high){
                            priority = Priority.HIGH;
                        }else if(selectedRadioButton.getId() == R.id.radioButton_med){
                            priority = Priority.MEDIUM;
                        }else if(selectedRadioButton.getId() == R.id.radioButton_low){
                            priority = Priority.LOW;
                        }else{
                            priority = Priority.LOW;
                        }
                    }else{
                        priority = Priority.LOW;
                    }
                }
            });
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = enterTodo.getText().toString().trim();
                if(!TextUtils.isEmpty(task) && dueDate != null && priority != null) {//if string is not empty
                    Task myTask = new Task(task, priority, dueDate, Calendar.getInstance().getTime(), false);
                    if(isEdit){
                        Task updateTask = sharedViewModel.getSelectedItem().getValue();
                        updateTask.setTask(task);
                        updateTask.setDateCreated(Calendar.getInstance().getTime());
                        updateTask.setPriority(priority);
                        updateTask.setDueDate(dueDate);
                        TaskViewModel.update(updateTask);
                        sharedViewModel.setIsEdit(false);
                    }else TaskViewModel.insert(myTask);
                    if(BottomSheetFragment.this.isVisible()){
                        BottomSheetFragment.this.dismiss();
                    }
                }else{
                    Snackbar.make(saveButton, R.string.empty_field, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        calendar = Calendar.getInstance();
        if(id == R.id.today_chip){
            //set date for today
            calendar.add(Calendar.DAY_OF_YEAR, 0);
            Log.d("CALENDAR", "onClick1: " + calendar.getTime());
            dueDate = calendar.getTime();
        }else if(id == R.id.tomorrow_chip){
            //set date for tomorrow
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Log.d("CALENDAR", "onClick2: " + calendar.getTime());
            dueDate = calendar.getTime();
        }else if(id == R.id.next_week_chip){
            //set date for next week
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            Log.d("CALENDAR", "onClick3: " + calendar.getTime());
            dueDate = calendar.getTime();
        }
    }
}