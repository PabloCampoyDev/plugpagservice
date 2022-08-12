package br.app.gestaotec.plugpagservice

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import androidx.core.app.ActivityCompat
import io.flutter.app.FlutterActivity
import android.app.Activity
import android.text.TextUtils
import io.flutter.plugin.common.MethodChannel.*
import io.flutter.plugin.common.*
import  br.com.uol.pagseguro.plugpag.PlugPagAuthenticationListener
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult
import br.com.uol.pagseguro.plugpag.PlugPagVoidData
import br.com.uol.pagseguro.plugpag.PlugPagPaymentData
import br.com.uol.pagseguro.plugpag.PlugPag
import br.app.gestaotec.plugpagservice.task.PinpadVoidPaymentTask
import br.app.gestaotec.plugpagservice.task.PinpadPaymentTask
import br.app.gestaotec.plugpagservice.task.TerminalQueryTransactionTask
import br.app.gestaotec.plugpagservice.task.TerminalVoidPaymentTask
import br.app.gestaotec.plugpagservice.task.TerminalPaymentTask
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.content.pm.PackageInfo

/** PlugpagservicePlugin FlutterPlugin, MethodCallHandler,*/
class PlugpagservicePlugin(): FlutterActivity(), MethodCallHandler, TaskHandler, PlugPagAuthenticationListener, FlutterPlugin  {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private val PERMISSIONS_REQUEST_CODE = 0x1234
  private val CHANNEL = "plugpagservice"
  private var context: Activity? = null
  private lateinit var methodChannel : MethodChannel

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(),  "plugpagservice")
      channel.setMethodCallHandler(PlugpagservicePlugin(registrar.activity() as Activity,channel))
    }
  }
  constructor( activity: Activity, methodChannel: MethodChannel) : this() {
    this.context = activity
    this.methodChannel = methodChannel
    PlugPagManager.create(activity)
  }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "plugpagservice")
    methodChannel.setMethodCallHandler(this)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    methodChannel.setMethodCallHandler(null)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
      //PERMISSIONS
      "getRequestPermissions" -> this.getRequestPermissions()
      else -> result.notImplemented()
    }
  }

  // -----------------------------------------------------------------------------------------------------------------
  // Request missing permissions
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Requests permissions on runtime, if any needed permission is not granted.
   */
  private fun getRequestPermissions() {
    var missingPermissions: Array<String>? = null
    print("1");  
    missingPermissions = this.filterMissingPermissions(this.getManifestPermissions())

    print("2.1");  
    if (missingPermissions != null && missingPermissions.size > 0) {
      ActivityCompat.requestPermissions(this.context!!, missingPermissions, PERMISSIONS_REQUEST_CODE)
    } else {
      print("Todas permissões concedidas")
    }
  }
  /**
   * Returns a list of permissions requested on the AndroidManifest.xml file.
   *
   * @return Permissions requested on the AndroidManifest.xml file.
   */
  private fun getManifestPermissions(): Array<String> {
    var permissions: Array<String>? = null
    var info: PackageInfo? = null
    var pm: PackageManager? = this?.context?.packageManager

    try {
      info = pm?.getPackageInfo(this.context?.packageName as String, PackageManager.GET_PERMISSIONS)
      permissions = info?.requestedPermissions
    } catch (e: PackageManager.NameNotFoundException) {
      print(e.message);  
    }

    if (permissions == null) {
      permissions = arrayOf()
    }

    return permissions
  }
  /**
   * Filters only the permissions still not granted.
   *
   * @param permissions List of permissions to be checked.
   * @return Permissions not granted.
   */
  private fun filterMissingPermissions(permissions: Array<String>?): Array<String>? {
    var missingPermissions: Array<String>? = null
    var list: MutableList<String>? = null

    list = ArrayList()

    if (permissions != null && permissions.size > 0) {
      for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(this?.context?.applicationContext!!, permission) != PackageManager.PERMISSION_GRANTED) {
          list.add(permission)
        }
      }
    }

    missingPermissions = list.toTypedArray()

    return missingPermissions
  }

  override fun onTaskStart() {
    print("Aguarde")
  }
  override fun onProgressPublished(progress: String, transactionInfo: Any) {
    var message: String? = null
    var type: String? = null

    if (TextUtils.isEmpty(progress)) {
      message = "Aguarde"
    } else {
      message = progress
    }

    if (transactionInfo is PlugPagPaymentData) {
      when (transactionInfo.type) {
        PlugPag.TYPE_CREDITO -> type = "Crédito"

        PlugPag.TYPE_DEBITO -> type = "Débito"

        PlugPag.TYPE_VOUCHER -> type = "Voucher"
      }

      message =   "Tipo: $type \nValor: R$ ${transactionInfo.amount.toDouble() / 100.0}\nParcelamento: ${transactionInfo.amount.toDouble() / 100.0}\n==========\n ${message}"
    } else if (transactionInfo is PlugPagVoidData) {
      message = "Estorno\\n==========\\n${message}"
    }

    print(message)
  }
  override fun onTaskFinished(result: Any) {
    if (result is PlugPagTransactionResult) {
      this.showResult(result)
    } else if (result is String) {
      print(result)
    }
  }
  // -----------------------------------------------------------------------------------------------------------------
  // Result display
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Shows a transaction's result.
   *
   * @param result Result to be displayed.
   */
  private fun showResult(result: PlugPagTransactionResult) {
    var resultDisplay: String? = null
    var lines: MutableList<String>? = null

    if (result == null) {
      throw RuntimeException("Transaction result cannot be null")
    }

    lines = ArrayList()
    lines.add("Resultado: ${result.result}")

    if (!TextUtils.isEmpty(result.errorCode)) {
      lines.add("Codigo de error: ${result.errorCode}")
    }

    if (!TextUtils.isEmpty(result.amount)) {
      var value: String? = null

      value = String.format("%.2f",
              java.lang.Double.parseDouble(result.amount) / 100.0)
      lines.add("Valor: $value")
    }

    if (!TextUtils.isEmpty(result.availableBalance)) {
      var value: String? = null

      value = String.format("%.2f",
              java.lang.Double.parseDouble(result.amount) / 100.0)
      lines.add("Valor disponivel: $value")
    }

    if (!TextUtils.isEmpty(result.bin)) {
      lines.add("BIN: ${result.bin}")
    }

    if (!TextUtils.isEmpty(result.cardBrand)) {
      lines.add("Bandeira: ${result.cardBrand}")
    }

    if (!TextUtils.isEmpty(result.date)) {
      lines.add("Data:  ${result.date}")
    }

    if (!TextUtils.isEmpty(result.time)) {
      lines.add("Hora: ${result.time}")
    }

    if (!TextUtils.isEmpty(result.holder)) {
      lines.add("Titular:  ${result.holder}")
    }

    if (!TextUtils.isEmpty(result.hostNsu)) {
      lines.add("Host NSU:  ${result.hostNsu} ")
    }

    if (!TextUtils.isEmpty(result.message)) {
      lines.add("Menssagem: ${result.message}")
    }

    if (!TextUtils.isEmpty(result.terminalSerialNumber)) {
      lines.add("Numero de serie: ${result.terminalSerialNumber}")
    }

    if (!TextUtils.isEmpty(result.transactionCode)) {
      lines.add("Código da transação:  ${result.transactionCode}")
    }

    if (!TextUtils.isEmpty(result.transactionId)) {
      lines.add( "ID da transação; ${result.transactionId}")
    }

    if (!TextUtils.isEmpty(result.userReference)) {
      lines.add("Código de venda: ${result.userReference}")
    }

    resultDisplay = TextUtils.join("\n", lines)
    print(resultDisplay)
  }

  override fun onSuccess() {
    print("Usuario autenticado com sucesso")
  }

  override fun onError() {
    print("Usuario nao autenticado")
  }
}
