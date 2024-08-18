package com.example.crud_operations;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
        lista = findViewById(R.id.listView_my_list);

        // Configurar o botão de voltar
        Button btnVoltar = findViewById(R.id.btn_voltar);
        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(MyListActivity.this, ListActivity.class);
            startActivity(intent);
        });

        // Configurar o botão de ação
        Button btnAction = findViewById(R.id.btn_action);
        btnAction.setOnClickListener(v -> showPhoneNumberDialog());

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

    private void showPhoneNumberDialog() {
        // Verificar se há produtos na lista
        if (listaBDHelper.getLista() == null || listaBDHelper.getLista().isEmpty()) {
            Toast.makeText(this, "A lista de produtos está vazia. Adicione produtos antes de enviar.", Toast.LENGTH_SHORT).show();
            return; // Não exibe o diálogo se a lista estiver vazia
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enviar para WhatsApp");

        // Infla o layout personalizado
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        builder.setView(customLayout);

        // Configura o EditText e o prefixo no layout personalizado
        EditText input = customLayout.findViewById(R.id.editText_phone_number);
        input.setHint("Digite o número de telefone");

        // Define os botões do diálogo
        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String phoneNumber = input.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                // Confirmar envio
                new AlertDialog.Builder(this)
                        .setTitle("Confirmar envio")
                        .setMessage("Você tem certeza que deseja enviar a lista para o número: " + phoneNumber + "?")
                        .setPositiveButton("Sim", (dialog1, which1) -> {
                            // Exibir o segundo diálogo informando que a mensagem está sendo enviada
                            showSendingMessageDialog();
                        })
                        .setNegativeButton("Não", (dialog1, which1) -> dialog1.dismiss())
                        .show();
            } else {
                Toast.makeText(MyListActivity.this, "Por favor, insira um número de telefone.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private boolean isPhoneNumberValid(String phoneNumber) {
        // Verifica se o número é válido. Ajuste conforme necessário.
        return phoneNumber.matches("\\d{10,11}"); // Apenas os números, sem o prefixo
    }


    private void showSendingMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enviando");
        builder.setMessage("Sua lista está sendo enviada...");
        builder.setCancelable(false);

        // Cria o diálogo
        AlertDialog dialog = builder.create();
        dialog.show();

        // Simula o tempo de envio da mensagem (ajuste conforme necessário)
        new Handler().postDelayed(() -> {
            dialog.dismiss();

            // Redirecionar para MainActivity
            Intent intent = new Intent(MyListActivity.this, MainActivity.class);
            startActivity(intent);

            // Opcional: Adicione um efeito de animação ao voltar para a MainActivity
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 2000); // 2 segundos de delay
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listaBDHelper != null) {
            listaBDHelper.close();
        }
    }
}
