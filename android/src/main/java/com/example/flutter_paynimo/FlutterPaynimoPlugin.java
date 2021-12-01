package com.example.flutter_paynimo;


import static de.greenrobot.event.EventBus.TAG;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;


import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;


import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.paynimo.android.payment.PaymentActivity;
import com.paynimo.android.payment.PaymentModesActivity;
import com.paynimo.android.payment.model.Checkout;
import com.paynimo.android.payment.util.Constant;




import java.util.Date;
import java.util.Stack;


/** FlutterPaynimoPlugin */
public class FlutterPaynimoPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
//public class FlutterPaynimoPlugin extends FlutterActivity {
//    private static final String CHANNEL = "samples.flutter.dev/battery";
//    public static final int REQUEST_CODE_PAYTM = 0x00;
//
//    private static final String TAG = "CheckoutActivity";
//  / The MethodChannel that will the communication between Flutter and native Android
//  /
//  / This local reference serves to register the plugin with the Flutter Engine and unregister it
//  / when the Flutter Engine is detached from the Activity

    private static final String CHANNEL = "flutter_paynimo";
    private MethodChannel channel;
  private static FlutterPaynimoPlugin instance;
  private FlutterPaynimoDelegate delegate;

    private Activity activity;




  public static void registerWith(Registrar registrar) {
    if(instance == null) {
      instance = new FlutterPaynimoPlugin();
    }

    if(registrar.activity() != null) {
      instance.onAttachedToEngine((FlutterPluginBinding) registrar.messenger());
      instance.onAttachedToActivity(registrar.activity());
      registrar.addActivityResultListener(instance.getActivityResultListener());
    }

  }



  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {


    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_paynimo");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

    if(activity == null || delegate == null) {
      result.error("no_activity", "esewa_pnp plugin requires a foreground activity.", null);
    }

    if(call.method.equals("configure")){
      delegate.configure(call.argument("merchentId"),call.argument("publicKey"));
    }
    else if (call.method.equals("startPayment")) {
      // result.success("Android " + android.os.Build.VERSION.RELEASE);
    //   startPay();
      delegate.startPay(call,result);
//       Log.d(TAG, "onMethodCall: ");

    } else {
      result.notImplemented();
    }


  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(this);
    channel= null;

  }

  private PluginRegistry.ActivityResultListener getActivityResultListener() {
    return delegate;
  }

  private void onAttachedToActivity(Activity activity) {
        this.activity = activity;
        delegate = new FlutterPaynimoDelegate(activity);
    }


  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
    if(getActivityResultListener() != null) {
      activityPluginBinding.removeActivityResultListener(getActivityResultListener());
    }
    onAttachedToActivity(activityPluginBinding.getActivity());
    activityPluginBinding.addActivityResultListener(getActivityResultListener());
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
  activity = null;
  delegate =null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {
    if(getActivityResultListener() != null) {
      activityPluginBinding.removeActivityResultListener(getActivityResultListener());
    }
    onAttachedToActivity(activityPluginBinding.getActivity());
    activityPluginBinding.addActivityResultListener(getActivityResultListener());
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
    delegate = null;
  }
}
