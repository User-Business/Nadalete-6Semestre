package com.example.AtlazDB.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordem_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_os")
    private Long id;

    @Column(name = "numero_os")
    private String numeroOs;

    @Column(name = "tipo_servico")
    private String tipoServico;

    @Column(name = "local_destino")
    private String localDestino;

    @Column(name = "km_saida")
    private Double kmSaida;

    @Column(name = "km_chegada")
    private Double kmChegada;

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    @Column(name = "data_retorno")
    private LocalDateTime dataRetorno;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_viatura")
    private Viatura viatura;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroOs() {
        return numeroOs;
    }

    public void setNumeroOs(String numeroOs) {
        this.numeroOs = numeroOs;
    }

    public String getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(String tipoServico) {
        this.tipoServico = tipoServico;
    }

    public String getLocalDestino() {
        return localDestino;
    }

    public void setLocalDestino(String localDestino) {
        this.localDestino = localDestino;
    }

    public Double getKmSaida() {
        return kmSaida;
    }

    public void setKmSaida(Double kmSaida) {
        this.kmSaida = kmSaida;
    }

    public Double getKmChegada() {
        return kmChegada;
    }

    public void setKmChegada(Double kmChegada) {
        this.kmChegada = kmChegada;
    }

    public LocalDateTime getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
    }

    public LocalDateTime getDataRetorno() {
        return dataRetorno;
    }

    public void setDataRetorno(LocalDateTime dataRetorno) {
        this.dataRetorno = dataRetorno;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Viatura getViatura() {
        return viatura;
    }

    public void setViatura(Viatura viatura) {
        this.viatura = viatura;
    }
}
