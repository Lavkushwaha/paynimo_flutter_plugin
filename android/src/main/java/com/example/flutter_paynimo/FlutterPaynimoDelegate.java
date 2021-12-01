package com.example.flutter_paynimo;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.paynimo.android.payment.PaymentActivity;
import com.paynimo.android.payment.PaymentModesActivity;
import com.paynimo.android.payment.model.Checkout;
import com.paynimo.android.payment.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.flutter.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class FlutterPaynimoDelegate implements  PluginRegistry.ActivityResultListener{

    private Activity activity;
    // private MethodChannel.Result pendingResult;

    private String MerchentId ="T750";
    private String PublicKey = "1234-6666-6789-56";


    public static final int REQUEST_CODE_PAYTM = 0x00;
    private static final String TAG = "CheckoutActivity";

    private MethodChannel.Result techProcessPaymentResult;

    private void onTechProcessPaymentFail(Map<String, Object> arguments) {
        Map<String,Object> result=new HashMap<>();
        result.put("status","failed");
        result.put("data","Payment Payment failed");
        result.put("errorCode",(String) arguments.get("errorCode"));
        result.put("errorMessage",(String) arguments.get("errorMessage"));
        techProcessPaymentResult.success(result);
    }
    private void onTechProcessPaymentSuccess(JSONObject data) {
        Map<String,Object> result=new HashMap<>();
        result.put("status","success");
        result.put("data",data.toString());
        techProcessPaymentResult.success(result);
    }



    public FlutterPaynimoDelegate(Activity activity) {
        this.activity = activity;
    }

    void configure(String merchentId, String publicKey) {


        this.MerchentId = merchentId != null ?merchentId:"T750";
        this.PublicKey=publicKey != null?publicKey:"1234-6666-6789-56";

    }



        void startPay(MethodCall call, MethodChannel.Result result) {

        Checkout checkout = new Checkout();
        checkout.setMerchantIdentifier(MerchentId);
        checkout.setTransactionIdentifier(String.valueOf(new Date().getTime()));
        checkout.setTransactionReference("ORD0001");
        checkout.setTransactionType(PaymentActivity.TRANSACTION_TYPE_SALE);
        checkout.setTransactionSubType(PaymentActivity.TRANSACTION_SUBTYPE_DEBIT);
        checkout.setTransactionCurrency("INR");
        checkout.setTransactionAmount("10");
        checkout.setTransactionDateTime("1-12-2021");
        checkout.setConsumerIdentifier("");
        checkout.setConsumerEmailID("");
        checkout.setConsumerMobileNumber("");
        checkout.setConsumerAccountNo("");
        checkout.addCartItem("FIRST", "10", "0.0", "0.0", "", "", "", "");

        Intent authIntent = PaymentModesActivity.Factory.getAuthorizationIntent(activity.getApplicationContext(), true);
        // Checkout Object
        Log.d("Checkout Request Object", checkout.getMerchantRequestPayload().toString());
        authIntent.putExtra(Constant.ARGUMENT_DATA_CHECKOUT, checkout);
        // Public Key
        authIntent.putExtra(PaymentActivity.EXTRA_PUBLIC_KEY, PublicKey);
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
            if (resultCode == PaymentActivity.RESULT_OK) {
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

                            //----------------
                            try {
                                Gson gson = new Gson();
                                String jsonString = gson.toJson(checkout_res.getMerchantResponsePayload());
                                JSONObject request = new JSONObject(jsonString);

                                String statusCode = checkout_res.getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getStatusCode();
                                String amount = checkout_res.getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getAmount();
                                String merchantTransactionIdentifier = checkout_res.getMerchantResponsePayload().getMerchantTransactionIdentifier();
                                String instrumentAliasName = checkout_res.getMerchantResponsePayload().getPaymentMethod().getInstrumentAliasName();
                                String instrumentToken = checkout_res.getMerchantResponsePayload().getPaymentMethod().getInstrumentToken();

                                request.put("amount", amount);
                                request.put("orderId", merchantTransactionIdentifier);
                                request.put("status", 1);
                                request.put("instrumentAliasName", instrumentAliasName);
                                request.put("instrumentToken", instrumentToken);

                                onTechProcessPaymentSuccess(request);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


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

                            String statusCode = checkout_res.getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getStatusCode();
                            Map<String, Object> errorArguments = new HashMap();
                            errorArguments.put("errorCode", statusCode);
                            errorArguments.put("errorMessage", "");
                            onTechProcessPaymentFail(errorArguments);
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

                    Map<String, Object> errorArguments = new HashMap();
                    errorArguments.put("errorCode", "");
                    if (error_code.equals("ERROR_PAYNIMO_023")) {
                        errorArguments.put("errorMessage", "Enter valid card details");
                    } else {
                        errorArguments.put("errorMessage", " ");
                    }
                    onTechProcessPaymentFail(errorArguments);
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




    // private void clearMethodCallAndResult() {
    //     pendingResult = null;
    // }


}
