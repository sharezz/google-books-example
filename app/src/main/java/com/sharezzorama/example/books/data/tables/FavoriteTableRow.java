package com.sharezzorama.example.books.data.tables;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class FavoriteTableRow extends BaseTable {
    public static final String TABLE_NAME = "favorites";
    public static final String AUTHORS_DELIMITER = ",";

    public long _id;
    public String remoteId;
    public String title;
    public String description;
    public String[] authors;
    public String preview;
    public String thumbnail;
    public String thumbnailSmall;

    public static FavoriteTableRow getEntity(Cursor cursor) {
        if (cursor == null || cursor.isAfterLast() || cursor.isBeforeFirst() || cursor.isClosed()) {
            return null;
        }

        FavoriteTableRow entity = new FavoriteTableRow();
        entity._id = cursor.getLong(cursor.getColumnIndex(Columns.ID));
        entity.remoteId = cursor.getString(cursor.getColumnIndex(Columns.REMOTE_ID));
        entity.title = cursor.getString(cursor.getColumnIndex(Columns.TITLE));
        entity.description = cursor.getString(cursor.getColumnIndex(Columns.DESCRIPTION));
        String authorsString = cursor.getString(cursor.getColumnIndex(Columns.AUTHORS));
        entity.authors = !TextUtils.isEmpty(authorsString) ? authorsString.split(AUTHORS_DELIMITER) : null;
        entity.preview = cursor.getString(cursor.getColumnIndex(Columns.PREVIEW));
        entity.thumbnail = cursor.getString(cursor.getColumnIndex(Columns.THUMBNAIL));
        entity.thumbnailSmall = cursor.getString(cursor.getColumnIndex(Columns.THUMBNAIL_SMALL));
        return entity;
    }

    @Override
    public void createTable(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s (%s integer primary key autoincrement, " +
                        "%s TEXT, " +
                        "%s TEXT, " +
                        "%s TEXT, " +
                        "%s TEXT, " +
                        "%s TEXT, " +
                        "%s TEXT, " +
                        "%s TEXT" +
                        ")",
                TABLE_NAME,
                Columns.ID,
                Columns.REMOTE_ID,
                Columns.TITLE,
                Columns.DESCRIPTION,
                Columns.AUTHORS,
                Columns.PREVIEW,
                Columns.THUMBNAIL,
                Columns.THUMBNAIL_SMALL));
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public long insert(@NonNull SQLiteDatabase db) {
        final ContentValues values = new ContentValues();
        values.put(Columns.REMOTE_ID, remoteId);
        values.put(Columns.TITLE, title);
        values.put(Columns.DESCRIPTION, description);
        values.put(Columns.AUTHORS, authors != null ? TextUtils.join(AUTHORS_DELIMITER, authors) : "");
        values.put(Columns.PREVIEW, preview);
        values.put(Columns.THUMBNAIL, thumbnail);
        values.put(Columns.THUMBNAIL_SMALL, thumbnailSmall);
        return db.insert(getTableName(), null, values);
    }

    public static boolean delete(@NonNull SQLiteDatabase db, String remoteId) {
        int deletedRowsCount = db.delete(TABLE_NAME, Columns.REMOTE_ID + " = ?", new String[]{remoteId});
        return deletedRowsCount > 0;
    }

    public static boolean isFavorite(@NonNull SQLiteDatabase db, String remoteId) {
        final Cursor cursor = db.query(TABLE_NAME,
                new String[]{"count(*)"},
                Columns.REMOTE_ID + " = ?",
                new String[]{String.valueOf(remoteId)},
                null,
                null,
                null);

        int count = 0;
        try {
            if (cursor != null && cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count > 0;
    }

    public static List<FavoriteTableRow> getAll(@NonNull SQLiteDatabase db) {
        List<FavoriteTableRow> result = new ArrayList<>();
        final Cursor cursor = db.query(TABLE_NAME, null, null,
                null, null, null, Columns.ID);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    result.add(getEntity(cursor));
                }
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    public interface Columns {
        String ID = "_id";
        String REMOTE_ID = "remote_id";
        String TITLE = "title";
        String DESCRIPTION = "description";
        String AUTHORS = "authors";
        String PREVIEW = "preview";
        String THUMBNAIL = "thumbnail";
        String THUMBNAIL_SMALL = "thumbnail_small";
    }
}
