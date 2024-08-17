package com.example.crud_operations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.crud_operations.model.Produtos;

import java.util.ArrayList;
import java.util.List;

public class ProdutosAdapter extends ArrayAdapter<Produtos> implements Filterable {

    private List<Produtos> originalList;
    private List<Produtos> filteredList;
    private OnAddToListClickListener onAddToListClickListener;
    private ProdutoFilter filter;

    public interface OnAddToListClickListener {
        void onAddToListClick(Produtos produto);
    }

    public void setOnAddToListClickListener(OnAddToListClickListener listener) {
        this.onAddToListClickListener = listener;
    }

    public ProdutosAdapter(Context context, List<Produtos> produtos) {
        super(context, 0, produtos);
        this.originalList = new ArrayList<>(produtos);
        this.filteredList = new ArrayList<>(produtos);
        this.filter = new ProdutoFilter();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_produto, parent, false);
        }

        Produtos produto = getItem(position);

        TextView textViewNome = convertView.findViewById(R.id.textView_nome);
        TextView textViewMarca = convertView.findViewById(R.id.textView_marca);
        TextView textViewPreco = convertView.findViewById(R.id.textView_preco);
        TextView textViewLocalizacao = convertView.findViewById(R.id.textView_localizacao);
        Button buttonAddLista = convertView.findViewById(R.id.btn_add_list);

        textViewNome.setText("Nome: " + produto.getNome());
        textViewMarca.setText("Marca: " + produto.getMarca());
        textViewPreco.setText("Preço: R$ " + produto.getPreco());
        textViewLocalizacao.setText("Localização: " + produto.getLocalizacao());

        buttonAddLista.setOnClickListener(v -> {
            if (onAddToListClickListener != null) {
                onAddToListClickListener.onAddToListClick(produto);
            }
        });

        return convertView;
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
