package com.example.crud_operations;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crud_operations.model.Produtos;

import java.util.ArrayList;

public class MyListActivity extends AppCompatActivity {

    ListView listView;
    Button btnAction;
    ArrayList<Produtos> produtosAdicionados;
    ProdutosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        View clickableArea = findViewById(R.id.btn_voltar);
        clickableArea.setOnClickListener(v -> {
            Intent intent = new Intent(MyListActivity.this, ListActivity.class);
            startActivity(intent);
        });

        listView = findViewById(R.id.listView_my_list);
        btnAction = findViewById(R.id.btn_action);

        if (savedInstanceState != null) {
            produtosAdicionados = savedInstanceState.getParcelableArrayList("produtos_adicionados");
        } else {
            produtosAdicionados = getIntent().getParcelableArrayListExtra("produtos_adicionados");
        }

        if (produtosAdicionados == null) {
            produtosAdicionados = new ArrayList<>();
        }

        adapter = new ProdutosAdapter(this, produtosAdicionados);
        listView.setAdapter(adapter);

        btnAction.setOnClickListener(v -> showWhatsAppNumberDialog());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("produtos_adicionados", produtosAdicionados);
    }

    private void showWhatsAppNumberDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_wpp_number, null);

        final EditText editTextWhatsappNumber = dialogView.findViewById(R.id.editText_whatsapp_number);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insira o número do WhatsApp")
                .setView(dialogView)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String whatsappNumber = editTextWhatsappNumber.getText().toString().trim();
                    if (TextUtils.isEmpty(whatsappNumber)) {
                        Toast.makeText(MyListActivity.this, "Por favor, insira um número do WhatsApp", Toast.LENGTH_SHORT).show();
                    } else {
                        enviarParaWhatsApp(whatsappNumber);
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();

        editTextWhatsappNumber.post(() -> {
            editTextWhatsappNumber.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(editTextWhatsappNumber, InputMethodManager.SHOW_IMPLICIT);
        });
    }

    private void enviarParaWhatsApp(String numero) {
        StringBuilder mensagem = new StringBuilder("Aqui está a lista de produtos:\n\n");
        for (Produtos produto : produtosAdicionados) {
            mensagem.append("Nome: ").append(produto.getNome()).append("\n");
            mensagem.append("Marca: ").append(produto.getMarca()).append("\n");
            mensagem.append("Preço: R$ ").append(produto.getPreco()).append("\n");
            mensagem.append("Localização: ").append(produto.getLocalizacao()).append("\n\n");
        }

        String url = "https://api.whatsapp.com/send?phone=" + numero + "&text=" + Uri.encode(mensagem.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Não foi possível encontrar o WhatsApp", Toast.LENGTH_SHORT).show();
        }
    }
}
