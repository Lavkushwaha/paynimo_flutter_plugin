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
