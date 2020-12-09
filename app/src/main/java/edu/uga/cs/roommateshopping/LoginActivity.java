package edu.uga.cs.roommateshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This class implements the user to be able to log-in.
 * Log-in will use an email and a 6 digit character password.
 */
public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = (EditText) findViewById(R.id.usernameField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        loginButton = (Button) findViewById(R.id.loginButton);


        mAuth = FirebaseAuth.getInstance();
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // gets the user and password to validate the credentials
                final String username = usernameField.getText().toString().trim();
                final String password = passwordField.getText().toString().trim();
                loginUser(username, password);
            }
        });
    }

    /**
     *
     * @param user the email the user provided.
     * @param pass the 6 digit password user provided
     */
    private void loginUser(String user, String pass) {
        mAuth.signInWithEmailAndPassword(user, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) { // see if the task is successful
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent( LoginActivity.this, MainActivity.class );
                            startActivity( intent );
                        } else { // if does not work due to wrong password or email, give a error
                            // If sign in fails, display a message to the user.
                            String e = "fail: " + task.getException();
                            Toast.makeText(getApplicationContext(), "Failed login: "+e, Toast.LENGTH_SHORT).show();
                            // ...
                        }

                        // ...
                    }
                });

    }
}