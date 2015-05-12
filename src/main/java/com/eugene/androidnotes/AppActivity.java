package com.eugene.androidnotes;

import android.app.Application;
import android.content.Context;

import com.eugene.androidnotes.Database.LogNotes;
import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;

import java.util.ArrayList;
import java.util.List;

public class AppActivity extends Application {
    private static AppActivity appContext;

    public void onCreate() {
        super.onCreate();
        appContext = this;
        initializeDatabase();
    }

    /**
     * An accessor method to make it easier to access the app context from
     * classes that are not activities
     *
     * @return Context the application context
     */
    public static Context context() {
        return appContext;
    }

    private void initializeDatabase() {
        // setup the database
        List<Class<? extends Model>> models = new ArrayList<>(0);
        models.add(LogNotes.class);
        String dbName = this.getResources().getString(R.string.database_name);
        DatabaseAdapter.setDatabaseName(dbName);
        DatabaseAdapter adapter = new DatabaseAdapter(appContext);
        adapter.setModels(models);
    }

}

