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

public class SendCashActivity extends AppCompatActivity {
    private Button tvContact;
    private EditText etSendMoney;
    public final int PICK_CONTACT = 2015;
    private String newPhone, newphone2;
    private String mobile_number, removedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendcash);

        etSendMoney = findViewById(R.id.etSendMoneyPhone);
        removedCode = null;

        //button to pick up numbers from contacts
        tvContact = findViewById(R.id.tvContact);
        tvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, PICK_CONTACT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                } else
                {
                    String pattern="[\\s]";
                    String replace="";
//
                    Pattern p= Pattern.compile(pattern);
                    Matcher m=p.matcher(phoneNo);

                    removedCode = m.replaceAll(replace);
                }

                Log.e("onActivityResult()", phoneIndex + " " + phoneNo + " " + nameIndex + " " + name);
                Log.e("numberzz",  phoneNo);
                Log.e("numberzz",  name);
                Log.e("numberzz", newPhone);
                Log.e("numberzz", removedCode);

                etSendMoney.setText("0" + removedCode);

            }
            cursor.close();
        }

    }

    public void backP2(View view) {
        pressBack();
    }

    private void pressBack() {
        startActivity(new Intent(SendCashActivity.this, DashboardActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pressBack();
    }
}