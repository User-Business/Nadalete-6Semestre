package com.example.atlazdb.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.atlazdb.dto.CityRequestDTO;
import com.example.atlazdb.exception.ResourceNotFoundException;
import com.example.atlazdb.model.City;
import com.example.atlazdb.repository.CityRepository;

@Service
public class CityService {

    private final CityRepository repository;

    public CityService(CityRepository repository) {
        this.repository = repository;
    }

    public List<City> listAll() {
        return repository.findAll();
    }

    public City findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found"));
    }

    public City save(CityRequestDTO dto) {
        City city = new City();
        apply(city, dto);
        return repository.save(city);
    }

    public City update(Long id, CityRequestDTO dto) {
        City city = findById(id);
        apply(city, dto);
        return repository.save(city);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void apply(City city, CityRequestDTO dto) {
        city.setName(dto.getName());
        city.setUf(dto.getUf());
    }
}
