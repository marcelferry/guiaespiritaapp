package br.com.mythe.droid.common.dao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.com.mythe.droid.common.modelo.EntityInterface;

public abstract class InterfaceDAO<T extends EntityInterface, ID extends Serializable> {

        protected abstract SQLiteDatabase getDataBase();

        protected abstract Context getContext();

        protected abstract String getTableName();

        protected abstract String[] getColumNames();

        protected abstract String getColumnOrderDefault();

        protected abstract T getFromCursor(Cursor cursor);

        protected abstract void populateContentValues(ContentValues cv, T t);

        public long insert(T vo) {
                SQLiteDatabase db = null;
                try {
                        db = getDataBase();
                        ContentValues cv = new ContentValues();
                        populateContentValues(cv, vo);
                        return db.insert(getTableName(), null, cv);
                } finally {
                        if (db != null && db.isOpen()) {
                                db.close();
                        }
                }
        }

        public long delete(T vo) {
                SQLiteDatabase db = null;
                try {
                        db = getDataBase();
                        return db.delete(getTableName(), "id=?", new String[] { vo.getId()
                                        .toString() });
                } finally {
                        if (db != null && db.isOpen()) {
                                db.close();
                        }
                }
        }

        public long update(T vo) {
                SQLiteDatabase db = getDataBase();
                try {
                        ContentValues cv = new ContentValues();
                        populateContentValues(cv, vo);
                        return db.update(getTableName(), cv, "id=?", new String[] { vo
                                        .getId().toString() });
                } finally {
                        if (db != null && db.isOpen()) {
                                db.close();
                        }
                }
        }

        public T getByID(ID id) {
                SQLiteDatabase db = getDataBase();
                Cursor cursor = null;
                try {
                        cursor = db.query(getTableName(), getColumNames(), "id=?",
                                        new String[] { id.toString() }, null, null, null);
                        T toReturn = null;
                        if (cursor.moveToFirst()) {
                                toReturn = getFromCursor(cursor);

                        }
                        return toReturn;
                } finally {
                        if (cursor != null && !cursor.isClosed()) {
                                cursor.close();
                        }
                        if (db != null && db.isOpen()) {
                                db.close();
                        }
                }
        }

        public List<T> getAll() {
                Cursor cursor = null;
                SQLiteDatabase db = getDataBase();
                try {
                        cursor = db.rawQuery("Select * From " + getTableName()
                                        + " Order by " + getColumnOrderDefault(), null);
                        List<T> toReturn = new ArrayList<T>();
                        while (cursor.moveToNext()) {
                                T toInsert = getFromCursor(cursor);
                                toReturn.add(toInsert);
                        }
                        return toReturn;
                } finally {
                        if (cursor != null && !cursor.isClosed()) {
                                cursor.close();
                        }
                        if (db != null && db.isOpen()) {
                                db.close();
                        }
                }
        }
}