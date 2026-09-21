package com.bukukas.app;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.activity.OnBackPressedCallback;

import com.getcapacitor.BridgeActivity;

/**
 * Tombol kembali Android TIDAK keluar dari aplikasi dan TIDAK berpindah halaman.
 * Tombol itu diteruskan ke Buku Kas (Apps Script) lewat pesan "kembali"; aplikasi yang
 * menutup kalender/dialog/form atau menghapus kata kunci pencarian. Jika tidak ada
 * yang bisa ditutup, tidak terjadi apa-apa.
 *
 * File ini menggantikan MainActivity bawaan Capacitor saat build (lihat build-android.yml).
 */
public class MainActivity extends BridgeActivity {

    // Kirim {bk:'kembali'} ke semua frame di dalam halaman web app. Halaman Apps Script
    // menaruh kode aplikasi di iframe bersarang, jadi pesan dikirim sampai kedalaman 2.
    private static final String JS_KEMBALI =
        "(function(){function k(w,d){var n=0;try{n=w.frames.length;}catch(e){return;}" +
        "for(var i=0;i<n;i++){try{w.frames[i].postMessage({bk:'kembali'},'*');}catch(e){}" +
        "if(d<2){k(w.frames[i],d+1);}}}k(window,0);})();";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Dipasang setelah super.onCreate supaya didahulukan dibanding penanganan bawaan Capacitor
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                teruskanKembali();
            }
        });
    }

    // Jalur lama (Android sebelum 13) agar perilakunya sama
    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        teruskanKembali();
    }

    private void teruskanKembali() {
        if (getBridge() == null) return;
        WebView web = getBridge().getWebView();
        if (web != null) web.evaluateJavascript(JS_KEMBALI, null);
    }
}
