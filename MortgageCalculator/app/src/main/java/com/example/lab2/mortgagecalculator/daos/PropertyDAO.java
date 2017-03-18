package com.example.lab2.mortgagecalculator.daos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dthesiya on 3/17/17.
 */

public class PropertyDAO extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "mortgagecalculator";

    private static final String TABLE_PROPERTIES = "properties";

    private static final String KEY_ID = "id";

    private static final String PROPERTY = "property";

    private static final String PROPERTY_TYPE = "type";
    private static final String ADDR = "address";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String ZIPCODE = "zipcode";

    private static final String LOAN_AMT = "loan_amt";
    private static final String DOWN_PMT = "down_pmt";
    private static final String APR = "apr";
    private static final String TERM_YRS = "term_yrs";
    private static final String RESULT = "result";

    private static final String DELETE_STMT = "DELETE FROM "+ TABLE_PROPERTIES + " WHERE " + KEY_ID + " = ?";
    private static final String INSERT_STMT = "INSERT INTO " + TABLE_PROPERTIES + " (" + PROPERTY + ") VALUES(?)";
    private static final String UPDATE_STMT = "UPDATE " + TABLE_PROPERTIES + " SET " + PROPERTY + " = ? " +
            "WHERE " + KEY_ID + " = ?";

    public long insertProperty(Property property){
        byte[] arr = serializeProperty(property);
        if(arr != null){
            SQLiteDatabase db = getWritableDatabase();
            SQLiteStatement insertStmt = db.compileStatement(INSERT_STMT);
            insertStmt.bindBlob(1, arr);
            return insertStmt.executeInsert();
        }
        return -1;
    }

    public boolean updateProperty(Property property){
        byte[] arr = serializeProperty(property);
        if(arr != null){
            SQLiteDatabase db = getWritableDatabase();
            SQLiteStatement updateStmt = db.compileStatement(UPDATE_STMT);
            updateStmt.bindBlob(1, arr);
            updateStmt.bindLong(2, property.getId());
            return updateStmt.executeUpdateDelete() > 0;
        }
        return false;
    }

    public boolean deleteProperty(int id){
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement deleteStmt = db.compileStatement(DELETE_STMT);
        deleteStmt.bindLong(1, id);
        return deleteStmt.executeUpdateDelete() > 0;
    }

    public List<Property> getProperties(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { KEY_ID, PROPERTY };

        // Filter results WHERE "title" = 'My Title'
        String selection = "";
        String[] selectionArgs = {};

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                KEY_ID + " ASC";

        Cursor cursor = db.query(
                TABLE_PROPERTIES,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        List<Property> properties = new ArrayList<Property>();
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID));
            byte[] arr = cursor.getBlob(cursor.getColumnIndexOrThrow(PROPERTY));
            Property property = deserializeProperty(arr);
            property.setId(id);
            if(property!=null) {
                properties.add(property);
            }
        }
        cursor.close();
        return properties;
    }

    public PropertyDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String CREATE_PROPERTIES_TABLE = "CREATE TABLE " + TABLE_PROPERTIES
//                + "("
//                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + PROPERTY_TYPE + " TEXT,"
//                + ADDR + " TEXT"
//                + CITY + " TEXT"
//                + STATE + " TEXT"
//                + ZIPCODE + " TEXT"
//                + LOAN_AMT + " REAL"
//                + DOWN_PMT + " REAL"
//                + APR + " REAL"
//                + TERM_YRS + " INTEGER"
//                + RESULT + " REAL"
//                + ")";
        String CREATE_PROPERTIES_TABLE = "CREATE TABLE " + TABLE_PROPERTIES
                + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PROPERTY + " BLOB"
                + ")";

        db.execSQL(CREATE_PROPERTIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTIES);
        onCreate(db);
    }

    public byte[] serializeProperty(Property obj){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] result = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            result = bos.toByteArray();
        } catch(IOException ex){
            ex.printStackTrace();
        } finally{
            try {
                bos.close();
            } catch (IOException ex) {
            }
        }
        return result;
    }

    public Property deserializeProperty(byte[] bytes){
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        Property obj = null;
        try {
            in = new ObjectInputStream(bis);
            obj = (Property) in.readObject();
        } catch(ClassNotFoundException ex){
            ex.printStackTrace();
        } catch(IOException ex){
            ex.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }
        return obj;
    }
}
