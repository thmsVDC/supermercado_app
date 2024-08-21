package com.example.crud_operations;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crud_operations.BDHelper.ListaProdutosBD;
import com.example.crud_operations.model.Produtos;

import java.text.DecimalFormat;
import java.util.List;import java.text.DecimalFormat;import java.text.DecimalFormat;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crud_operations.BDHelper.ListaProdutosBD;
import com.example.crud_operations.model.Produtos;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyListActivity extends AppCompatActivity {

    private ListaProdutosBD listaBDHelper;
    private List<Produtos> listView_produtos;
    private ProdutosAdapter adapter;
    private ListView lista;
    private TextView textViewTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        // Configurar a ListView
        lista = findViewById(R.id.listView_my_list);

        // Configurar o TextView para o total de preços
        textViewTotalPrice = findViewById(R.id.textView_total_price);

        // Configurar o botão de voltar
        ImageView btnVoltar = findViewById(R.id.icon_voltar);
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
            // Ordenar a lista por localização
            Collections.sort(listView_produtos, new Comparator<Produtos>() {
                @Override
                public int compare(Produtos p1, Produtos p2) {
                    return compareLocalizacao(p1.getLocalizacao(), p2.getLocalizacao());
                }
            });

            adapter = new ProdutosAdapter(
                    MyListActivity.this,
                    listView_produtos,
                    true, // Modo de remoção
                    false,
                    true
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

            // Atualizar o total de preços
            atualizarTotalPreco();
        }
    }

    private int compareLocalizacao(String loc1, String loc2) {
        // Regex para encontrar o número na localização
        Pattern pattern = Pattern.compile("(\\d+)$");
        Matcher matcher1 = pattern.matcher(loc1);
        Matcher matcher2 = pattern.matcher(loc2);

        int num1 = 0, num2 = 0;

        if (matcher1.find()) {
            num1 = Integer.parseInt(matcher1.group(1));
        }
        if (matcher2.find()) {
            num2 = Integer.parseInt(matcher2.group(1));
        }

        // Comparar as localizações numericamente
        return Integer.compare(num1, num2);
    }

    private void atualizarTotalPreco() {
        double totalPreco = 0.0;

        if (listView_produtos != null) {
            for (Produtos produto : listView_produtos) {
                totalPreco += produto.getPreco();
            }
        }

        // Atualiza o TextView com o total
        textViewTotalPrice.setText(String.format("Total: R$ %.2f", totalPreco));
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
                            // Enviar a lista para o WhatsApp
                            enviarParaWhatsApp(phoneNumber);
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

    private void enviarParaWhatsApp(String phoneNumber) {
        // Verifica se o número de telefone está no formato correto
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+" + phoneNumber; // Adiciona o sinal de "+" no início se não estiver presente
        }

        // Construa a mensagem
        StringBuilder mensagem = new StringBuilder();
        mensagem.append("🛒 Lista de Produtos:\n\n");

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        for (Produtos produto : listView_produtos) {
            mensagem.append("📦 Nome: ").append(produto.getNome()).append("\n");
            mensagem.append("🏷 Marca: ").append(produto.getMarca()).append("\n");
            mensagem.append("📍 Localização: ").append(produto.getLocalizacao()).append("\n");
            mensagem.append("💰 Preço: R$ ").append(decimalFormat.format(produto.getPreco())).append("\n\n");
        }

        mensagem.append("📊 **Total da Compra:** R$ ").append(decimalFormat.format(calcularTotalPreco()));

        // URL para o WhatsApp
        String url = "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(mensagem.toString());

        // Cria a intenção para o WhatsApp
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        try {
            startActivity(intent);
            showSendingMessageDialog(); // Exibir diálogo de envio
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MyListActivity.this, "WhatsApp não está instalado.", Toast.LENGTH_SHORT).show();
        }
    }

    private double calcularTotalPreco() {
        double totalPreco = 0.0;
        if (listView_produtos != null) {
            for (Produtos produto : listView_produtos) {
                totalPreco += produto.getPreco();
            }
        }
        return totalPreco;
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

            // Exibir o diálogo de mensagem enviada
            showMessageSentDialog();
        }, 2000); // 2 segundos de delay
    }

    private void showMessageSentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enviado");
        builder.setMessage("Sua lista foi enviada com sucesso!")
                .setIcon(R.drawable.icon_done) // Substitua 'icon_done' pelo drawable apropriado
                .setPositiveButton("OK", (dialog, which) -> {
                    // Redirecionar para MainActivity
                    Intent intent = new Intent(MyListActivity.this, MainActivity.class);
                    startActivity(intent);

                    // Opcional: Adicione um efeito de animação ao voltar para a MainActivity
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });

        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listaBDHelper != null) {
            listaBDHelper.close();
        }
    }
}
