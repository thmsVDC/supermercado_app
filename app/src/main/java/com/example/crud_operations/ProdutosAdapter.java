package com.example.crud_operations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.crud_operations.BDHelper.ProdutosBD;
import com.example.crud_operations.model.Produtos;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProdutosAdapter extends ArrayAdapter<Produtos> implements Filterable {

    private List<Produtos> originalList;
    private List<Produtos> filteredList;
    private OnAddToListClickListener onAddToListClickListener;
    private OnRemoveFromListClickListener onRemoveFromListClickListener;
    private ProdutoFilter filter;
    private boolean isRemoveMode;
    private boolean isAdminAuthenticated;
    private ProdutosBD produtosBD;

    public interface OnAddToListClickListener {
        void onAddToListClick(Produtos produto);
    }

    public interface OnRemoveFromListClickListener {
        void onRemoveFromListClick(Produtos produto);
    }

    public void setOnAddToListClickListener(OnAddToListClickListener listener) {
        this.onAddToListClickListener = listener;
    }

    public void setOnRemoveFromListClickListener(OnRemoveFromListClickListener listener) {
        this.onRemoveFromListClickListener = listener;
    }

    public ProdutosAdapter(Context context, List<Produtos> produtos, boolean isRemoveMode, boolean isAdminAuthenticated) {
        super(context, 0, produtos);
        this.originalList = new ArrayList<>(produtos);
        this.filteredList = new ArrayList<>(produtos);
        this.filter = new ProdutoFilter();
        this.isRemoveMode = isRemoveMode;
        this.isAdminAuthenticated = isAdminAuthenticated;
        this.produtosBD = new ProdutosBD(context);
    }

    public void setRemoveMode(boolean removeMode) {
        this.isRemoveMode = removeMode;
        notifyDataSetChanged();
    }

    public void setAdminAuthenticated(boolean adminAuthenticated) {
        this.isAdminAuthenticated = adminAuthenticated;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_produto, parent, false);
        }

        Produtos produto = getItem(position);

        TextView textViewNome = convertView.findViewById(R.id.textView_nome_valor);
        TextView textViewMarca = convertView.findViewById(R.id.textView_marca_valor);
        TextView textViewPreco = convertView.findViewById(R.id.textView_preco_valor);
        TextView textViewLocalizacao = convertView.findViewById(R.id.textView_localizacao_valor);
        Button buttonAction = convertView.findViewById(R.id.btn_action_or_remove);
        ImageView imageViewGearFill = convertView.findViewById(R.id.img_gear_fill);

        textViewNome.setText(produto.getNome());
        textViewMarca.setText(produto.getMarca());

        // Formatar o preço para ter duas casas decimais
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String precoFormatado = decimalFormat.format(produto.getPreco());
        textViewPreco.setText(precoFormatado);

        textViewLocalizacao.setText(produto.getLocalizacao());

        if (isRemoveMode) {
            buttonAction.setText("Remover da lista");
            buttonAction.setBackgroundColor(getContext().getResources().getColor(R.color.red));
            buttonAction.setOnClickListener(v -> {
                if (onRemoveFromListClickListener != null) {
                    onRemoveFromListClickListener.onRemoveFromListClick(produto);
                }
            });

            imageViewGearFill.setVisibility(View.GONE);
        } else {
            buttonAction.setText("+ Adicionar à lista");
            buttonAction.setBackgroundColor(getContext().getResources().getColor(R.color.primary));
            buttonAction.setOnClickListener(v -> {
                if (onAddToListClickListener != null) {
                    onAddToListClickListener.onAddToListClick(produto);
                }
            });

            if (isAdminAuthenticated) {
                imageViewGearFill.setVisibility(View.VISIBLE);
                imageViewGearFill.setOnClickListener(v -> {
                    removeProdutoDoBanco(produto);
                    originalList.remove(produto);
                    filteredList.remove(produto);
                    notifyDataSetChanged();
                });
            } else {
                imageViewGearFill.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private void removeProdutoDoBanco(Produtos produto) {
        produtosBD.deletarProduto(produto);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Produtos getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ProdutoFilter();
        }
        return filter;
    }

    private class ProdutoFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Produtos> filteredResults = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredResults.addAll(originalList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Produtos produto : originalList) {
                    if (produto.getNome().toLowerCase().contains(filterPattern)) {
                        filteredResults.add(produto);
                    }
                }
            }

            results.values = filteredResults;
            results.count = filteredResults.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (List<Produtos>) results.values;
            notifyDataSetChanged();
        }
    }
}
