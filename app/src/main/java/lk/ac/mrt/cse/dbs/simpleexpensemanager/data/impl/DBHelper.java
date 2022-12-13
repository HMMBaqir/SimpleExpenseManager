package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES_Account =
            "CREATE TABLE " + "Account(" +
            "Account_Number TEXT PRIMARY KEY," +
            "Bank_Name TEXT," +
            "Account_Holder_Name TEXT," +
            "Balance REAL)";
    private static final String SQL_CREATE_ENTRIES_Transaction =
            "CREATE TABLE " + "Transaction(" +
                    "Date TEXT," +
                    "Account_Number TEXT," +
                    "Expense_Type TEXT," +
                    "Amount REAL," +
                    "FOREIGN KEY (Account_Number) " +
                    "REFERENCES Account(Account_Number))";


    public DBHelper( Context context) {
        super(context, "Expenses.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase Database) {
        Database.execSQL(SQL_CREATE_ENTRIES_Account);
        Database.execSQL((SQL_CREATE_ENTRIES_Transaction));
    }

    @Override
    public void onUpgrade(SQLiteDatabase Database, int i, int i1) {
        Database.execSQL("DROP TABLE IF EXISTS Account");
        Database.execSQL("DROP TABLE IF EXISTS Transactions");
        onCreate(Database);
    }




}
