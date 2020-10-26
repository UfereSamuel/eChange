package com.alc.echange.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alc.echange.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestCashActivity extends AppCompatActivity {
    private Button tvContact2;
    public final int PICK_CONTACT = 2015;
    private String removedCode, newPhone;
    private EditText etSender, etReciever, etPurpose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_cash);

        etReciever = findViewById(R.id.etReciever);

        tvContact2 = findViewById(R.id.tvContact2);
        tvContact2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, PICK_CONTACT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
//            Uri contactUri = data.getData();
//            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
//            cursor.moveToFirst();
//            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
////            (new normalizePhoneNumberTask()).execute(cursor.getString(column));
//            Log.d("phone number", cursor.getString(column));
//        }

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT) {
            String phoneNo = null;
            String name = null;

            Uri uri = data.getData();
            Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);

            if (cursor.moveToFirst()) {
                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                phoneNo = cursor.getString(phoneIndex);
                name = cursor.getString(nameIndex);

                newPhone = phoneNo.substring(1);

                //method to remove country code
                if (phoneNo.contains("+234")) {
                    removedCode = phoneNo.substring(4);
                } else if (phoneNo.contains("234")) {
                    removedCode = phoneNo.substring(3);
                } else if (phoneNo.contains("0")) {
                    removedCode = phoneNo.substring(1);
                } else {
//                        String str= "jld fdkjg jfdg ";
                    String pattern = "[\\s]";
                    String replace = "";
//
                    Pattern p = Pattern.compile(pattern);
                    Matcher m = p.matcher(phoneNo);

                    removedCode = m.replaceAll(replace);
//
//                        str=m.replaceAll(replace);

//                    removedCode = phoneNo.replaceAll("\\s+","");
                }


                Log.e("onActivityResult()", phoneIndex + " " + phoneNo + " " + nameIndex + " " + name);
                Log.e("numberzz", phoneNo);
                Log.e("numberzz", name);
                Log.e("numberzz", newPhone);
//                Log.e("numberzz", newphone2);
                Log.e("numberzz", removedCode);

                etReciever.setText("0" + removedCode);

            }
            cursor.close();
        }

    }

    public void backP(View view) {
        pressBack();
    }

    private void pressBack() {
        startActivity(new Intent(RequestCashActivity.this, DashboardActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pressBack();
    }
}