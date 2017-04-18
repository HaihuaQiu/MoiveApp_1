/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.moiveapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for weather data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "movie.db";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MovieEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POPULARITY + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TOPRATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RESULT_IMAGE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_YEAR + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_SCORE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_COLLECT + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VIDEO_PARTONE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_VIDEO_PARTTWO + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_VIDEO_PARTTHREE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_REVIEW_PARTONE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_REVIEW_PARTTWO + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_REVIEW_PARTTHREE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_TIME + " TEXT " +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
