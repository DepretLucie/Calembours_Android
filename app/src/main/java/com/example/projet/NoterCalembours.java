package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class NoterCalembours extends AppCompatActivity {

    TextView tv_typePunchline;
    TextView tv_questionPunchline;
    TextView tv_reponsePunchline;
    RatingBar rb_nbEtoiles;
    Button b_retour;
    Button b_enregistrer;
    CalemboursDAO unCalemboursDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noter_calembours);

        // -------- RECUPERE LES PARAMETRES PASSEES PAR L'ACTIVITY MAIN
        Bundle params = getIntent().getExtras();
        long id =  params.getLong("Id");
        String type = params.getString("Type");
        String setup = params.getString("Setup");
        String punchline = params.getString("Punchline");
        float note = params.getFloat("Note");
        Boolean vu = params.getBoolean("Vu");

        // -------- INITIALISATION
        tv_typePunchline = findViewById(R.id.tv_typePunchline);
        tv_typePunchline.setText(type);
        tv_questionPunchline = findViewById(R.id.tv_questionPunchline);
        tv_questionPunchline.setText(setup);
        tv_reponsePunchline = findViewById(R.id.tv_reponsePunchline);
        tv_reponsePunchline.setText(punchline);
        rb_nbEtoiles = findViewById(R.id.rb_nbEtoiles);
        rb_nbEtoiles.setRating(note);

        rb_nbEtoiles.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                b_enregistrer.setVisibility(View.VISIBLE);
            }
        });

        b_retour = findViewById(R.id.b_retour);
        b_retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unCalemboursDAO = new CalemboursDAO(getApplicationContext()); // Création d'un lien vers la BD
                Calembours calembours = new Calembours(id, type, setup, punchline, note, vu); // Création d'un calembour
                calembours.setVu(true);
                unCalemboursDAO.open();
                unCalemboursDAO.updateCalemboursSaisi(calembours); // Met à jour un calembour
                unCalemboursDAO.close();
                finish();
            }
        });

        b_enregistrer = findViewById(R.id.b_enregistrer);
        b_enregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unCalemboursDAO = new CalemboursDAO(getApplicationContext());
                Calembours calembours = new Calembours(id, type, setup, punchline, note, vu);
                calembours.setNote(rb_nbEtoiles.getRating());
                calembours.setVu(true);
                unCalemboursDAO.open();
                unCalemboursDAO.updateCalemboursSaisi(calembours);
                unCalemboursDAO.close();
                finish();
            }
        });
    }
}