package com.example.crud_operations;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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
    private boolean isAdminMode = false;

    private static final String PREFS_NAME = "AdminPrefs";
    private static final String KEY_IS_AUTHENTICATED = "is_authenticated";

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

        // Verifica se o admin está autenticado
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isAdminMode = preferences.getBoolean(KEY_IS_AUTHENTICATED, false);

        View clickableArea = findViewById(R.id.icon_action);
        clickableArea.setOnClickListener(v -> {
            Intent intent = isAdminMode
                    ? new Intent(ListActivity.this, FormActivity.class)
                    : new Intent(ListActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });

        View clickableArea2 = findViewById(R.id.btn_view_my_list);
        clickableArea2.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, MyListActivity.class);
            intent.putParcelableArrayListExtra("produtos_adicionados", produtosAdicionados);
            startActivity(intent);
        });

        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, MainActivity.class);
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
            if (adapter == null) {
                // Cria o adaptador pela primeira vez
                adapter = new ProdutosAdapter(
                        ListActivity.this,
                        listView_produtos,
                        isAdminMode, // Passa o estado do admin para o adaptador
                        isAdminMode  // Passa o estado do admin para o adaptador
                );

                // Define o listener para adicionar produtos à lista
                adapter.setOnAddToListClickListener(produto -> {
                    listaBDHelper.salvarProduto(produto);
                    Toast.makeText(ListActivity.this, "Produto adicionado à lista: " + produto.getNome(), Toast.LENGTH_SHORT).show();
                });

                // Define o listener para remover produtos da lista (somente em modo admin)
                adapter.setOnRemoveFromListClickListener(produto -> {
                    // Remover produto do banco de dados
                    bdHelper.deletarProduto(produto);

                    // Recarregar a lista após a remoção
                    carregarProduto();

                    Toast.makeText(ListActivity.this, "Produto removido: " + produto.getNome(), Toast.LENGTH_SHORT).show();
                });

                lista.setAdapter(adapter);
            } else {
                // Atualiza a lista e o adaptador, se já estiver criado
                adapter.clear();
                adapter.addAll(listView_produtos);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void filterList(String query) {
        if (adapter != null) {
            adapter.getFilter().filter(query);
        }
    }
}
