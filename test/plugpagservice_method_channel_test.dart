import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:plugpagservice/plugpagservice_method_channel.dart';

void main() {
  MethodChannelPlugpagservice platform = MethodChannelPlugpagservice();
  const MethodChannel channel = MethodChannel('plugpagservice');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
