package com.example.atlazdb.service;

import static com.example.atlazdb.support.Fixtures.city;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.atlazdb.dto.CityRequestDTO;
import com.example.atlazdb.exception.ResourceNotFoundException;
import com.example.atlazdb.model.City;
import com.example.atlazdb.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock
    private CityRepository repository;

    @InjectMocks
    private CityService service;

    @Test
    void shouldListAllCities() {
        List<City> cities = List.of(city(1L), city(2L));
        when(repository.findAll()).thenReturn(cities);

        assertThat(service.listAll()).isEqualTo(cities);
    }

    @Test
    void shouldFindCityById() {
        City city = city(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(city));

        assertThat(service.findById(1L)).isSameAs(city);
    }

    @Test
    void shouldThrowNotFoundWhenCityDoesNotExist() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(9L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("City not found");
    }

    @Test
    void shouldCreateCityFromRequest() {
        when(repository.save(any(City.class))).thenAnswer(invocation -> invocation.getArgument(0));

        City saved = service.save(request("Taubate", "SP"));

        assertThat(saved).extracting(City::getName, City::getUf).containsExactly("Taubate", "SP");
    }

    @Test
    void shouldUpdateExistingCity() {
        City city = city(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(city));
        when(repository.save(city)).thenReturn(city);

        City updated = service.update(1L, request("Jacarei", "RJ"));

        assertThat(updated).extracting(City::getName, City::getUf).containsExactly("Jacarei", "RJ");
    }

    @Test
    void shouldDeleteExistingCity() {
        City city = city(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(city));

        service.delete(1L);

        verify(repository).delete(city);
    }

    @Test
    void shouldNotDeleteWhenCityDoesNotExist() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(9L)).isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).delete(any());
    }

    private static CityRequestDTO request(String name, String uf) {
        CityRequestDTO dto = new CityRequestDTO();
        dto.setName(name);
        dto.setUf(uf);
        return dto;
    }
}
