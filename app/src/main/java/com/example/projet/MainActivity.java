package com.example.projet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity{

    ListView lv_calembours;
    Button b_precedent;
    Button b_suivant;
    TextView tv_nbPage;
    ImageButton ib_filtre;
    String liste = "";
    String posTexte;
    ArrayAdapter<String> arrayQuestion;
    ArrayList<Calembours> arrayCalembours;
    String[] listeCourante = new String[10];
    CalemboursDAO unCalemboursDAO;
    int posType;
    int posNote;
    int start = 0;
    int end = 9;
    boolean triType = false;
    boolean triVu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ib_filtre = findViewById(R.id.ib_filtre);

        // Permet de choisir un filtre de recherche
        ib_filtre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Appel de pop-up
                LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                View promptView = layoutInflater.inflate(R.layout.activity_filtrer, null);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this).setView(promptView);

                // -------- SPINNER TYPE
                Spinner s_type = (Spinner) promptView.findViewById(R.id.s_type);
                s_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        posType = i; // récupère position de l'élément cliqué
                        posTexte = s_type.getSelectedItem().toString(); // récupère le texte de l'élément cliqué
                        triType = true;

                        // Pagination
                        start = 0;
                        end = 9;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                // Mise à jour du spinner de types
                getTypes(s_type);
                s_type.setSelection(posType);

                // -------- SPINNER NOTE
                Spinner s_notes = (Spinner) promptView.findViewById(R.id.s_notes);
                s_notes.setSelection(posNote);
                s_notes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        posNote = i; // Récupère position de l'élément cliqué

                        // Pagination
                        start = 0;
                        end = 9;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                // -------- CHECKBOX VU
                CheckBox cb_vu = (CheckBox) promptView.findViewById(R.id.cb_vu);
                cb_vu.setChecked(triVu);
                cb_vu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            triVu = true;
                        }
                        else{
                            triVu = false;

                            // Pagination
                            start = 0;
                            end = 9;
                        }
                    }
                });

                // -------- CHOIX DES BUTTON DU POP-UP
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getCalembours(); // Récupère les calembours
                        if(triVu){
                            trierParVu();
                        }
                        if(triType){
                            trierParTypes();
                        }
                        trierParNotes(s_notes.getSelectedItem().toString());
                        b_suivant.setEnabled(true);
                        tv_nbPage.setText(String.valueOf(0));
                        b_precedent.setEnabled(false);
                        remplissageListeCourante(); // Insert les calembours dans la listeCourante
                        affichage();
                    }
                });
                alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.setNeutralButton("Réinitialiser", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cb_vu.setChecked(false);
                        posType = 0;
                        posNote = 0;
                        s_type.setSelection(0);
                        s_notes.setSelection(0);

                        getCalembours();
                        if(triVu){
                            trierParVu();
                        }
                        if(triType){
                            trierParTypes();
                        }
                        trierParNotes(s_notes.getSelectedItem().toString());
                        b_suivant.setEnabled(true);
                        tv_nbPage.setText(String.valueOf(0));
                        b_precedent.setEnabled(false);
                        remplissageListeCourante();
                        affichage();
                    }
                });
                AlertDialog b = alertDialog.create();
                b.show();
            }
        });

        // -------- LISTEVIEW CALEMBOURS
        lv_calembours = findViewById(R.id.lv_calembours);
        lv_calembours.setOnItemClickListener(new AdapterView.OnItemClickListener(){
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
                    Toast.makeText(getApplicationContext(), "Calembours non existant", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_nbPage = findViewById(R.id.tv_nbPage);
        tv_nbPage.setText(String.valueOf(0));

        // -------- PAGINATION
        b_precedent = findViewById(R.id.b_precedent);
        b_precedent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start -= 10;
                end -= 10;
                tv_nbPage.setText(String.valueOf(Integer.parseInt(tv_nbPage.getText().toString())-1));
                remplissageListeCourante();
                affichage();
                if(start == 0){
                    b_precedent.setEnabled(false);
                }
                if(end < arrayCalembours.size()){
                    b_suivant.setEnabled(true);
                }
            }
        });
        b_suivant = findViewById(R.id.b_suivant);
        b_suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(end != arrayCalembours.size()){
                    start += 10;
                    end += 10;
                    tv_nbPage.setText(String.valueOf(Integer.parseInt(tv_nbPage.getText().toString())+1));
                    remplissageListeCourante();
                    affichage();
                    if(start == 10){
                        b_precedent.setEnabled(true);
                    }
                    if(end >= arrayCalembours.size()){
                        b_suivant.setEnabled(false);
                    }
                }
            }
        });

        // -------- CREATION D'UN LIEN VERS LA BD
        unCalemboursDAO = new CalemboursDAO(getApplicationContext());

        // -------- CREATION D'UN THREAD QUI VA POUVOIR GERER L'APPEL A MON API > OPERATION LONGUE
        Thread thread = new Thread(){
            @Override
            public void run(){
                try{
                    connectionAPI();
                    transformer();
                    remplissageListeCourante();
                    affichage();
                } catch (IOException | JSONException e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    // -------- REPONSE D'UN RETOUR D'ACTIVITY > ICI NOTERCALEMBOURS
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        getCalembours();
        if(triVu){
            trierParVu();
        }
        if(triType){
            trierParTypes();
        }
        remplissageListeCourante();
        affichage();
    }

    // -------- TRIE PAR TYPE CHOISI DANS LE SPINNER
    public void trierParTypes(){
        if(posType != 0){
            arrayCalembours = (ArrayList<Calembours>) arrayCalembours.stream().filter(c -> posTexte.equals(c.getType())).collect(Collectors.toList());
        }
    }

    // -------- TRIE PAR VU SI CHECKBOX COCHEE
    public void trierParVu() {
        arrayCalembours = (ArrayList<Calembours>) arrayCalembours.stream().filter(c -> !c.isVu()).collect(Collectors.toList());
    }

    // -------- TRIE PAR NOTE CHOISI DANS LE SPINNER
    public void trierParNotes(String texte){
        if(texte.equals("Trier par ordre croissant")){
            arrayCalembours = (ArrayList<Calembours>) arrayCalembours.stream().sorted(Comparator.comparingDouble(Calembours::getNote)).collect(Collectors.toList());
        }
        else if(texte.equals("Trier par ordre décroissant")){
            arrayCalembours = (ArrayList<Calembours>) arrayCalembours.stream().sorted(Comparator.comparingDouble(Calembours::getNote).reversed()).collect(Collectors.toList());
        }
    }

    // -------- CONNECTION A L'API
    public void connectionAPI() throws IOException{
        URL url = new URL("https://official-joke-api.appspot.com/random_ten");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            liste = readStream(in);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
    }

    // -------- PERMET DE CONSTRUIRE LE RESULTAT SOUS FORME D'UN STRING
    private String readStream(InputStream is) throws IOException{
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    // -------- PERMET DE TRANSFORMER EN UN JSON QUI EST ENSUITE INSERE DANS LA BD
    public void transformer() throws JSONException{
        JSONArray json = new JSONArray(liste);
        for (int i = 0; i < json.length(); i++){
            unCalemboursDAO.open();
            unCalemboursDAO.addCalembours(new Calembours(0, json.getJSONObject(i).getString("type"), json.getJSONObject(i).getString("setup"), json.getJSONObject(i).getString("punchline"), 0f, false));
            unCalemboursDAO.close();
        }
        getCalembours();
    }

    // -------- REMPLISSAGE DE LA LISTE A AFFICHER
    public void remplissageListeCourante(){
        int indice = 0;
        for(int i = start; i <= end; i++) {
            if(arrayCalembours.size() > indice){
                listeCourante[indice++] = arrayCalembours.get(i).getSetup();
            }
            else{
                listeCourante[indice++] = "";
                b_suivant.setEnabled(false);
            }
        }
    }

    // -------- AFFICHAGE
    public void affichage(){
        arrayQuestion = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, listeCourante);
        runOnUiThread(() -> lv_calembours.setAdapter(arrayQuestion));
    }

    // -------- RECUPERE LES CALEMBOURS
    public void getCalembours(){
        unCalemboursDAO.open();
        arrayCalembours = unCalemboursDAO.getCalembours();
        unCalemboursDAO.close();
    }

    // -------- RECUPERE LES TYPES
    public void getTypes(Spinner spinner){
        unCalemboursDAO.open();
        spinner.setAdapter(new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, unCalemboursDAO.getTypes()));
        unCalemboursDAO.close();
    }

    // -------- CREATION DU MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ajouter_calembours, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.toString().equals("Ajouter un calembour")){
            Intent saisirCalembours = new Intent(getApplicationContext(), CreerCalembours.class);
            startActivityForResult(saisirCalembours,1);
        } else if(item.toString().equals("Statistiques")){
            Intent statistique = new Intent(getApplicationContext(), Statistique.class);
            startActivity(statistique);
        } else if(item.toString().equals("Historique")){
            Intent historique = new Intent(getApplicationContext(), Historique.class);
            startActivity(historique);
        }
        return super.onOptionsItemSelected(item);
    }
}