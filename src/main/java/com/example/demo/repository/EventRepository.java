package com.example.demo.repository;

import com.example.demo.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    List<Event> findByUserId(String userId);

    List<Event> findByUserIdOrderByTimestampDesc(String userId);

    List<Event> findByService(String service);

    List<Event> findByEventType(String eventType);

    @Query("SELECT e FROM Event e WHERE e.userId = :userId AND e.timestamp >= :since ORDER BY e.timestamp DESC")
    List<Event> findRecentEventsByUser(@Param("userId") String userId, @Param("since") LocalDateTime since);

    @Query("SELECT e FROM Event e ORDER BY e.timestamp DESC")
    List<Event> findAllOrderByTimestampDesc();

    @Query("SELECT e FROM Event e WHERE e.timestamp >= :since ORDER BY e.timestamp DESC")
    List<Event> findRecentEvents(@Param("since") LocalDateTime since);

    /**
     * Son 15 dakika içindeki PAYMENT event'lerini sayar
     * RR-03 kuralı için: payments_15min_count >= 2
     */
    @Query("SELECT COUNT(e) FROM Event e WHERE e.userId = :userId AND e.service = :service " +
           "AND e.eventType = 'PAYMENT' AND e.timestamp >= :since")
    long countPaymentEventsInTimeWindow(@Param("userId") String userId,
                                        @Param("service") String service,
                                        @Param("since") LocalDateTime since);
}