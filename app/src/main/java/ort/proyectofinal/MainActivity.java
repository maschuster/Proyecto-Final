package ort.proyectofinal;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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
                        new authenticationTask().execute();
                        /*System.out.println("Success");
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken, new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response != null) {
                                            try {
                                                String jsonresult = String.valueOf(json);
                                                System.out.println("JSON Result"+jsonresult);

                                                String idFacebook = json.getString("id");
                                                String nombre = json.getString("name");
                                                String email = json.getString("email");
                                                user = new Usuario (idFacebook,nombre,email);

                                                Intent intent = new Intent(MainActivity.this, ListarEventos.class);
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
                        */
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

    private class authenticationTask extends AsyncTask<String, Void, Usuario> {
        private OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPostExecute(Usuario userResult) {
            super.onPostExecute(userResult);
            Intent intent = new Intent(MainActivity.this, ListarEventos.class);
            startActivity(intent);
            }

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
                return parsearResultado(jsonresponse);              //Cuando entra después de pasar por parsear resultado, salta directamente a
            } catch (IOException | JSONException e) {
                Log.d("Error", e.getMessage());
                return new Usuario("","");                          //ACÁ
            }
        }

        Usuario parsearResultado(String JSONstr) throws JSONException {
                JSONObject jsonResultado = new JSONObject(JSONstr);
                String idFacebook = jsonResultado.getString("idFacebook");
                String nombre = jsonResultado.getString("nombre");
                user  = new Usuario(idFacebook,nombre);
            return user;
        }
    }
}


