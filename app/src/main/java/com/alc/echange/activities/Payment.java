package com.alc.echange.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alc.echange.R;
import com.alc.echange.model.Users;
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONException;

import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import faranjit.currency.edittext.CurrencyEditText;

public class Payment extends AppCompatActivity implements OnCardFormSubmitListener,
        CardEditText.OnCardTypeChangedListener {

    private Card card;
    private Charge charge;
    private String cvv, cardNumber;

    private EditText emailField;
    private EditText cardNumberField;
    private EditText expiryMonthField;
    private EditText expiryYearField;
    private EditText cvvField;

//    private String email;

    private ProgressDialog dialog;

    //    private String email, cardNumber, cvv;
    private int expiryMonth, expiryYear;


    private static final int GROUP_LEN = 4;
    private static final int koboToNaira = 100;
    private String paystack_public_key = "pk_test_cd59f81cce67a108d7452525f800aeb3eeb27f4c";

    private AppCompatActivity activity = Payment.this;

    private Users model;


    private EditText mPaymentDescription;
    private EditText mAmount;

    private TextView mTextError;
    private EditText mEmail;

    private Button payNowBtn;
    private String paymentFor;
    private String email, message, status;
    private int balance;

    private String account_number, gateway, transaction_code;

    String totalAmount, emailAddress, accountName;

    private Transaction transaction;

    private static final CardType[] SUPPORTED_CARD_TYPES = {CardType.VISA, CardType.MASTERCARD,
            CardType.DISCOVER, CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB,
            CardType.MAESTRO, CardType.UNIONPAY};

    private SupportedCardTypesView mSupportedCardTypesView;

    protected CardForm mCardForm;
    private CurrencyEditText etCurrency;

    private Button proceed;

    private String amount;
    private int newAmount = 0;

    private double pay;
    private int paying;

    private String transactionReference;

    private Bundle bundle;
    private KProgressHUD hud;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        hud = KProgressHUD.create(this);

        model = new Users();

        //initialize sdk
        PaystackSdk.initialize(getApplicationContext());
        PaystackSdk.setPublicKey(paystack_public_key);

        bundle = getIntent().getExtras();

        proceed = findViewById(R.id.proceed);

        mSupportedCardTypesView = findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);

        mTextError = findViewById(R.id.card_error);
        mEmail = findViewById(R.id.etEmail1);

