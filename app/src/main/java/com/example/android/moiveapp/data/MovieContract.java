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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.moiveapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH = "movie";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_NAME = "movie_name";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_COLLECT = "collection";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_TOPRATE = "top_rate";
        public static final String COLUMN_RESULT_IMAGE = "movie_image";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_SCORE = "movie_score";
        public static final String COLUMN_OVERVIEW = "movie_overview";
        public static final String COLUMN_VIDEO_PARTONE = "video_part_one";
        public static final String COLUMN_VIDEO_PARTTWO = "video_part_two";
        public static final String COLUMN_VIDEO_PARTTHREE = "video_part_three";
        public static final String COLUMN_REVIEW_PARTONE = "review_part_one";
        public static final String COLUMN_REVIEW_PARTTWO = "review_part_two";
        public static final String COLUMN_REVIEW_PARTTHREE = "review_part_three";
        public static final String COLUMN_TIME = "movie_time";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieWithDetail(String detail) {
            return CONTENT_URI.buildUpon().appendPath(detail).build();
        }

        public static String getDetailFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
