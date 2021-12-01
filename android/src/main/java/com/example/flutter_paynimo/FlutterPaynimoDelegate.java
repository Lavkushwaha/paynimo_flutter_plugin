package com.example.flutter_paynimo;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.paynimo.android.payment.PaymentActivity;
import com.paynimo.android.payment.PaymentModesActivity;
import com.paynimo.android.payment.model.Checkout;
import com.paynimo.android.payment.util.Constant;

import java.util.Date;

import io.flutter.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class FlutterPaynimoDelegate implements  PluginRegistry.ActivityResultListener{

    private Activity activity;
    private MethodChannel.Result pendingResult;

    public static final int REQUEST_CODE_PAYTM = 0x00;
    private static final String TAG = "CheckoutActivity";


    public FlutterPaynimoDelegate(Activity activity) {
        this.activity = activity;
    }


     void startPay(MethodCall call, MethodChannel.Result result) {

        Checkout checkout = new Checkout();
        checkout.setMerchantIdentifier("T515688");
        checkout.setTransactionIdentifier(String.valueOf(new Date().getTime()));
        checkout.setTransactionReference("ORD0001");
        checkout.setTransactionType(PaymentActivity.TRANSACTION_TYPE_SALE);
        checkout.setTransactionSubType(PaymentActivity.TRANSACTION_SUBTYPE_DEBIT);
        checkout.setTransactionCurrency("INR");
        checkout.setTransactionAmount("50");
        checkout.setTransactionDateTime("30-11-2021");
        checkout.setConsumerIdentifier("10086");
        checkout.setConsumerEmailID("rohitbhard@gmail.com");
        checkout.setConsumerMobileNumber("8826120009");
        checkout.setConsumerAccountNo("");
        checkout.addCartItem("FIRST", "1", "0.0", "0.0", "", "", "", "");
        checkout.setPaymentInstructionAction("Y");
        checkout.setPaymentInstructionStartDateTime("30-11-2021");
        checkout.setPaymentInstructionEndDateTime("25-02-2050");
        checkout.setPaymentInstructionLimit("100");
        checkout.setPaymentInstructionFrequency("ADHO");
        checkout.setPaymentInstructionType("F");

        checkout.setConsumerAccountHolderName("ROHIT BHARDWAJ");
        checkout.setConsumerAccountType("Saving");
        checkout.setConsumerAccountNo("031401543318");
        checkout.setConsumerPan("AMDPB1252C"); //Consumer PAN
        checkout.setConsumerPhoneNumber("8826120009"); //Consumer Phone Number

        Intent authIntent = PaymentModesActivity.Factory.getAuthorizationIntent(activity.getApplicationContext(), true);
        // Checkout Object
        Log.d("Checkout Request Object", checkout.getMerchantRequestPayload().toString());
        authIntent.putExtra(Constant.ARGUMENT_DATA_CHECKOUT, checkout);
        // Public Key
        authIntent.putExtra(PaymentActivity.EXTRA_PUBLIC_KEY, "1234-6666-6789-56");
        // Requested Payment Mode
        authIntent.putExtra(PaymentActivity.EXTRA_REQUESTED_PAYMENT_MODE,
                PaymentActivity.PAYMENT_METHOD_NETBANKING);

        PaymentModesActivity.Settings settings = new PaymentModesActivity.Settings();
        authIntent.putExtra(Constant.ARGUMENT_DATA_SETTING, settings);

        activity.startActivityForResult(authIntent, PaymentActivity.REQUEST_CODE);

    }


    @Override
      public boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        // Check which request we're responding to
        if (requestCode == PaymentActivity.REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Result Code :" + RESULT_OK);
                if (data != null) {
                    try {
                        Checkout checkout_res = (Checkout) data
                                .getSerializableExtra(Constant
                                        .ARGUMENT_DATA_CHECKOUT);
                        Log.d("Checkout Response Obj", checkout_res
                                .getMerchantResponsePayload().toString());
                        String transactionSubType = checkout_res.getMerchantRequestPayload().getTransaction().getSubType();
                        System.out.println("Payment type => " + transactionSubType);
                        // Transaction Completed and Got SUCCESS
                        if (checkout_res.getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getStatusCode().equalsIgnoreCase(
                                PaymentActivity.TRANSACTION_STATUS_SALES_DEBIT_SUCCESS)) {
                            Toast.makeText(activity.getApplicationContext(), "Transaction Status - Success", Toast.LENGTH_SHORT).show();
                            Log.v("TRANSACTION STATUS=>", "SUCCESS");
                            System.out.println("TRANSACTION_STATUS_SALES_DEBIT_SUCCESS");
                            /**
                             * TRANSACTION STATUS - SUCCESS (status code
                             * 0300 means success), NOW MERCHANT CAN PERFORM
                             * ANY OPERATION OVER SUCCESS RESULT
                             */
                            if (checkout_res.getMerchantResponsePayload().
                                    getPaymentMethod().getPaymentTransaction().
                                    getInstruction().getId() != null && checkout_res.getMerchantResponsePayload().
                                    getPaymentMethod().getPaymentTransaction().
                                    getInstruction().getId().isEmpty()) {
                                Log.v("TRANSACTION SI STATUS=>",
                                        "SI Transaction Not Initiated");
                                System.out.println("TRANSACTION SI  SI Transaction Not Initiated");
                            } else if (checkout_res.getMerchantResponsePayload().
                                    getPaymentMethod().getPaymentTransaction().
                                    getInstruction().getId() != null && !checkout_res.getMerchantResponsePayload().
                                    getPaymentMethod().getPaymentTransaction().
                                    getInstruction().getId().isEmpty()) {
                            /*
                              SI TRANSACTION STATUS - SUCCESS (Mandate  Registration ID received means success)
                             */
                                System.out.println("TRANSACTION SI SUCCESS (Mandate  Registration ID received means success)");
                                Log.v("TRANSACTION SI STATUS=>", "SUCCESS");
                            }
                        } else if (checkout_res
                                .getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getStatusCode().equalsIgnoreCase(
                                        PaymentActivity.TRANSACTION_STATUS_DIGITAL_MANDATE_SUCCESS
                                )) {
                            Toast.makeText(activity.getApplicationContext(), "Transaction Status - Success", Toast.LENGTH_SHORT).show();
                            Log.v("TRANSACTION STATUS=>", "SUCCESS");
                            System.out.println("TRANSACTION_STATUS_DIGITAL_MANDATE_SUCCESS");
                            /**
                             * TRANSACTION STATUS - SUCCESS (status code
                             * 0398 means success), NOW MERCHANT CAN PERFORM
                             * ANY OPERATION OVER SUCCESS RESULT
                             */
                            if (checkout_res.getMerchantResponsePayload().
                                    getPaymentMethod().getPaymentTransaction().
                                    getInstruction().getId() != null
                                    && !checkout_res
                                    .getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getInstruction().getId().isEmpty()) {
                                /**
                                 * SI TRANSACTION STATUS - SUCCESS (status
                                 * code 0300 means success)
                                 */
                                Log.v("TRANSACTION SI STATUS=>",
                                        "INITIATED");
                                System.out.println("Transaction Digital Mandate Success");
                            } else {
                                System.out.println("Transaction Digital Mandate Failure");
                                /**
                                 * SI TRANSACTION STATUS - Failure (status
                                 * code OTHER THAN 0300 means failure)
                                 */
                                Log.v("TRANSACTION SI STATUS=>", "FAILURE");
                            }
                        } else {
                            System.out.println("Bank Error Failure");
                            // some error from bank side
                            Log.v("TRANSACTION STATUS=>", "FAILURE");
                            Toast.makeText(activity.getApplicationContext(),
                                    "Transaction Status - Failure",
                                    Toast.LENGTH_SHORT).show();
                        }
                        String umrnNo = "";
                        String addDetails = checkout_res
                                .getMerchantResponsePayload().getMerchantAdditionalDetails();
                        if (addDetails.contains("UMRNNumber")) {
                            String[] arrMandateData = addDetails.split("mandateData\\{");
                            if (arrMandateData != null && arrMandateData.length > 1) {
                                String[] arrMandateParams = arrMandateData[1].split("~");
                                if (arrMandateParams != null
                                        && arrMandateParams.length > 1) {
                                    String[] arrUMRN = arrMandateParams[0].split(":");
                                    if (arrUMRN != null && arrUMRN.length > 1) {
                                        umrnNo = arrUMRN[1];
                                    }
                                }
                            }
                        }

                        String result = "StatusCode : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getStatusCode()
                                + "\nStatusMessage : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getStatusMessage()
                                + "\nErrorMessage : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getErrorMessage()
                                + "\nAmount : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getAmount()
                                + "\nDateTime : " + checkout_res.
                                getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getDateTime()
                                + "\nMerchantTransactionIdentifier : "
                                + checkout_res.getMerchantResponsePayload()
                                .getMerchantTransactionIdentifier()
                                + "\nIdentifier : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getIdentifier()
                                + "\nBankSelectionCode : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getBankSelectionCode()
                                + "\nBankReferenceIdentifier : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getBankReferenceIdentifier()
                                + "\nRefundIdentifier : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getRefundIdentifier()
                                + "\nBalanceAmount : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getBalanceAmount()
                                + "\nInstrumentAliasName : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getInstrumentAliasName()
                                + "\nSI Mandate Id : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getInstruction().getId()
                                + "\nUMRN No : "
                                + umrnNo

                                + "\nSI Mandate Status : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getInstruction().getStatusCode()
                                + "\nSI Mandate Error Code : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getInstruction().getErrorcode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == PaymentActivity.RESULT_ERROR) {
                Log.d(TAG, "got an error");
                if (data.hasExtra(PaymentActivity.RETURN_ERROR_CODE) &&
                        data.hasExtra(PaymentActivity.RETURN_ERROR_DESCRIPTION)) {
                    String error_code = (String) data
                            .getStringExtra(PaymentActivity.RETURN_ERROR_CODE);
                    String error_desc = (String) data
                            .getStringExtra(PaymentActivity.RETURN_ERROR_DESCRIPTION);
                    Toast.makeText(activity.getApplicationContext(), " Got error :"
                            + error_code + "--- " + error_desc, Toast.LENGTH_SHORT)
                            .show();
                    Log.d(TAG + " Code=>", error_code);
                    Log.d(TAG + " Desc=>", error_desc);
                }
            } else if (resultCode == PaymentActivity.RESULT_CANCELED) {
                Toast.makeText(activity.getApplicationContext(), "Transaction Aborted by User",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "User pressed back button");
            }
        }

        if (requestCode == REQUEST_CODE_PAYTM && data != null) {
            Toast.makeText(activity, data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
        }

        return false;
    }




    private void clearMethodCallAndResult() {
        pendingResult = null;
    }


}
