package com.example.crud_operations;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crud_operations.BDHelper.ProdutosBD;
import com.example.crud_operations.model.Produtos;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ListView lista;
    ProdutosBD bdHelper;
    ArrayList<Produtos> listView_produtos;
    Produtos produto;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View clickableArea = findViewById(R.id.gear_icon);
        clickableArea.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, FormActivity.class);
            startActivity(intent);
        });

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);




        lista = (ListView) findViewById(R.id.listView_produtos);
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarProduto();
    }

    public void carregarProduto() {
        bdHelper = new ProdutosBD(ListActivity.this);
        listView_produtos = bdHelper.getLista();
        bdHelper.close();


        if(listView_produtos != null) {
            adapter = new ArrayAdapter<Produtos>(
                    ListActivity.this,
                    android.R.layout.simple_list_item_1,
                    listView_produtos
            );

            lista.setAdapter(adapter);
        }
    }



}