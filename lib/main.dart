import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:webview_flutter/webview_flutter.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: DisposableBrowserScreen(),
    );
  }
}

class DisposableBrowserScreen extends StatefulWidget {
  const DisposableBrowserScreen({super.key});

  @override
  _DisposableBrowserScreenState createState() => _DisposableBrowserScreenState();
}

class _DisposableBrowserScreenState extends State<DisposableBrowserScreen> {
  static const platform = MethodChannel('com.example.ghostbrowse/disposable');
  static const webViewChannel = MethodChannel('webview_flutter');
  final TextEditingController _urlController = TextEditingController();
  String _status = "Ready";
  WebViewController? _webViewController;
  bool _isInstanceActive = false;
  String? _tempDir;

  Future<void> _startInstance() async {
    try {
      final String url = _urlController.text;
      if (url.isNotEmpty) {
        // Notify Kotlin to start isolated environment
        final Map<dynamic, dynamic> result = await platform.invokeMethod('startDisposableInstance', {'url': url});
        _tempDir = result['tempDir'];
        await webViewChannel.invokeMethod('setTempDir', {'tempDir': _tempDir});
        setState(() {
          _status = result['message']; // "Instance started"
          _isInstanceActive = true;
          _webViewController?.loadRequest(Uri.parse(url)); // Load URL in Flutter WebView
        });
      }
    } catch (e) {
      setState(() => _status = "Error: $e");
    }
  }

  Future<void> _stopInstance() async {
    try {
      final String result = await platform.invokeMethod('stopDisposableInstance');
      setState(() {
        _status = result; // "Instance stopped"
        _isInstanceActive = false;
        _webViewController?.loadRequest(Uri.parse('about:blank')); // Clear WebView
      });
    } catch (e) {
      setState(() => _status = "Error: $e");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("Disposable Browser")),
      body: Column(
        children: [
          Padding(
            padding: EdgeInsets.all(16.0),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _urlController,
                    decoration: InputDecoration(labelText: "Enter URL"),
                  ),
                ),
                SizedBox(width: 10),
                ElevatedButton(
                  onPressed: _startInstance,
                  child: Text("Launch"),
                ),
                SizedBox(width: 10),
                ElevatedButton(
                  onPressed: _stopInstance,
                  child: Text("Discard"),
                ),
              ],
            ),
          ),
          Text("Status: $_status"),
          Expanded(
            child: _isInstanceActive
                ? WebViewWidget(
              controller: _webViewController ??=
              WebViewController()
                ..setJavaScriptMode(JavaScriptMode.unrestricted)
                ..setBackgroundColor(const Color(0xFFFFFFFF))
                ..setNavigationDelegate(
                  NavigationDelegate(
                    onPageStarted: (String url) {
                      setState(() => _status = "Loading: $url");
                    },
                    onPageFinished: (String url) {
                      setState(() => _status = "Loaded: $url");
                    },
                    onWebResourceError: (error) {
                      setState(() => _status = "Error: ${error.description}");
                    },
                  ),
                ),
            )
                : const Center(child: Text("No active instance")),
          ),
        ],
      ),
    );
  }
}