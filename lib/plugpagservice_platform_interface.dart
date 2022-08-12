import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'plugpagservice_method_channel.dart';

abstract class PlugpagservicePlatform extends PlatformInterface {
  /// Constructs a PlugpagservicePlatform.
  PlugpagservicePlatform() : super(token: _token);

  static final Object _token = Object();

  static PlugpagservicePlatform _instance = MethodChannelPlugpagservice();

  /// The default instance of [PlugpagservicePlatform] to use.
  ///
  /// Defaults to [MethodChannelPlugpagservice].
  static PlugpagservicePlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [PlugpagservicePlatform] when
  /// they register themselves.
  static set instance(PlugpagservicePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future getRequestPermissions() {
    throw UnimplementedError('requestPermission() has not been implemented.');
  }
}
