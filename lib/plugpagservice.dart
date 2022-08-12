import 'plugpagservice_platform_interface.dart';

class Plugpagservice {
  Future<String?> getPlatformVersion() {
    return PlugpagservicePlatform.instance.getPlatformVersion();
  }

  Future getRequestPermissions() {
    return PlugpagservicePlatform.instance.getRequestPermissions();
  }
}
