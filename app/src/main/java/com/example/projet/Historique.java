package com.example.projet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Historique extends AppCompatActivity{

    ListView lv_calemboursHist;
    Button b_retourHist;
    CalemboursDAO unCalemboursDAO;
    ArrayList<Calembours> arrayCalembours;
    String[] listeCourante = new String[10];
    ArrayAdapter<String> arrayQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);
        lv_calemboursHist = findViewById(R.id.lv_calemboursHist);
        lv_calemboursHist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                if(listeCourante[i] != ""){
                    // Si listeCourante non vide > transmet les param à la page de note calembours
                    Intent intentAffichagePunchline = new Intent(getApplicationContext(), NoterCalembours.class);
                    intentAffichagePunchline.putExtra("Id", arrayCalembours.get(i).getId());
                    intentAffichagePunchline.putExtra("Type", arrayCalembours.get(i).getType());
                    intentAffichagePunchline.putExtra("Setup", arrayCalembours.get(i).getSetup());
                    intentAffichagePunchline.putExtra("Punchline", arrayCalembours.get(i).getPunchline());
                    intentAffichagePunchline.putExtra("Note", arrayCalembours.get(i).getNote());
                    intentAffichagePunchline.putExtra("Vu", arrayCalembours.get(i).isVu());
                    startActivityForResult(intentAffichagePunchline,1); // Récupère les résultats
                }
                else{
                    Toast.makeText(getApplicationContext(), "Aucun calembour vu", Toast.LENGTH_SHORT).show();
                }
            }
        });

        b_retourHist = findViewById(R.id.b_retourHist);
        b_retourHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        unCalemboursDAO = new CalemboursDAO(getApplicationContext()); // Création d'un lien vers la BD
        getVu();
        remplissageListeCourante();
        affichage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    public void remplissageListeCourante(){
        for(int i = 0; i < 10; i++) {
            if(arrayCalembours.size() > i){
                listeCourante[i] = arrayCalembours.get(i).getSetup();
            }
            else{
                listeCourante[i] = "";
            }
        }
    }

    public void affichage(){
        arrayQuestion = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, listeCourante);
        lv_calemboursHist.setAdapter(arrayQuestion);
    }

    public void getVu(){
        unCalemboursDAO.open();
        arrayCalembours = unCalemboursDAO.calemboursVus();
        unCalemboursDAO.close();
    }
}