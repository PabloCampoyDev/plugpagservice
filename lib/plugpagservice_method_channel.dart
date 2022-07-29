import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'plugpagservice_platform_interface.dart';

/// An implementation of [PlugpagservicePlatform] that uses method channels.
class MethodChannelPlugpagservice extends PlugpagservicePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('plugpagservice');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
