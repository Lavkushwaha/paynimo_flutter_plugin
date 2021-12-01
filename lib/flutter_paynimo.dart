import 'dart:async';

import 'package:flutter/services.dart';

class FlutterPaynimo {
  static const MethodChannel _channel = MethodChannel('flutter_paynimo');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('startPayment');
    return version;
  }

  /// CONFIGURE PAYMENT PARAMETERS
  /// MERCHENT ID - Your Paynimo merchant ID
  /// PUBLIC KEY - Your Paynimo public key

  static Future configure({
    String? merchantId,
    String? publicKey,
  }) async {
    var paynimoData = {
      "merchantId": "$merchantId",
      "publicKey": "$publicKey",
    };
    return await _channel.invokeMethod('configure', paynimoData);
  }

  /// START PAYMENT
  static Future startPayment() async {
    return await _channel.invokeMethod('startPayment');
  }
}
