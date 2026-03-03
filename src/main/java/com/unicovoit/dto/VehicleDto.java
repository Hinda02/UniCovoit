package com.unicovoit.dto;

import jakarta.validation.constraints.*;

public class VehicleDto {

    @NotBlank(message = "La marque est obligatoire")
    @Size(max = 100, message = "La marque ne doit pas dépasser 100 caractères")
    private String brand;

    @NotBlank(message = "Le modèle est obligatoire")
    @Size(max = 100, message = "Le modèle ne doit pas dépasser 100 caractères")
    private String model;

    @Size(max = 50, message = "La couleur ne doit pas dépasser 50 caractères")
    private String color;

    @Size(max = 50, message = "La plaque ne doit pas dépasser 50 caractères")
    private String plateNumber;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Le nombre de places doit être au moins 1")
    @Max(value = 8, message = "Le nombre de places ne doit pas dépasser 8")
    private Integer seatsTotal;

    public VehicleDto() {}

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Integer getSeatsTotal() {
        return seatsTotal;
    }

    public void setSeatsTotal(Integer seatsTotal) {
        this.seatsTotal = seatsTotal;
    }
}
