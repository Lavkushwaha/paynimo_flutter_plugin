# Flutter Paynimo Payment Gateway - flutter_paynimo

[![pub package](https://img.shields.io/pub/v/flutter_paynimo.svg)](https://pub.dev/packages/flutter_paynimo)

A Flutter plugin for Android Paynimo Payment Gateway Integration.

*Note*: This plugin is still under development, and some APIs might not be available yet. We are working on a refactor which can be followed here: [issue]()

## Features

* Initialize the payment gateway with the merchant credentials
* Make a payment request
* Handle the payment response

## Installation

First, add `flutter_paynimo` as a [dependency in your pubspec.yaml file](https://flutter.dev/using-packages/).

<!-- ### iOS


``` -->

### Android

Change the minimum Android sdk version to 21 (or higher) in your `android/app/build.gradle` file.

```
minSdkVersion 21
```


<!-- ### Web integration

For web integration details, see the -->

### Initialization


```dart
   @override
  void initState() {
    super.initState();

    /// INITIALIZE PAYNIMO
    initializePaynimo();
  }

  /// ENTER YOUR PAYNIMO PUBLIC KEY AND MERCHENT ID HERE
  /// THTESE ARE TEST CREDENTIALS
  /// MERCHANT ID: T750
  /// PUB KEY: 1234-6666-6789-56

  initializePaynimo() async {
    await FlutterPaynimo.configure(
        merchantId: "T750", publicKey: "1234-6666-6789-56");
  }
```

### Example

Here is a full example flutter app running paynimo client.

```dart
import 'package:flutter/material.dart';
import 'package:flutter_paynimo/flutter_paynimo.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String paymentResponse = 'Unknown';

  @override
  void initState() {
    super.initState();

    /// INITIALIZE PAYNIMO
    initializePaynimo();
  }

  /// ENTER YOUR PAYNIMO PUBLIC KEY AND MERCHENT ID HERE
  /// THTESE ARE TEST CREDENTIALS
  /// MERCHANT ID: T750
  /// PUB KEY: 1234-6666-6789-56

  initializePaynimo() async {
    await FlutterPaynimo.configure(
        merchantId: "T750", publicKey: "1234-6666-6789-56");
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('PAYNIMO FLUTTER PAYMENT'),
        ),
        body: Center(
          child: ElevatedButton(
            child: const Text("Start Payment"),
            onPressed: () async {
              //   final result = await FlutterPaynimo.startPayment(
              //       amount: "100",
              //       currency: "NGN",
              //       description: "Test Payment",
              //       email: "",
              //       firstName: "",
              //       lastName: "",
              //       phone: "",
              //       redirectUrl: "",
              //       metadata: "",
              //       onSuccess: (data) {
              //         setState(() {
              //           paymentResponse = data;
              //         });
              //       },
              //       onError: (data) {
              //         setState(() {
              //           paymentResponse = data;
              //         });
              //       });
              // });

              Map<dynamic, dynamic> paymentResult =
                  await FlutterPaynimo.startPayment();

              debugPrint("PAYMENT RESULT : ${paymentResult.toString()}");
            },
          ),
        ),
      ),
    );
  }
}


```

For a more elaborate usage example see [here](https://github.com/Lavkushwaha/paynimo_flutter_plugin/example/).

*Note*: This plugin is still under development, and some APIs might not be available yet.
[Feedback welcome](https://github.com/flutter/flutter/issues) and
[Pull Requests](https://github.com/flutter/plugins/pulls) are most welcome!