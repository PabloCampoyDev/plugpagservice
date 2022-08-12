import 'package:flutter_test/flutter_test.dart';
import 'package:plugpagservice/plugpagservice.dart';
import 'package:plugpagservice/plugpagservice_platform_interface.dart';
import 'package:plugpagservice/plugpagservice_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockPlugpagservicePlatform
    with MockPlatformInterfaceMixin
    implements PlugpagservicePlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<String?> getRequestPermissions() => Future.value('42');
}

void main() {
  final PlugpagservicePlatform initialPlatform =
      PlugpagservicePlatform.instance;

  test('$MethodChannelPlugpagservice is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelPlugpagservice>());
  });

  test('getPlatformVersion', () async {
    Plugpagservice plugpagservicePlugin = Plugpagservice();
    MockPlugpagservicePlatform fakePlatform = MockPlugpagservicePlatform();
    PlugpagservicePlatform.instance = fakePlatform;

    expect(await plugpagservicePlugin.getPlatformVersion(), '42');
  });

  test('getRequestPermission', () async {
    Plugpagservice plugpagservicePlugin = Plugpagservice();
    MockPlugpagservicePlatform fakePlatform = MockPlugpagservicePlatform();
    PlugpagservicePlatform.instance = fakePlatform;

    expect(await plugpagservicePlugin.getRequestPermissions(), '42');
  });
}
