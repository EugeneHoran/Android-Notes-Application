package com.eugene.androidnotes.Database;

import android.content.Context;

import com.eugene.androidnotes.AppActivity;
import com.orm.androrm.CharField;
import com.orm.androrm.DateField;
import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class LogNotes extends Model {
    protected CharField note;
    protected DateField date;

    public LogNotes() {
        super(true);
        note = new CharField();
        date = new DateField();
    }


    public String getNote() {
        return note.get();
    }

    public void setNote(String mNote) {
        note.set(mNote);
    }

    public Date getDate() {
        return date.get();
    }

    public void setDate(Date mDate) {
        date.set(mDate);
    }


    public boolean save() {
        int min = 65;
        int max = 2000000;
        Random r = new Random();
        int i1 = r.nextInt(max - min + 1) + min;
        return this.save(context(), i1);
    }

    public boolean edit() {
        return this.save(context());
    }

    public boolean delete() {
        return this.delete(context());
    }

    public static List<LogNotes> orderByDate() {
        return LogNotes.objects().all().orderBy("date").toList();
    }


    /**
     * Returns a list of all LogNotess sorted in descending order of date
     *
     * @return
     */
    public static List<LogNotes> all() {

        return LogNotes.objects().all().orderBy("-date").toList();
    }

    /**
     * Builds up query for LogNotess database
     *
     * @return
     */
    public static QuerySet<LogNotes> objects() {

        return LogNotes.objects(context(), LogNotes.class);
    }

    /**
     * Get application context
     *
     * @return
     */
    private static Context context() {

        return AppActivity.context();
    }
}
