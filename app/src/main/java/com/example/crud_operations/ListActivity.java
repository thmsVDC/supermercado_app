package com.example.crud_operations;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crud_operations.BDHelper.ListaProdutosBD;
import com.example.crud_operations.BDHelper.ProdutosBD;
import com.example.crud_operations.model.Produtos;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    ListView lista;
    EditText editTextSearch;
    ProdutosBD bdHelper;
    ListaProdutosBD listaBDHelper;
    List<Produtos> listView_produtos;
    ProdutosAdapter adapter;

    ArrayList<Produtos> produtosAdicionados;

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

        View clickableArea2 = findViewById(R.id.btn_view_my_list);
        clickableArea2.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, MyListActivity.class);
            intent.putParcelableArrayListExtra("produtos_adicionados", produtosAdicionados);
            startActivity(intent);
        });

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        lista = findViewById(R.id.listView_produtos);
        editTextSearch = findViewById(R.id.editText_search);

        produtosAdicionados = new ArrayList<>();

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Não faz nada antes do texto mudar
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Não faz nada depois do texto mudar
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarProduto();
    }

    public void carregarProduto() {
        bdHelper = new ProdutosBD(ListActivity.this);
        listaBDHelper = new ListaProdutosBD(ListActivity.this);
        listView_produtos = bdHelper.getLista();
        bdHelper.close();

        if (listView_produtos != null) {
            adapter = new ProdutosAdapter(
                    ListActivity.this,
                    listView_produtos,
                    false
            );

            adapter.setOnAddToListClickListener(produto -> {
                listaBDHelper.salvarProduto(produto);
                Toast.makeText(ListActivity.this, "Produto adicionado à lista: " + produto.getNome(), Toast.LENGTH_SHORT).show();
            });

            lista.setAdapter(adapter);
        }
    }

    private void filterList(String query) {
        if (adapter != null) {
            adapter.getFilter().filter(query);
        }
    }
}
