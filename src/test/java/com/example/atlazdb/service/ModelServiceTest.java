package com.example.atlazdb.service;

import static com.example.atlazdb.support.Fixtures.model;
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

import com.example.atlazdb.dto.ModelRequestDTO;
import com.example.atlazdb.exception.ResourceNotFoundException;
import com.example.atlazdb.model.Model;
import com.example.atlazdb.repository.ModelRepository;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {

    @Mock
    private ModelRepository repository;

    @InjectMocks
    private ModelService service;

    @Test
    void shouldListAllModels() {
        List<Model> models = List.of(model(1L));
        when(repository.findAll()).thenReturn(models);

        assertThat(service.listAll()).isEqualTo(models);
    }

    @Test
    void shouldFindModelById() {
        Model model = model(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(model));

        assertThat(service.findById(1L)).isSameAs(model);
    }

    @Test
    void shouldThrowNotFoundWhenModelDoesNotExist() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(9L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Model not found");
    }

    @Test
    void shouldCreateModelFromRequest() {
        when(repository.save(any(Model.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Model saved = service.save(request("Hilux", "Toyota"));

        assertThat(saved).extracting(Model::getModelName, Model::getBrandName).containsExactly("Hilux", "Toyota");
    }

    @Test
    void shouldUpdateExistingModel() {
        Model model = model(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(model));
        when(repository.save(model)).thenReturn(model);

        Model updated = service.update(1L, request("Onix", "Chevrolet"));

        assertThat(updated).extracting(Model::getModelName, Model::getBrandName).containsExactly("Onix", "Chevrolet");
    }

    @Test
    void shouldDeleteExistingModel() {
        Model model = model(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(model));

        service.delete(1L);

        verify(repository).delete(model);
    }

    @Test
    void shouldNotDeleteWhenModelDoesNotExist() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(9L)).isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).delete(any());
    }

    private static ModelRequestDTO request(String modelName, String brandName) {
        ModelRequestDTO dto = new ModelRequestDTO();
        dto.setNameModel(modelName);
        dto.setNameBrand(brandName);
        return dto;
    }
}
