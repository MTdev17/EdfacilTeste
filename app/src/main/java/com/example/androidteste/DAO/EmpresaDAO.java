package com.example.androidteste.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.androidteste.model.Empresa;

public class EmpresaDAO {

    private final SQLiteDatabase write;
    private final SQLiteDatabase read;

    public EmpresaDAO(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        write = dbHelper.getWritableDatabase();
        read = dbHelper.getReadableDatabase();
    }

    public void salvar(Empresa empresa){
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.COLUNA_ID_FIREBASE, empresa.getId());
        cv.put(DbHelper.COLUNA_NOME, empresa.getNome());
        cv.put(DbHelper.COLUNA_TAXA_ENTREGA, empresa.getTaxaEntrega());
        cv.put(DbHelper.COLUNA_TEMPO_MINIMO, empresa.getTempominEntrega());
        cv.put(DbHelper.COLUNA_TEMPO_MAXIMO, empresa.getTempomaxEntrega());
        cv.put(DbHelper.COLUNA_URL_IMAGEM, empresa.getUrlLogo());

        try {
            write.insert(DbHelper.TABELA_EMPRESA, null, cv);
            Log.i("INFO_DB", "onCreate: Sucesso ao salvar tabela.");
        } catch (Exception e) {
            Log.i("INFO_DB", "onCreate: Erro ao salvar tabela.");
        }

    }

    public Empresa getEmpresa(){
        Empresa empresa = null;

        String sql = " SELECT * FROM " + DbHelper.TABELA_EMPRESA + ";";
        Cursor cursor  = read.rawQuery(sql, null);

        while (cursor.moveToNext()){
            @SuppressLint("Range") String id_firebase = cursor.getString(cursor.getColumnIndex(DbHelper.COLUNA_ID_FIREBASE));
            @SuppressLint("Range") String nome = cursor.getString(cursor.getColumnIndex(DbHelper.COLUNA_NOME));
            @SuppressLint("Range") double taxa_entrega = cursor.getDouble(cursor.getColumnIndex(DbHelper.COLUNA_TAXA_ENTREGA));
            @SuppressLint("Range") int tempo_minimo = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUNA_TEMPO_MINIMO));
            @SuppressLint("Range") int tempo_maximo = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUNA_TEMPO_MAXIMO));
            @SuppressLint("Range") String url_logo = cursor.getString(cursor.getColumnIndex(DbHelper.COLUNA_URL_IMAGEM));

            empresa = new Empresa();
            empresa.setId(id_firebase);
            empresa.setNome(nome);
            empresa.setTaxaEntrega(taxa_entrega);
            empresa.setTempominEntrega(tempo_minimo);
            empresa.setTempomaxEntrega(tempo_maximo);
            empresa.setUrlLogo(url_logo);

        }
        return empresa;
    }

    public void removerEmpresa(){
        try {
            write.delete(DbHelper.TABELA_EMPRESA, null, null);
            Log.i("INFO_DB", "onCreate: Sucesso ao remover a tabela.");
        } catch (Exception e) {
            Log.i("INFO_DB", "onCreate: Erro ao remover a tabela..");
        }
    }
}
