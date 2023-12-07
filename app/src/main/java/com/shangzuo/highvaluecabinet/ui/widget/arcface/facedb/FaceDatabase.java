package com.shangzuo.highvaluecabinet.ui.widget.arcface.facedb;

import android.content.Context;
import android.util.Log;


import com.shangzuo.highvaluecabinet.ui.widget.arcface.facedb.dao.FaceDao;
import com.shangzuo.highvaluecabinet.ui.widget.arcface.facedb.entity.FaceEntity;

import java.io.File;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FaceEntity.class}, version = 3, exportSchema = false)
public abstract class FaceDatabase extends RoomDatabase {
    public abstract FaceDao faceDao();

    private static volatile FaceDatabase faceDatabase = null;

    public static FaceDatabase getInstance(Context context) {
        if (faceDatabase == null) {
            Log.e("FaceDatabase", "getInstance: ");
            synchronized (FaceDatabase.class) {
                if (faceDatabase == null) {
                    Log.e("FaceDatabase", "getInstance: ");
                    faceDatabase = Room.databaseBuilder(context, FaceDatabase.class,
                            context.getExternalFilesDir("database") + File.separator + "faceDB.db").build();
                }
            }
        }
        return faceDatabase;
    }

}
