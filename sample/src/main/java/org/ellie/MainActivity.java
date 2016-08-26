package org.ellie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.ellie.library.OkHttpRequest;
import org.ellie.library.callback.Callback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_get_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_get_btn:
                Map<String, String> params = new HashMap<>();
                params.put("sort", "stars");
                params.put("order", "desc");

                OkHttpRequest.get("https://api.github.com/search/users")
                        .param("per_page", "2")
                        .param("q", "Jake")
                        .params(params)
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(int id, Response response) {
                        Log.e("get", "parseNetworkResponse " + id);

                        return response;
                    }

                    @Override
                    public void onResponse(int id, Object data) {
                        Log.e("get", "onResponse " + id + " " + data.toString());
                    }

                    @Override
                    public void onError(int id, Exception e) {
                        Log.e("get", "onError " + id);
                    }
                });
                break;
        }
    }
}
