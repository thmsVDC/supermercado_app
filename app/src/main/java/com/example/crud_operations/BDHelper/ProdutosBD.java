package com.example.crud_operations.BDHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.crud_operations.model.Produtos;

import java.util.ArrayList;

public class ProdutosBD extends SQLiteOpenHelper {

    private static final String DATABASE = "bdprodutos";
    private static final int VERSION = 1;

    public ProdutosBD (Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String produto = "CREATE TABLE produtos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "nome TEXT NOT NULL," +
                "marca TEXT NOT NULL," +
                "preco DOUBLE NOT NULL," +
                "localizacao TEXT NOT NULL);";
        db.execSQL(produto);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String produto = "DROP TABLE IF EXISTS produtos";
        db.execSQL(produto);
    }


    public boolean salvarProduto(Produtos produto) {
        SQLiteDatabase db = null;
        long id = -1;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("nome", produto.getNome());
            values.put("marca", produto.getMarca());
            values.put("preco", produto.getPreco());
            values.put("localizacao", produto.getLocalizacao());

            id = db.insert("produtos", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return id != -1;
    }







    public void alterarProduto(Produtos produto) {
        ContentValues values = new ContentValues();

        values.put("nome", produto.getNome());
        values.put("marca", produto.getMarca());
        values.put("preco", produto.getPreco());
        values.put("localizacao", produto.getLocalizacao());

        String [] args = {produto.getId().toString()};
        getWritableDatabase().update("produtos", values, "id=?", args);
    }

    public void deletarProduto(Produtos produto) {
        String [] args = {produto.getId().toString()};
        getWritableDatabase().delete("produtos", "id=?", args);
    }

    public ArrayList<Produtos> getLista() {
        String [] columns = {"id", "nome", "marca", "preco", "localizacao"};

        Cursor cursor = getReadableDatabase().query(
                "produtos",
                columns,
                null,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<Produtos> produtos = new ArrayList<Produtos>();

        while (cursor.moveToNext()) {
            Produtos produto = new Produtos();
            produto.setId(cursor.getLong(0));
            produto.setNome(cursor.getString(1));
            produto.setMarca(cursor.getString(2));
            produto.setPreco(cursor.getDouble(3));
            produto.setLocalizacao(cursor.getString(4));

            produtos.add(produto);
        }
        cursor.close();

        return produtos;
    }
}
