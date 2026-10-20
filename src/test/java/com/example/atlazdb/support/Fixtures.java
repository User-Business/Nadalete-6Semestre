package com.example.atlazdb.support;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.FuelType;
import com.example.atlazdb.enums.OccurrenceType;
import com.example.atlazdb.enums.Profile;
import com.example.atlazdb.enums.UserStatus;
import com.example.atlazdb.enums.VehicleStatus;
import com.example.atlazdb.enums.VehicleType;
import com.example.atlazdb.model.City;
import com.example.atlazdb.model.Model;
import com.example.atlazdb.model.Refueling;
import com.example.atlazdb.model.ServiceOrder;
import com.example.atlazdb.model.User;
import com.example.atlazdb.model.Vehicle;

/**
 * Builders of domain objects with controlled values shared by the unit tests.
 */
public final class Fixtures {

    public static final LocalDateTime DEPARTURE_DATE = LocalDateTime.of(2026, 4, 2, 8, 0);

    private Fixtures() {
    }

    public static User user(Long id, CnhType... licenses) {
        User user = new User();
        user.setId(id);
        user.setName("John Tech");
        user.setRegistration("REG-" + id);
        user.setEmail("john" + id + "@atlaz.com");
        user.setPasswordHash("hash-" + id);
        user.setProfile(Profile.TECNICO);
        user.setUserStatus(UserStatus.DISPONIVEL);
        Set<CnhType> types = EnumSet.noneOf(CnhType.class);
        types.addAll(Set.of(licenses));
        user.setCnhTypes(types);
        return user;
    }

    public static Model model(Long id) {
        Model model = new Model();
        model.setId(id);
        model.setModelName("Prisma");
        model.setBrandName("Chevrolet");
        return model;
    }

    public static Vehicle vehicle(Long id, VehicleStatus status, CnhType requiredLicense) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setPrefix("US0" + id);
        vehicle.setType(VehicleType.UTILITARIO);
        vehicle.setFuelType(FuelType.FLEX);
        vehicle.setVehicleStatus(status);
        vehicle.setTipoCnhNecessaria(requiredLicense);
        vehicle.setModel(model(10L));
        vehicle.setKm(1000.0);
        vehicle.setKmTrocaOleo(new BigDecimal("11000"));
        return vehicle;
    }

    public static City city(Long id) {
        City city = new City();
        city.setId(id);
        city.setName("Sao Jose dos Campos");
        city.setUf("SP");
        return city;
    }

    public static ServiceOrder serviceOrder(Long id, String departureKm, String arrivalKm) {
        ServiceOrder order = new ServiceOrder();
        order.setId(id);
        order.setServiceType(OccurrenceType.ADMINISTRATIVO);
        order.setDestinationLocation("Taubate");
        order.setRequester("Requester");
        order.setDepartureDate(DEPARTURE_DATE);
        order.setDepartureKm(departureKm == null ? null : new BigDecimal(departureKm));
        order.setArrivalKm(arrivalKm == null ? null : new BigDecimal(arrivalKm));
        order.setUser(user(1L, CnhType.B));
        order.setVehicle(vehicle(2L, VehicleStatus.EM_USO, CnhType.B));
        return order;
    }

    public static Refueling refueling(Long id, ServiceOrder order, String liters) {
        Refueling refueling = new Refueling();
        refueling.setId(id);
        refueling.setServiceOrder(order);
        refueling.setLiters(liters == null ? null : new BigDecimal(liters));
        refueling.setTotalValue(new BigDecimal("150.00"));
        refueling.setReceiptNumber("NF-" + id);
        refueling.setDateTime(DEPARTURE_DATE);
        refueling.setVehicle(vehicle(2L, VehicleStatus.EM_USO, CnhType.B));
        return refueling;
    }
}
