package com.example.crud_operations;

import android.content.Context;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crud_operations.BDHelper.ListaProdutosBD;
import com.example.crud_operations.BDHelper.ProdutosBD;
import com.example.crud_operations.model.Produtos;
import com.example.crud_operations.model.TipoProduto;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView lista;
    private EditText editTextSearch;
    private ProdutosBD bdHelper;
    private ListaProdutosBD listaBDHelper;
    private List<Produtos> listView_produtos;
    private ProdutosAdapter adapter;
    private ArrayList<Produtos> produtosAdicionados;
    private boolean isAdminMode = false;
    private String tipoSelecionado = null; // Inicializa a variável

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

        Button btnViewMyList = findViewById(R.id.btn_view_my_list);
        Button cancelButton = findViewById(R.id.button_cancel);

        // Configura o botão "Ver minha lista"
        btnViewMyList.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, MyListActivity.class);
            intent.putParcelableArrayListExtra("produtos_adicionados", produtosAdicionados);
            startActivity(intent);
        });

        btnViewMyList.setOnLongClickListener(v -> {
            Intent intent = isAdminMode
                    ? new Intent(ListActivity.this, FormActivity.class)
                    : new Intent(ListActivity.this, AdminLoginActivity.class);
            startActivity(intent);
            return true; // Indica que o long click foi consumido
        });

        // Configura o botão "Cancelar busca"
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, MainActivity.class);
            startActivity(intent);
        });

        cancelButton.setOnLongClickListener(v -> {
            Intent intent = isAdminMode
                    ? new Intent(ListActivity.this, FormActivity.class)
                    : new Intent(ListActivity.this, AdminLoginActivity.class);
            startActivity(intent);
            return true; // Indica que o long click foi consumido
        });

        View btnShowTypes = findViewById(R.id.icon_tipos);
        btnShowTypes.setOnClickListener(v -> showTiposDialog());

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

        // Carrega a lista de produtos ao iniciar a atividade
        carregarProduto();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarProduto();
    }

    private void carregarProduto() {
        bdHelper = new ProdutosBD(ListActivity.this);
        listaBDHelper = new ListaProdutosBD(ListActivity.this);

        // Carrega produtos com base no tipo selecionado
        if (tipoSelecionado == null || tipoSelecionado.equals("Sem filtro")) {
            listView_produtos = bdHelper.getLista();
        } else {
            listView_produtos = bdHelper.getProdutosPorTipo(tipoSelecionado);
        }
        bdHelper.close();

        if (listView_produtos != null) {
            if (adapter == null) {
                adapter = new ProdutosAdapter(
                        ListActivity.this,
                        listView_produtos,
                        isAdminMode,
                        isAdminMode,
                        false
                );

                adapter.setOnAddToListClickListener(produto -> {
                    listaBDHelper.salvarProduto(produto);
                    Toast.makeText(ListActivity.this, "Produto adicionado à lista: " + produto.getNome(), Toast.LENGTH_SHORT).show();
                });

                adapter.setOnRemoveFromListClickListener(produto -> {
                    bdHelper.deletarProduto(produto);
                    carregarProduto(); // Recarrega a lista após a remoção
                    Toast.makeText(ListActivity.this, "Produto removido: " + produto.getNome(), Toast.LENGTH_SHORT).show();
                    recreate();
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

    private void showTiposDialog() {
        List<TipoProduto> tipos = new ArrayList<>();
        tipos.add(new TipoProduto("Sem filtro", R.drawable.icon_sem_filtro));
        tipos.add(new TipoProduto("Bebidas alcoólicas", R.drawable.icon_bebidas_alcoolicas));
        tipos.add(new TipoProduto("Carne e frutos do mar", R.drawable.icon_acougue));
        tipos.add(new TipoProduto("Casa", R.drawable.icon_casa));
        tipos.add(new TipoProduto("Frios", R.drawable.icon_frios));
        tipos.add(new TipoProduto("Frutas", R.drawable.icon_frutas));
        tipos.add(new TipoProduto("Higiene pessoal", R.drawable.icon_higiene_pessoal));
        tipos.add(new TipoProduto("Laticínios", R.drawable.icon_laticinios));
        tipos.add(new TipoProduto("HortiFruti", R.drawable.icon_horti_fruti));
        tipos.add(new TipoProduto("Não perecíveis", R.drawable.icon_nao_pereciveis));
        tipos.add(new TipoProduto("Origem animal", R.drawable.icon_origem_animal));
        tipos.add(new TipoProduto("Padaria", R.drawable.icon_padaria));
        tipos.add(new TipoProduto("Produtos de limpeza", R.drawable.icon_produtos_limpeza));
        tipos.add(new TipoProduto("Produtos para pets", R.drawable.icon_produtos_pets));
        tipos.add(new TipoProduto("Bebidas", R.drawable.icon_refrigerantes));
        tipos.add(new TipoProduto("Outros", R.drawable.icon_outros));

        TipoProdutoAdapter tipoProdutoAdapter = new TipoProdutoAdapter(this, tipos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tipos de Produtos");
        builder.setAdapter(tipoProdutoAdapter, (dialog, which) -> {
            TipoProduto tipoSelecionado = tipos.get(which);
            Toast.makeText(ListActivity.this, "Selecionado: " + tipoSelecionado.getNome(), Toast.LENGTH_SHORT).show();

            // Atualiza o tipo selecionado e o filtro
            if ("Sem filtro".equals(tipoSelecionado.getNome())) {
                this.tipoSelecionado = null; // Define como null para indicar que não há filtro
            } else {
                this.tipoSelecionado = tipoSelecionado.getNome();
            }
            adapter.setTipoFiltro(this.tipoSelecionado);

            carregarProduto();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
