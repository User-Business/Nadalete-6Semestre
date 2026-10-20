package com.example.atlazdb.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.atlazdb.dto.ModelRequestDTO;
import com.example.atlazdb.exception.ResourceNotFoundException;
import com.example.atlazdb.model.Model;
import com.example.atlazdb.repository.ModelRepository;

@Service
public class ModelService {

    private final ModelRepository repository;

    public ModelService(ModelRepository repository) {
        this.repository = repository;
    }

    public List<Model> listAll() {
        return repository.findAll();
    }

    public Model findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Model not found"));
    }

    public Model save(ModelRequestDTO dto) {
        Model model = new Model();
        apply(model, dto);
        return repository.save(model);
    }

    public Model update(Long id, ModelRequestDTO dto) {
        Model model = findById(id);
        apply(model, dto);
        return repository.save(model);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void apply(Model model, ModelRequestDTO dto) {
        model.setModelName(dto.getNameModel());
        model.setBrandName(dto.getNameBrand());
    }
}
