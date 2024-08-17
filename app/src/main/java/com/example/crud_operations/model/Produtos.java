package com.example.crud_operations.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Produtos implements Parcelable {

    private Long id;
    private String nome;
    private String marca;
    private double preco;
    private String localizacao;

    // Construtor padrão
    public Produtos() {
    }

    // Construtor para criar a partir de um Parcel
    protected Produtos(Parcel in) {
        id = in.readLong();
        nome = in.readString();
        marca = in.readString();
        preco = in.readDouble();
        localizacao = in.readString();
    }

    public static final Creator<Produtos> CREATOR = new Creator<Produtos>() {
        @Override
        public Produtos createFromParcel(Parcel in) {
            return new Produtos(in);
        }

        @Override
        public Produtos[] newArray(int size) {
            return new Produtos[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(nome);
        dest.writeString(marca);
        dest.writeDouble(preco);
        dest.writeString(localizacao);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    @Override
    public String toString() {
        return nome;
    }

    // Getters e setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }
}
