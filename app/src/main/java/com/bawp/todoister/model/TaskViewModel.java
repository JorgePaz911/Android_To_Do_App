package com.bawp.todoister.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bawp.todoister.data.DoisterRepository;

import java.util.List;

//This class is necessary. It notices when data changes happen through LiveData variables. Must use observer mechanism
//When new data shows up that is different from the initial call we made to the constructor, this notifies the UI and
//the UI knows it must update itself.
public class TaskViewModel extends AndroidViewModel {
    public static DoisterRepository repository;
    public final LiveData<List<Task>> allTasks;

    //this does the exact same thing as the repository
    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new DoisterRepository(application);
        allTasks = repository.getAllTasks();
    }

    //same as repository
    //WHY IS THIS NOT STATIC VS OTHER METHODS?
    public LiveData<List<Task>> getAllTasks(){
        return allTasks;
    }

    public static void insert(Task task){
        repository.insert(task);
    }

    public LiveData<Task> get(long id){
        return repository.get(id);
    }

    public static void update(Task task){
        repository.update(task);
    }

    public static void delete(Task task){
        repository.delete(task);
    }


}
