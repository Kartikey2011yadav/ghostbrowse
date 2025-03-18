// Copyright 2013 The Flutter Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.webviewflutter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

import android.webkit.WebView;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
/**
 * Java platform implementation of the webview_flutter plugin.
 *
 * <p>Register this in an add to app scenario to gracefully handle activity and context changes.
 */
public class WebViewFlutterPlugin implements FlutterPlugin, ActivityAware {
  private FlutterPluginBinding pluginBinding;
  private ProxyApiRegistrar proxyApiRegistrar;
  private MethodChannel channel;
  private String tempDirSuffix;

  /**
   * Add an instance of this to {@link io.flutter.embedding.engine.plugins.PluginRegistry} to
   * register it.
   *
   * <p>Registration should eventually be handled automatically by v2 of the
   * GeneratedPluginRegistrant. https://github.com/flutter/flutter/issues/42694
   */
  public WebViewFlutterPlugin() {}

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    pluginBinding = binding;

    channel = new MethodChannel(binding.getBinaryMessenger(), "webview_flutter");
    proxyApiRegistrar =
        new ProxyApiRegistrar(
            binding.getBinaryMessenger(),
            binding.getApplicationContext(),
            new FlutterAssetManager.PluginBindingFlutterAssetManager(
                binding.getApplicationContext().getAssets(), binding.getFlutterAssets()));

    binding
        .getPlatformViewRegistry()
        .registerViewFactory(
            "plugins.flutter.io/webview",
            new FlutterViewFactory(proxyApiRegistrar.getInstanceManager()));

    proxyApiRegistrar.setUp();

    // Set up method channel to receive tempDir from Flutter
    channel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
      @Override
      public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
          case "setTempDir":
            String tempDir = call.argument("tempDir");
            if (tempDir != null) {
              tempDirSuffix = "instance_" + System.currentTimeMillis();
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WebView.setDataDirectorySuffix(tempDirSuffix);
                result.success("Temp dir set: " + tempDir);
              } else {
                result.success("Pre-Android 11: Cache will be cleared");
              }
            } else {
              result.error("INVALID_ARG", "tempDir is null", null);
            }
            break;
          default:
            result.notImplemented();
            break;
        }
      }
    });
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    if (proxyApiRegistrar != null) {
      proxyApiRegistrar.tearDown();
      proxyApiRegistrar.getInstanceManager().stopFinalizationListener();
      proxyApiRegistrar = null;
    }
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
    if (proxyApiRegistrar != null) {
      proxyApiRegistrar.setContext(activityPluginBinding.getActivity());
    }
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    proxyApiRegistrar.setContext(pluginBinding.getApplicationContext());
  }

  @Override
  public void onReattachedToActivityForConfigChanges(
      @NonNull ActivityPluginBinding activityPluginBinding) {
    proxyApiRegistrar.setContext(activityPluginBinding.getActivity());
  }

  @Override
  public void onDetachedFromActivity() {
    proxyApiRegistrar.setContext(pluginBinding.getApplicationContext());
  }

  /** Maintains instances used to communicate with the corresponding objects in Dart. */
  @Nullable
  public AndroidWebkitLibraryPigeonInstanceManager getInstanceManager() {
    return proxyApiRegistrar.getInstanceManager();
  }
}
