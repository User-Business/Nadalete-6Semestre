package com.example.AtlazDB.model;

import jakarta.persistence.*;
import com.example.AtlazDB.enums.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "viatura")
@Data
@Getter
@Setter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_viatura")
    private Long id;

    @Column(name = "prefixo")
    private String prefix;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @Column(name = "viatura_status")
    @Enumerated(EnumType.STRING)
    private VehicleStatus vehicleStatus;

    @ManyToOne
    @JoinColumn(name = "id_modelo")
    private Model model;
    
    @Column(name = "tipo_abastecimento", nullable = false)
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Column(name = "km_atual")
    private Double km;


    public Long getId() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public VehicleStatus getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(VehicleStatus vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public Double getKm() {
        return km;
    }

    public void setKm(Double km) {
        this.km = km;
    }

}