package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private final DBHelper dbHelper;
    private SQLiteDatabase db;

    public PersistentTransactionDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("Date", df.format(date));
        values.put("Account_Number", accountNo);
        values.put("Expense_Type", String.valueOf(expenseType));
        values.put("Amount", amount);


        db.insert("Transaction", null, values);
        db.close();
    }

        @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {

            db = dbHelper.getReadableDatabase();
            List<Transaction> transactionArray = new ArrayList<Transaction>();


            String[] projection = {
                    "Date",
                    "Account_Number",
                    "Expense_Type",
                    "Amount"
            };

            Cursor cursor = db.query(
                    "Transaction",
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            while(cursor.moveToNext()) {
                String dateformated = cursor.getString(cursor.getColumnIndex("Date"));
                Date dateString = new SimpleDateFormat("dd-MM-yyyy").parse(dateformated);
                String accountNumber = cursor.getString(cursor.getColumnIndex("Account_Number"));
                String typeString = cursor.getString(cursor.getColumnIndex("Expense_Type"));
                ExpenseType expenseType = ExpenseType.valueOf(typeString);
                double amount = cursor.getDouble(cursor.getColumnIndex("Amount"));
                Transaction transaction = new Transaction(dateString,accountNumber,expenseType,amount);

                transactionArray.add(transaction);
            }
            cursor.close();
            return transactionArray;
        }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        List<Transaction> transactionArray = new ArrayList<Transaction>();
        transactionArray=getAllTransactionLogs();
        int size = transactionArray.size();

        if (size <= limit) {
            return transactionArray;
        }
        return transactionArray.subList(size - limit, size);


    }

}
