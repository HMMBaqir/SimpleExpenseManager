package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final DBHelper dbHelper;
    private SQLiteDatabase db;


    public PersistentAccountDAO(Context context) {
        dbHelper = new DBHelper(context);
    }


    @Override
    public List<String> getAccountNumbersList() {
        db=dbHelper.getReadableDatabase();
        String[] projection = {
                "Account_Number"
        };

        Cursor cursor = db.query(
                "Account",
                projection,
                null,
                null,
                null,
                null,
                null
        );
        List<String> accountNumberArray = new ArrayList<String>();

        while(cursor.moveToNext()) {
            String accountNumber = cursor.getString(
                    cursor.getColumnIndexOrThrow("Account_Number"));
            accountNumberArray.add(accountNumber);
        }
        cursor.close();
        return accountNumberArray;
    }

    @Override
    public List<Account> getAccountsList() {

        db = dbHelper.getReadableDatabase();

        List<Account> accountArray = new ArrayList<Account>();

        String[] projection = {
                "Account_Number",
                "Bank_Name",
                "Account_Holder_Name",
                "Balance"
        };

        Cursor cursor = db.query(
                "Account",
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while(cursor.moveToNext()) {
            String accountNo = cursor.getString(cursor.getColumnIndex("Account_Number"));
            String accountHolderName = cursor.getString(cursor.getColumnIndex("Account_Holder_Name"));
            String bankName = cursor.getString(cursor.getColumnIndex("Bank_Name"));
            double balance = cursor.getDouble(cursor.getColumnIndex("Balance"));
            Account account = new Account(accountNo,bankName,accountHolderName,balance);

            accountArray.add(account);
        }
        cursor.close();
        return accountArray;

    }

    @Override
    public Account getAccount(String Account_Number) throws InvalidAccountException {

        db = dbHelper.getReadableDatabase();
        String[] projection = {
                "Account_Number",
                "Bank_Name",
                "Account_Holder_Name",
                "Balance"
        };

        String selection = "Account_Number" + " = ?";
        String[] whereArgument = { "Account_Number" };

        Cursor cursor = db.query(
                "Account",
                projection,
                selection,
                whereArgument,
                null,
                null,
                null
        );

        if (cursor == null){
            String msg = "Account " + Account_Number + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        else {
            cursor.moveToFirst();

            Account account = new Account(Account_Number,cursor.getString(cursor.getColumnIndex("Bank_Name")),
                    cursor.getString(cursor.getColumnIndex("Account_Holder_Name")), cursor.getDouble(cursor.getColumnIndex("Balance")));
            return account;
        }
    }

    @Override
    public void addAccount(Account account) {
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Account_Number", account.getAccountNo());
        values.put("Bank_Name", account.getBankName());
        values.put("Account_Holder_Name", account.getAccountHolderName());
        values.put("Balance",account.getBalance());


        db.insert("Account", null, values);
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        db =dbHelper.getWritableDatabase();
        db.delete("Account", "Account_NUmber" + " = ?",
                new String[] { accountNo });
        db.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        db = dbHelper.getWritableDatabase();
        String[] projection = {
                "Balance"
        };

        String selection = "Account_Number" + " = ?";
        String[] whereArgument = { accountNo };

        Cursor cursor = db.query(
                "Account",
                projection,
                selection,
                whereArgument,
                null,
                null,
                null
        );

        double balance;
        if(cursor.moveToFirst())
            balance = cursor.getDouble(0);
        else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        ContentValues values = new ContentValues();
        switch (expenseType) {
            case EXPENSE:
                values.put("Balance", balance - amount);
                break;
            case INCOME:
                values.put("Balance", balance + amount);
                break;
        }


        db.update("Account", values, "Account_Number" + " = ?",
                new String[] { accountNo });

        cursor.close();
        db.close();

    }
}
