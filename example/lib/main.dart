import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:plugpagservice/plugpagservice.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _plugpagservicePlugin = Plugpagservice();

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion = await _plugpagservicePlugin.getPlatformVersion() ??
          'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Text('Running on: $_platformVersion\n'),
              Text(
                "Antes de tudo, faça o pareamento da sua máquina de cartão!",
                textAlign: TextAlign.center,
                style: TextStyle(color: Colors.red, fontSize: 20),
              ),
              Text(
                "Se nao fizer isso o aplicativo vai fechar, estou resolvendo isso...",
                textAlign: TextAlign.center,
                style: TextStyle(color: Colors.red, fontSize: 20),
              ),
              SizedBox(
                height: 30,
              ),
              Text("Execute os botões na ordem"),
              ElevatedButton(
                child: Text("Pedir permissões"),
                onPressed: () {
                  _plugpagservicePlugin.getRequestPermissions();
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
