package ort.proyectofinal;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends FragmentActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    public static String url = "http://eventospf2016.azurewebsites.net/";
    Usuario user;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("email");

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        System.out.println("Success");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response != null) {
                                            try {
                                                String jsonresult = String.valueOf(json);
                                                System.out.println("JSON Result"+jsonresult);

                                                String idFacebook = json.getString("id");
                                                String nombre = json.getString("name");
                                                String email = json.getString("email");
                                                checkUser(idFacebook,nombre,email);
                                                user = new Usuario (idFacebook,nombre,email);

                                                Intent intent = new Intent(MainActivity.this, ListarEventos.class);
                                                intent.putExtra("user", user);
                                                startActivity(intent);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();

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

    public void checkUser(String idFacebook, String nombre, String email){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            json.put("idFacebook", idFacebook);
            json.put("nombre", nombre);
            json.put("email", email);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

            Request request = new Request.Builder()
                    .url(url+"checkUser.php")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String respuesta = response.body().string();        //Validar si hay errores
        } catch (IOException | JSONException e) {
            Log.d("Error", e.getMessage());
        }
    }
}
