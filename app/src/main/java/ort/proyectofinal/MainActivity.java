package ort.proyectofinal;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    public static String url = "http://eventospf2016.azurewebsites.net/";
    Usuario user;
    AccessToken accessToken;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.eatapp", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("email");
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        accessToken = loginResult.getAccessToken();
                        //new authenticationTask().execute();
                        authentication(accessToken);
                        Intent intent = new Intent(MainActivity.this, ListarEventos.class);

                        startActivity(intent);
                        System.out.println("Success");
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void authentication(AccessToken accessToken){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            String accesToken = accessToken.getToken();
            json.put("accesToken", accesToken);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

            Request request = new Request.Builder()
                    .url(url+"authentication.php")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String jsonresponse = response.body().string();
            JSONObject jsonResultado = new JSONObject(jsonresponse);
            String idFacebook = jsonResultado.getString("idFacebook");
            String nombre = jsonResultado.getString("nombre");
            user  = new Usuario(idFacebook,nombre);
        } catch (IOException | JSONException e) {
            Log.d("Error", e.getMessage());
        }
    }

    /*private class authenticationTask extends AsyncTask<String, Void, Usuario> {
        private OkHttpClient client = new OkHttpClient();


        @Override
        protected Usuario doInBackground(String... params) {
            try {
            JSONObject json = new JSONObject();
            String accesToken = accessToken.getToken();
            json.put("accesToken", accesToken);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

            Request request = new Request.Builder()
                    .url(url+"authentication.php")
                    .post(body)
                    .build();

                Response response = client.newCall(request).execute();
                String jsonresponse = response.body().string();
                return parsearResultado(jsonresponse);
            } catch (IOException | JSONException e) {
                Log.d("Error", e.getMessage());
                return new Usuario("","");
            }
        }

        Usuario parsearResultado(String JSONstr) throws JSONException {
                JSONObject jsonResultado = new JSONObject(JSONstr);
                String idFacebook = jsonResultado.getString("idFacebook");
                String nombre = jsonResultado.getString("nombre");
                user  = new Usuario(idFacebook,nombre);
            return user;
        }
    }*/
}


