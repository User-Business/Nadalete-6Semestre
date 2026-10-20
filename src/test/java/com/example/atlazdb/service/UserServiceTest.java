package com.example.atlazdb.service;

import static com.example.atlazdb.support.Fixtures.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.atlazdb.dto.UserRequestDTO;
import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.Profile;
import com.example.atlazdb.enums.UserStatus;
import com.example.atlazdb.exception.BusinessRuleException;
import com.example.atlazdb.exception.ResourceNotFoundException;
import com.example.atlazdb.model.User;
import com.example.atlazdb.repository.ServiceOrderRepository;
import com.example.atlazdb.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @InjectMocks
    private UserService service;

    @Test
    void shouldListAllUsers() {
        List<User> users = List.of(user(1L));
        when(repository.findAll()).thenReturn(users);

        assertThat(service.listAll()).isEqualTo(users);
    }

    @Test
    void shouldThrowNotFoundWhenUserDoesNotExist() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(9L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void shouldCreateUserAsAvailableByDefault() {
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = service.save(request(null));

        assertThat(saved).extracting(User::getName, User::getRegistration, User::getEmail, User::getPasswordHash,
                        User::getProfile, User::getUserStatus, User::getCnhTypes)
                .containsExactly("Mary", "M-01", "mary@atlaz.com", "hashed", Profile.ADMIN, UserStatus.DISPONIVEL,
                        Set.of(CnhType.B));
    }

    @Test
    void shouldCreateUserWithInformedStatus() {
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = service.save(request(UserStatus.EM_CAMPO));

        assertThat(saved.getUserStatus()).isEqualTo(UserStatus.EM_CAMPO);
    }

    @Test
    void shouldUpdateUserKeepingStatusWhenNotInformed() {
        User user = user(1L);
        user.setUserStatus(UserStatus.EM_CAMPO);
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(user);

        User updated = service.update(1L, request(null));

        assertThat(updated.getName()).isEqualTo("Mary");
        assertThat(updated.getUserStatus()).isEqualTo(UserStatus.EM_CAMPO);
        verifyNoInteractions(serviceOrderRepository);
    }

    @Test
    void shouldDeactivateUserWithoutTripInProgress() {
        User user = user(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(serviceOrderRepository.existsByUserIdAndReturnDateIsNull(1L)).thenReturn(false);
        when(repository.save(user)).thenReturn(user);

        User updated = service.update(1L, request(UserStatus.DESLIGADO));

        assertThat(updated.getUserStatus()).isEqualTo(UserStatus.DESLIGADO);
    }

    @Test
    void shouldNotDeactivateUserWithTripInProgress() {
        when(repository.findById(1L)).thenReturn(Optional.of(user(1L)));
        when(serviceOrderRepository.existsByUserIdAndReturnDateIsNull(1L)).thenReturn(true);
        UserRequestDTO request = request(UserStatus.DESLIGADO);

        assertThatThrownBy(() -> service.update(1L, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Ordem de Serviço em andamento");
        verify(repository, never()).save(any());
    }

    @Test
    void shouldDeleteExistingUser() {
        User user = user(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        service.delete(1L);

        verify(repository).delete(user);
    }

    private static UserRequestDTO request(UserStatus status) {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("Mary");
        dto.setRegistrationNumber("M-01");
        dto.setEmail("mary@atlaz.com");
        dto.setPasswordHash("hashed");
        dto.setProfile(Profile.ADMIN);
        dto.setUserStatus(status);
        dto.setCnhTypes(Set.of(CnhType.B));
        return dto;
    }
}
