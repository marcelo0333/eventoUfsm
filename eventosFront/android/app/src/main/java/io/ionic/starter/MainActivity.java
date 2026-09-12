package io.ionic.starter;

import android.os.Bundle;
import android.webkit.WebSettings;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    // No Capacitor 6 os plugins são registrados automaticamente.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // A página roda em https://localhost (Capacitor), mas a API do backend
        // é http puro na rede local (sem certificado). Sem isso o WebView
        // bloqueia a chamada como "mixed content".
        WebSettings settings = this.bridge.getWebView().getSettings();
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    }
}
