package com.example.tsuyoshi.englishwordtraining;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;

/**
 * Created by tsuyoshi on 2018/07/13.
 */

public class Premium extends AppCompatActivity {

    private BillingClient mBillingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preminum);
/*
        mBillingClient = new BillingClient.Builder(mActivity).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingResponse.OK) {
                    // 課金クライアント準備完了。 ここに購入処理を記述。
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // startConnection()を呼び出してアプリ内課金サービスに再接続する
            }
        });
        */
    }
}
