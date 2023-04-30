package com.example.projet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreerCalembours extends AppCompatActivity {

    EditText et_typeCalembours;
    EditText et_questionCalembours;
    EditText et_reponseCalembours;
    Button b_annuler;
    Button b_enregistrer;
    CalemboursDAO unCalemboursDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creer_calembours);
        unCalemboursDAO = new CalemboursDAO(getApplicationContext()); // Création d'un lien vers la BD
        et_typeCalembours = findViewById(R.id.et_typeCalembours);
        et_questionCalembours = findViewById(R.id.et_questionCalembours);
        et_reponseCalembours = findViewById(R.id.et_reponseCalembours);
        b_annuler = findViewById(R.id.b_annuler);
        b_annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        b_enregistrer = findViewById(R.id.b_enregistrer);
        b_enregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_typeCalembours.getText().length() != 0 && et_questionCalembours.getText().length() != 0 && et_reponseCalembours.getText().length() != 0) {
                    unCalemboursDAO.open();
                    unCalemboursDAO.addCalembours(new Calembours(0, et_typeCalembours.getText().toString(), et_questionCalembours.getText().toString(), et_reponseCalembours.getText().toString(), 0f, false));
                    unCalemboursDAO.close();
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Veuillez remplir tout les champs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}