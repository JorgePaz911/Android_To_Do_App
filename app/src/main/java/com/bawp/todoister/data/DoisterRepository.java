package com.bawp.todoister.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.bawp.todoister.model.Task;
import com.bawp.todoister.util.TaskRoomDatabase;

import java.util.List;

//Repository is not necessary but is good practice for code organization purposes
public class DoisterRepository {
    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;

    public DoisterRepository(Application application) { //This is where we instantiate the database from so it needs the application context
        TaskRoomDatabase database = TaskRoomDatabase.getDatabase(application); //DB instantiation here
        taskDao = database.taskDao(); //fetch the taskDao
        allTasks = taskDao.getTasks(); //get all tasks from the taskDao. Good to do in the constructor when first calling the repository.
    }

    //all tasks are already in global variable allTasks.
    //obtained from constructor.
    public LiveData<List<Task>> getAllTasks(){
        return allTasks;
    }

    public void insert(Task task){
        // when writing into DB, we want it to be done in background thread. Can bug and lag the system if we dont.
        //this is why we use databaseWriteExecutor.
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> taskDao.insertTask(task));
    }

    public LiveData<Task> get(long id){
        return taskDao.get(id);
    }

    public void update(Task task){
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> taskDao.update(task));
    }

    public void delete(Task task){
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> taskDao.delete(task));
    }

}
