package co.edu.udea.compumovil.gr04_20211.paqueteriareg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.muddzdev.styleabletoast.StyleableToast;
import com.skydoves.elasticviews.ElasticCheckButton;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class RegisterClientsActivity extends AppCompatActivity {
    TextInputEditText email, password;
    private ElasticCheckButton registrar;
    private ProgressDialog progress;
    FirebaseAuth auth;
    AlertDialog alerta;
    FirebaseFirestore store;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_clients);
        //Titulo centrado de la app Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.txt_titulo_nav);

        //Activar boton back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        registrar = (ElasticCheckButton) findViewById(R.id.btnRegistrase);
        email = findViewById(R.id.gmail);
        password = findViewById(R.id.password);

        auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();

        progress = new ProgressDialog(this);
        alerta = new SpotsDialog.Builder().setContext(RegisterClientsActivity.this).setMessage("Por favor espere....").build();
        registrar();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void registrar() {
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userE = email.getText().toString().trim();
                String passE = password.getText().toString().trim();
                if(TextUtils.isEmpty(userE)){
                    StyleableToast.makeText(getApplicationContext(), "Ingrese un correo",
                            Toast.LENGTH_LONG, R.style.DemoButton).show();
                }else if(TextUtils.isEmpty(passE) || (passE.length() < 6)){
                    StyleableToast.makeText(getApplicationContext(), "Ingrese una contraseña de 6 caracteres",
                            Toast.LENGTH_LONG, R.style.DemoButton).show();
                }else{
                    alerta.show();
                    auth.createUserWithEmailAndPassword(userE, passE)
                            .addOnCompleteListener(RegisterClientsActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        StyleableToast.makeText(getApplicationContext(), "Hubo un error creando la cuenta. Es posible que ya haya una cuenta con ese correo.",
                                                Toast.LENGTH_LONG, R.style.DemoButton).show();
                                    }else{
                                        FirebaseUser user = auth.getCurrentUser();
                                        Intent i = new Intent(RegisterClientsActivity.this, LoginClientsActivity.class);
                                        StyleableToast.makeText(getApplicationContext(), "Cuenta creada exitosamente",
                                                Toast.LENGTH_LONG, R.style.DemoButton).show();
                                        DocumentReference df = store.collection("Users").document(user.getUid());
                                        Map<String,Object> userInfo = new HashMap<>();
                                        userInfo.put("isClient", "1");
                                        userInfo.put("email", userE);
                                        df.set(userInfo);
                                        startActivity(i);
                                        finish();
                                    }
                                    alerta.dismiss();
                                }
                            });
                    email.setText("");
                    password.setText("");
                }
            }
        });
    }
}