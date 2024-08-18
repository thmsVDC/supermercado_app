package com.example.crud_operations;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crud_operations.BDHelper.ProdutosBD;
import com.example.crud_operations.model.Produtos;

public class FormActivity extends AppCompatActivity {
    EditText editText_nome, editText_marca, editText_preco, editText_localizacao;
    Button btn_polimorfismo;
    Produtos editarProduto, produto;
    ProdutosBD bdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        View clickableArea = findViewById(R.id.btn_voltar);
        clickableArea.setOnClickListener(view -> {
            Intent intent = new Intent(FormActivity.this, ListActivity.class);
            startActivity(intent);
            finish();
        });

        produto = new Produtos();
        bdHelper = new ProdutosBD(FormActivity.this);

        Intent intent = getIntent();
        editarProduto = (Produtos) intent.getSerializableExtra("produto-escolhido");

        editText_nome = findViewById(R.id.editText_nome);
        editText_marca = findViewById(R.id.editText_marca);
        editText_preco = findViewById(R.id.editText_preco);
        editText_localizacao = findViewById(R.id.editText_localizacao);

        btn_polimorfismo = findViewById(R.id.btn_polimorfismo);

        if (editarProduto != null) {
            btn_polimorfismo.setText("Modificar");
            // Preenche os campos com os dados do produto a ser editado
            editText_nome.setText(editarProduto.getNome());
            editText_marca.setText(editarProduto.getMarca());
            editText_preco.setText(String.valueOf(editarProduto.getPreco()));
            editText_localizacao.setText(editarProduto.getLocalizacao());
        } else {
            btn_polimorfismo.setText("Cadastrar");
        }

        btn_polimorfismo.setOnClickListener(view -> {
            produto.setNome(editText_nome.getText().toString());
            produto.setMarca(editText_marca.getText().toString());

            double preco = 0;
            try {
                preco = Double.parseDouble(editText_preco.getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            produto.setPreco(preco);
            produto.setLocalizacao(editText_localizacao.getText().toString());

            if (btn_polimorfismo.getText().toString().equals("Cadastrar")) {
                boolean success = bdHelper.salvarProduto(produto);
                if (success) {
                    // Exibe a mensagem de sucesso e limpa os campos
                    Toast.makeText(FormActivity.this, "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    // Exibe a mensagem de falha, se necessário
                    Toast.makeText(FormActivity.this, "Falha ao cadastrar o produto.", Toast.LENGTH_SHORT).show();
                }
            } else {
                produto.setId(editarProduto.getId()); // Assumindo que o ID deve ser atualizado
                boolean success = bdHelper.alterarProduto(produto);
                if (success) {
                    // Exibe a mensagem de sucesso
                    Toast.makeText(FormActivity.this, "Produto modificado com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    // Exibe a mensagem de falha, se necessário
                    Toast.makeText(FormActivity.this, "Falha ao modificar o produto.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearFields() {
        editText_nome.setText("");
        editText_marca.setText("");
        editText_preco.setText("");
        editText_localizacao.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bdHelper != null) {
            bdHelper.close();
        }
    }
}
