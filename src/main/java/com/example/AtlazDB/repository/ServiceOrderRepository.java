package com.example.AtlazDB.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.AtlazDB.model.ServiceOrder;
import com.example.AtlazDB.repository.projection.AtividadeProjection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    @Query("SELECT o FROM ServiceOrder o WHERE o.departureDate >= :start AND o.departureDate < :end")
    List<ServiceOrder> findByPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    
      @Query(value = """
        SELECT 
            os.id_os            AS id,
            'DESLOCAMENTO'     AS tipo,
            v.prefixo           AS prefixoViatura,
            u.nome              AS nomeTecnico,
            os.local_destino    AS descricao,
            'CONCLUÍDO'         AS status,
            'EMERALD'           AS corStatus
        FROM ordem_servico os
        JOIN viatura v  ON v.id_viatura = os.id_viatura
        JOIN usuario u  ON u.id_usuario = os.id_usuario
        WHERE DATE(os.data_saida) = CURRENT_DATE
        """, nativeQuery = true)
    List<AtividadeProjection> findOrdensDeHoje();

    ServiceOrder findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(Long viaturaId);
}