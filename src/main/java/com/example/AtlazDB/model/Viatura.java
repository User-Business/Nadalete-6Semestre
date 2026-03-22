package com.example.AtlazDB.model;

import jakarta.persistence.*;
import com.example.AtlazDB.enums.*;

@Entity
@Table(name = "viatura")
public class Viatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_viatura")
    private Long id;

    private String prefixo;

    @Enumerated(EnumType.STRING)
    private TipoViatura tipo;

    @Column(name = "viatura_status")
    @Enumerated(EnumType.STRING)
    private ViaturaStatus viaturaStatus;

    @ManyToOne
    @JoinColumn(name = "id_modelo")
    private Modelo modelo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrefixo() {
        return prefixo;
    }

    public void setPrefixo(String prefixo) {
        this.prefixo = prefixo;
    }

    public TipoViatura getTipo() {
        return tipo;
    }

    public void setTipo(TipoViatura tipo) {
        this.tipo = tipo;
    }

    public ViaturaStatus getViaturaStatus() {
        return viaturaStatus;
    }

    public void setViaturaStatus(ViaturaStatus viaturaStatus) {
        this.viaturaStatus = viaturaStatus;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }
}