//        email = model.getEmail();

        mCardForm = findViewById(R.id.card_form);
        mCardForm.cardRequired(true)
                .maskCardNumber(true)
                .maskCvv(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .mobileNumberRequired(false)
                .mobileNumberExplanation("Make sure SMS is enabled for this mobile number")
//                .actionLabel(getString(R.string.purchase))
                .setup(this);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvaible()) {
                    try {
                        validateInputs();
                        startAFreshCharge(true);

                    } catch (Exception e) {
                        Log.d("errro", "" + e.getMessage().toString());
                        Payment.this.mTextError.setText(String.format("An " +
                                "error occurred while charging card"));

                    }
                } else {
                    Toast.makeText(activity, "No Internet connectivity",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void validateInputs() {
        if (TextUtils.isEmpty(mCardForm.getCardNumber())) {
            mCardForm.setCardNumberError("Enter a valid card number");
            return;
        }
        if (TextUtils.isEmpty(mCardForm.getExpirationYear())) {
            mCardForm.setExpirationError("Enter your card expiry Year");
            return;
        }
        if (TextUtils.isEmpty(mCardForm.getExpirationMonth())) {
            mCardForm.setExpirationError("Enter your card expiry Month");
            return;
        }
        if (TextUtils.isEmpty(mCardForm.getCvv())) {
            mCardForm.setCvvError("Enter your Card Verification Value");
        }
    }

    private void startAFreshCharge(boolean local) {

        // initialize the charge
        charge = new Charge();
        charge.setCard(loadCardFromForm());

        if (!card.validNumber()) {
            MDToast.makeText(getApplicationContext(), "Invalid card number",
                    Toast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
            return;
        }

        if (!card.validCVC()) {
            MDToast.makeText(getApplicationContext(), "Invalid CVC",
                    Toast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
            return;
        }

        if (!card.validExpiryDate()) {
            MDToast.makeText(getApplicationContext(), "Card expiry date is invalid",
                    Toast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
            return;
        }

        if (card.isValid()) {

            pDialog = new SweetAlertDialog(Payment.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#03A9F4"));
            pDialog.getProgressHelper().setSpinSpeed(1.0f);
            pDialog.getProgressHelper().setBarWidth(6);
            pDialog.setTitleText("Processing Transaction...");
            pDialog.setCancelable(false);

            pDialog.show();

            if (local) {

                newAmount = bundle.getInt("Amount");
                gateway = bundle.getString("gateway");

                Log.d("pay", ""+pay);
                Log.d("pay2", ""+amount);

                email = mEmail.getText().toString();
//                email = model.getEmail();
//                gateway = "paystack";

                charge.setAmount(newAmount);
                charge.setEmail(email);
                charge.setReference(""+ Calendar.getInstance().getTimeInMillis());
                try {
                    charge.putCustomField("Charge from ", "Android device");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                chargeCard();
            }
        } else {
            pDialog.dismiss();
            MDToast.makeText(getApplicationContext(),
                    "Payment card is invalid.\nPlease try again later",
                    Toast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
        }
    }

    private void chargeCard() {

        Log.d("chargeCard", " entered chargeCard method");
        transaction = null;
        PaystackSdk.chargeCard(Payment.this, charge,
                new Paystack.TransactionCallback() {
                    // This is called only after transaction is successful
                    @Override
                    public void onSuccess(Transaction transaction) {
                        hud.dismiss();
                        int sam = (newAmount / koboToNaira);
                        amount = String.valueOf(sam);

                        transaction_code = transaction.getReference().toString();


                        Log.d("chargeCard", " transaction successful");
                        pDialog.dismissWithAnimation();

                        Payment.this.transaction = transaction;
                        mTextError.setText("Transaction Successful");
                        MDToast.makeText(activity, "Transaction successful",
                                Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                        Log.d("chargeCard", ""+transaction.toString());
                        Log.d("chargeCard1", ""+amount);
                        Log.d("chargeCard2", ""+gateway);
                        Log.d("chargeCard5", ""+email);
                        Log.d("chargeCard3", ""+transaction.getReference().toString());
                        Log.d("chargeCard4", ""+transaction.getClass().toString());
                        transactionReference = transaction.getReference().toString();
//                        PostPayment(account_number, amount, gateway, transaction_code);
                        postPayment();
                        updateTextViews();
                    }

                    // This is called only before requesting OTP
                    // Save reference so you may send to server if
                    // error occurs with OTP
                    // No need to dismiss dialog
                    @Override
                    public void beforeValidate(Transaction transaction) {
                        Payment.this.transaction = transaction;

                        Log.d("chargeCard", " before validation");

                        updateTextViews();
                    }

                    @Override
                    public void onError(Throwable error, Transaction transaction) {
                        // If an access code has expired, simply ask your server for a new one
                        // and restart the charge instead of displaying error

                        Toast.makeText(activity, "Transaction declined by bank or financial institution",
                                Toast.LENGTH_SHORT).show();
                        mTextError.setText("Transaction declined by bank or financial institution");

                        Log.d("chargeCard", " onError method");

                        Payment.this.transaction = transaction;
                        if (error instanceof ExpiredAccessCodeException) {
                            Payment.this.startAFreshCharge(false);
                            Payment.this.chargeCard();

                            return;
                        }

                        pDialog.dismissWithAnimation();

                        if (transaction.getReference() != null) {
//                            mTextError.setText(String.format("%s  concluded with error: %s %s",
//                                    transaction.getReference(), error.getClass().getSimpleName(),
//                                    error.getMessage()));
                            Log.d("error1", ""+transaction.getReference().toString());

//                             new verifyOnServer().execute(transaction.getReference());
                        } else {
                            MDToast.makeText(getApplicationContext(), error.getMessage(),
                                    MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
//                            Toasty.error(Payment.this, error.getMessage(),
//                                    Toast.LENGTH_SHORT, true).show();
//                            mTextError.setText(String.format("Error: %s %s", error.getClass()
//                                    .getSimpleName(), error.getMessage()));

                            Log.d("error1", ""+error.getMessage().toString());
                        }
                        updateTextViews();
                    }

                });
    }

    private void postPayment() {
    }

    private void updateTextViews() {
        if (transaction.getReference() != null) {
            mTextError.setText(String.format("Reference: %s", transaction.getReference()));
            Log.d("error1", ""+transaction.getReference().toString());
            Log.d("error1", ""+transaction.getReference().equalsIgnoreCase("status"));

        } else {
            mTextError.setText("No transaction");
        }
    }

    private Card loadCardFromForm() {
        String cardNum = mCardForm.getCardNumber().trim();

        //build card object with ONLY the number, update the other fields later
        card = new Card.Builder(cardNum, 0, 0, "").build();
        String cvc = mCardForm.getCvv().trim();
        //update the cvc field of the card
        card.setCvc(cvc);

        //validate expiry month;
        String sMonth = mCardForm.getExpirationMonth().trim();
        int month = 0;
        try {
            month = Integer.parseInt(sMonth);
        } catch (Exception ignored) {
        }

        card.setExpiryMonth(month);

        String sYear = mCardForm.getExpirationYear().trim();
        int year = 0;
        try {
            year = Integer.parseInt(sYear);
        } catch (Exception ignored) {
        }
        card.setExpiryYear(year);

        return card;
    }

    private boolean isNetworkAvaible() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();


    }

    @Override
    public void onCardFormSubmit() {

    }

    @Override
    public void onCardTypeChanged(CardType cardType) {

    }
}