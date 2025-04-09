// Copyright 2013 The Flutter Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.webviewflutter;

import android.webkit.WebStorage;
import androidx.annotation.NonNull;
import android.os.Build;
import android.webkit.WebView;
import androidx.annotation.NonNull;

/**
 * Host api implementation for {@link WebStorage}.
 *
 * <p>Handles creating {@link WebStorage}s that intercommunicate with a paired Dart object.
 */
public class WebStorageProxyApi extends PigeonApiWebStorage {

  private String tempDir; // Store the custom directory

  /** Creates a host API that handles creating {@link WebStorage} and invoke its methods. */
  public WebStorageProxyApi(@NonNull ProxyApiRegistrar pigeonRegistrar) {
    super(pigeonRegistrar);
    WebView.setDataDirectorySuffix("storage_" + System.currentTimeMillis());
  }

  // Custom method to set the temporary directory
  @Override
  public void setTemporaryDirectory(@NonNull WebStorage pigeon_instance, @NonNull String tempDir) {
    this.tempDir = tempDir;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      String suffix = "storage_" + System.currentTimeMillis(); // Unique suffix
      android.util.Log.d("WebStorageProxy", "Setting WebView suffix for tempDir: " + tempDir + " with suffix: " + suffix);
      WebView.setDataDirectorySuffix(suffix);
    }
  }

  @NonNull
  @Override
  public WebStorage instance() {
//    if (tempDir != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//       Ensure suffix is set before returning instance
//      WebView.setDataDirectorySuffix(tempDir);
//    }

    WebView.setDataDirectorySuffix("/data/data/com.example.ghostbrowse/cache/test");
//    WebView.setDataDirectorySuffix("storage_" + System.currentTimeMillis());
    WebStorage webStorage = WebStorage.getInstance();
    if (webStorage != null) {
        android.util.Log.d("WebStorageProxy", "WebStorage instance created successfully.");
        return webStorage;
        } else {
        android.util.Log.e("WebStorageProxy", "Failed to create WebStorage instance.");
    }
    android.util.Log.e("WebStorageProxy", "Failed to set custom path.");
    return WebStorage.getInstance();
  }

  @Override
  public void deleteAllData(@NonNull WebStorage pigeon_instance) {
    pigeon_instance.deleteAllData();
  }
}
