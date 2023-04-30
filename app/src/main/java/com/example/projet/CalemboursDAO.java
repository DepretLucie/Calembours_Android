package com.example.projet;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class CalemboursDAO {

    private static final String TABLE_NAME = "Calembours";
    public static final String KEY_ID_CALEMBOURS="id";
    public static final String KEY_TYPE_CALEMBOURS="type";
    public static final String KEY_QUESTION_CALEMBOURS="setup";
    public static final String KEY_REPONSE_CALEMBOURS="punchline";
    public static final String KEY_NOTE_CALEMBOURS="note";
    public static final String KEY_DEJA_VU_CALEMBOURS="vu";
    public static final String CREATE_TABLE_CALEMBOURS = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+KEY_ID_CALEMBOURS+" INTEGER primary key," +
            " "+KEY_TYPE_CALEMBOURS+" TEXT NOT NULL," +
            " "+KEY_QUESTION_CALEMBOURS+" TEXT NOT NULL, " +
            " "+KEY_REPONSE_CALEMBOURS+" TEXT NOT NULL, " +
            " "+KEY_NOTE_CALEMBOURS+" FLOAT NOT NULL, " +
            " "+KEY_DEJA_VU_CALEMBOURS+" BOOLEAN NOT NULL" +
            ");";

    private TheSQLiteDB maBase; // notre gestionnaire du fichier SQLite
    private SQLiteDatabase db;

    // Constructeur
    public CalemboursDAO(Context context){
        maBase = TheSQLiteDB.getInstance(context);
    }

    public void open(){
        //on ouvre la table en lecture/écriture
        db = maBase.getWritableDatabase();
    }

    public void close(){
        //on ferme l'accès à la BDD
        db.close();
    }

    public long addCalembours(Calembours calembours){
        // Ajout d'un enregistrement dans la table
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE_CALEMBOURS, calembours.getType());
        values.put(KEY_QUESTION_CALEMBOURS, calembours.getSetup());
        values.put(KEY_REPONSE_CALEMBOURS, calembours.getPunchline());
        values.put(KEY_NOTE_CALEMBOURS, calembours.getNote());
        values.put(KEY_DEJA_VU_CALEMBOURS, calembours.isVu());
        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur
        return db.insert(TABLE_NAME,null,values);
    }

    public int updateNomOfTypeCalembours(Calembours calembours){
        // modification d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la requête
        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION_CALEMBOURS, calembours.getSetup());
        String where = KEY_TYPE_CALEMBOURS+" = ?";
        String[] whereArgs = {calembours.getType()+""};
        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    public int updateCalemboursSaisi(Calembours calembours){
        // modification d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la requête
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE_CALEMBOURS, calembours.getType());
        values.put(KEY_QUESTION_CALEMBOURS, calembours.getSetup());
        values.put(KEY_REPONSE_CALEMBOURS, calembours.getPunchline());
        values.put(KEY_NOTE_CALEMBOURS, calembours.getNote());
        values.put(KEY_DEJA_VU_CALEMBOURS, String.valueOf(calembours.isVu()));

        String where = KEY_ID_CALEMBOURS+" = ?";
        String[] whereArgs = {calembours.getId()+""};
        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    public int removeAllCalembour() {
        // suppression d'un enregistrement en fonction du type
        // valeur de retour : (int) nombre de lignes affectées par la clause WHERE, 0 sinon

        String where = KEY_TYPE_CALEMBOURS+" LIKE ?";
        String[] whereArgs = {"%%"};

        return db.delete(TABLE_NAME, where, whereArgs);
    }

    public int removeCalemboursSaisi(String question){
        // suppression d'un enregistrement
        String where = KEY_QUESTION_CALEMBOURS+" = ?";
        String[] whereArgs = {question + ""};
        return db.delete(TABLE_NAME, where, whereArgs);
    }

    public int removeAllCalemboursOfType(String type){
        // suppression des calembours d'un type
        String where = KEY_TYPE_CALEMBOURS+" = ?";
        String[] whereArgs = {type + ""};
        return db.delete(TABLE_NAME, where, whereArgs);
    }

    // SUPPRIMER UN TYPE DE CALEMBOUR = MÊME FONCTION QUE REMOVEALLCALEMBOURSOFTYPE PUISQUE SI ON SUPPRIME TOUT LES CALEMBOURS D'UN TYPE ALORS LE TYPE EST SUPPRIME

    @SuppressLint("Range")
    public Calembours getCalembours(int id){
        // Retourne le calembour dont l'id est passé en paramètre
        Calembours calembours = new Calembours(0L,"", "", "", 0f, false);
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_ID_CALEMBOURS+"="+id, null);

        if (cursor.moveToFirst()){
            calembours.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_CALEMBOURS)));
            calembours.setType(cursor.getString(cursor.getColumnIndex(KEY_TYPE_CALEMBOURS)));
            calembours.setSetup(cursor.getString(cursor.getColumnIndex(KEY_QUESTION_CALEMBOURS)));
            calembours.setPunchline(cursor.getString(cursor.getColumnIndex(KEY_REPONSE_CALEMBOURS)));
            calembours.setNote(cursor.getFloat(cursor.getColumnIndex(KEY_NOTE_CALEMBOURS)));
            calembours.setVu(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(KEY_DEJA_VU_CALEMBOURS))));
            cursor.close();
        }
        return calembours;
    }

    @SuppressLint("Range")
    public ArrayList<Calembours> getCalembours(){
        ArrayList<Calembours> calemboursList = new ArrayList<Calembours>();
        //récupère dans un curseur le résultat du select sur la table
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);

        if (c.moveToFirst()){
            //parcourt le curseur obtenu, jusqu'a la fin, et créer pour chaque enregistrement un objet Calembours
            do{
                Calembours calembours = new Calembours(0L, "", "", "", 0f, false);
                calembours.setId(c.getInt(c.getColumnIndex(KEY_ID_CALEMBOURS)));
                calembours.setType(c.getString(c.getColumnIndex(KEY_TYPE_CALEMBOURS)));
                calembours.setSetup(c.getString(c.getColumnIndex(KEY_QUESTION_CALEMBOURS)));
                calembours.setPunchline(c.getString(c.getColumnIndex(KEY_REPONSE_CALEMBOURS)));
                calembours.setNote(c.getFloat(c.getColumnIndex(KEY_NOTE_CALEMBOURS)));
                calembours.setVu(Boolean.parseBoolean(c.getString(c.getColumnIndex(KEY_DEJA_VU_CALEMBOURS))));
                // ajoute l'objet créé à la ArrayList de Calembour qui sera renvoyée.
                calemboursList.add(calembours);
            }
            while (c.moveToNext());
        }
        c.close();
        return calemboursList;
    }

    @SuppressLint("Range")
    public String[] getTypes(){
        //récupère dans un curseur le résultat du select sur la table
        Cursor c = db.rawQuery("SELECT DISTINCT type FROM "+TABLE_NAME, null);
        String[] types = new String[c.getCount()+1];
        types[0] = "Tous";
        int indice = 1;
        if (c.moveToFirst()){
            //parcourt le curseur obtenu, jusqu'a la fin, et créer pour chaque enregistrement un objet Calembours
            do{
                types[indice++] = c.getString(c.getColumnIndex(KEY_TYPE_CALEMBOURS));
            }
            while (c.moveToNext());
        }
        c.close();
        return types;
    }

    @SuppressLint("Range")
    public int nbVue(String type){
        //récupère dans un curseur le résultat du select sur la table
        Cursor c;
        if(type.equals("Tous")){
            c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE vu = 'true'", null);
        }
        else{
            c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE vu = 'true' AND type = '"+type+"'", null);
        }

        int nbVue = c.getCount();
        c.close();
        return nbVue;
    }

    @SuppressLint("Range")
    public float moyNotes(String type){
        //récupère dans un curseur le résultat du select sur la table
        Cursor c;
        float moyNotes = 0;
        if(type.equals("Tous")){
            c = db.rawQuery("SELECT AVG(note) FROM "+TABLE_NAME, null);
        }
        else{
            c = db.rawQuery("SELECT AVG(note) FROM "+TABLE_NAME+" WHERE type = '"+type+"'", null);
        }

        if (c.moveToFirst()){
            moyNotes = c.getFloat(c.getColumnIndex("AVG(note)"));
        }
        c.close();
        return moyNotes;
    }

    @SuppressLint("Range")
    public ArrayList<Calembours> calemboursVus(){
        ArrayList<Calembours> calemboursList = new ArrayList<Calembours>();
        //récupère dans un curseur le résultat du select sur la table
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE vu = 'true'", null);

        if (c.moveToFirst()){
            //parcourt le curseur obtenu, jusqu'a la fin, et créer pour chaque enregistrement un objet Calembours
            do{
                Calembours calembours = new Calembours(0L, "", "", "", 0f, false);
                calembours.setId(c.getInt(c.getColumnIndex(KEY_ID_CALEMBOURS)));
                calembours.setType(c.getString(c.getColumnIndex(KEY_TYPE_CALEMBOURS)));
                calembours.setSetup(c.getString(c.getColumnIndex(KEY_QUESTION_CALEMBOURS)));
                calembours.setPunchline(c.getString(c.getColumnIndex(KEY_REPONSE_CALEMBOURS)));
                calembours.setNote(c.getFloat(c.getColumnIndex(KEY_NOTE_CALEMBOURS)));
                calembours.setVu(Boolean.parseBoolean(c.getString(c.getColumnIndex(KEY_DEJA_VU_CALEMBOURS))));
                // ajoute l'objet créé à la ArrayList de Calembour qui sera renvoyée.
                calemboursList.add(calembours);
            }
            while (c.moveToNext());
        }
        c.close();
        return calemboursList;
    }
} // class CalemboursDAO