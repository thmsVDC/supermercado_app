package com.example.crud_operations;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crud_operations.BDHelper.ListaProdutosBD;
import com.example.crud_operations.model.Produtos;

import java.util.List;

public class MyListActivity extends AppCompatActivity {

    private ListaProdutosBD listaBDHelper;
    private List<Produtos> listView_produtos;
    private ProdutosAdapter adapter;
    private ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        // Configurar a ListView
        lista = findViewById(R.id.listView_my_list); // Certifique-se de que o ID está correto

        // Configurar o botão de voltar
        Button btnVoltar = findViewById(R.id.btn_voltar);
        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(MyListActivity.this, ListActivity.class);
            startActivity(intent);
        });

        // Configurar o botão de ação
        Button btnAction = findViewById(R.id.btn_action);
        btnAction.setOnClickListener(v -> {
            // Exemplo de ação ao clicar no botão
            Toast.makeText(MyListActivity.this, "Botão de ação clicado", Toast.LENGTH_SHORT).show();
        });

        // Inicializar o helper do banco de dados
        listaBDHelper = new ListaProdutosBD(MyListActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarProduto();
    }

    private void carregarProduto() {
        listView_produtos = listaBDHelper.getLista();

        if (listView_produtos != null) {
            adapter = new ProdutosAdapter(
                    MyListActivity.this,
                    listView_produtos,
                    true // Modo de remoção
            );

            // Configurar o listener para o botão de remover
            adapter.setOnRemoveFromListClickListener(produto -> {
                // Remover produto do banco de dados
                listaBDHelper.deletarProduto(produto);

                // Recarregar a lista após a remoção
                carregarProduto();

                Toast.makeText(MyListActivity.this, "Produto removido da lista: " + produto.getNome(), Toast.LENGTH_SHORT).show();
            });

            lista.setAdapter(adapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listaBDHelper != null) {
            listaBDHelper.close();
        }
    }
}
