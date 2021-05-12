package com.bawp.todoister.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.bawp.todoister.data.TaskDao;
import com.bawp.todoister.model.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class TaskRoomDatabase extends RoomDatabase {
    public static final int NUMBER_OF_THREADS = 4;
    public static final String DATABASE_NAME = "todoister_database";
    private static volatile TaskRoomDatabase INSTANCE;

    //Allows us to run things in the background thread NEVER TOUCH MAIN THREAD
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //this gets called because of the addCallback
    public static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                //Invoke Dao, write, etc.
                TaskDao taskDao = INSTANCE.taskDao();
                taskDao.deleteAll(); //clean slate
            });
        }
    };

    //this runs first. global variable INSTANCE is assigned first
    public static TaskRoomDatabase getDatabase(final Context context) { // Allows us to create an instance of our DB
        if(INSTANCE == null){ //if current instance of the DB is null
            synchronized (TaskRoomDatabase.class){ //Make sure everything goes to the background thread, not main thread.
                if(INSTANCE == null){//Make sure only 1 instance of the DB is created throughout the project
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TaskRoomDatabase.class, DATABASE_NAME)
                            .addCallback(sRoomDatabaseCallback)// add things to DB when it is created for the first time.
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    //This method is used in the repository constructor
    //also needed to be able to do INSTANCE.taskDao();
    //Links the TaskDao class to the DB
    public abstract TaskDao taskDao();

}
