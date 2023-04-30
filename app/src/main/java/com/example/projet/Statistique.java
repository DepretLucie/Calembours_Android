package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Statistique extends AppCompatActivity{

    Spinner s_typeStat;
    TextView tv_nbVue;
    TextView tv_moyNotes;
    Button b_retourStat;
    CalemboursDAO unCalemboursDAO;
    ArrayAdapter<String> arrayTypeStats;
    int nbVue;
    float moyNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistique);

        s_typeStat = findViewById(R.id.s_typeStat);
        s_typeStat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getNumberVue();
                tv_nbVue.setText(String.valueOf(nbVue));
                getMoyenneNotes();
                tv_moyNotes.setText(String.valueOf(moyNotes));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        unCalemboursDAO = new CalemboursDAO(getApplicationContext()); // Création d'un lien vers la BD
        getTypes();
        getNumberVue();
        getMoyenneNotes();

        // -------- INITIALISATION
        tv_nbVue = findViewById(R.id.tv_nbVue);
        tv_nbVue.setText(String.valueOf(nbVue));
        tv_moyNotes = findViewById(R.id.tv_moyNotes);
        tv_moyNotes.setText(String.valueOf(moyNotes));
        b_retourStat = findViewById(R.id.b_retourStat);
        b_retourStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // -------- RECUPERE LE NOMBRE DE VU DES CALEMBOURS TOUT CONFONDUS
    public void getNumberVue(){
        unCalemboursDAO.open();
        nbVue = unCalemboursDAO.nbVue(s_typeStat.getSelectedItem().toString());
        unCalemboursDAO.close();
    }

    // -------- RECUPERE LA MOYENNE DES CALEMBOURS VU ET NOTE
    public void getMoyenneNotes(){
        unCalemboursDAO.open();
        moyNotes = unCalemboursDAO.moyNotes(s_typeStat.getSelectedItem().toString());
        unCalemboursDAO.close();
    }

    // -------- RECUPERE LES TYPES DE CALEMBOURS
    public void getTypes(){
        unCalemboursDAO.open();
        arrayTypeStats = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_dropdown_item_1line, unCalemboursDAO.getTypes());
        s_typeStat.setAdapter(arrayTypeStats);
        unCalemboursDAO.close();
    }
}