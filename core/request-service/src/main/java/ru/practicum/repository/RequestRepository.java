package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r " +
            "WHERE r.requesterId = :userId")
    List<Request> findByUserId(Long userId);

    @Query("SELECT r FROM Request r " +
            "WHERE r.eventId = :eventId")
    List<Request> findByEventId(Long eventId);

    @Query("SELECT r FROM Request r " +
            "WHERE r.eventId in :eventIds ")
    List<Request> findByEventIds(List<Long> eventIds);

    List<Request> findRequestByEventIdAndStatus(Long eventId, String status);

    @Query("SELECT COUNT(r) FROM Request r " +
            "WHERE r.eventId = :eventId " +
            "AND r.status in :statuses")
    Long countByEventAndStatuses(Long eventId, List<String> statuses);

    @Query("SELECT r FROM Request r " +
            "WHERE r.eventId in :eventIds " +
            "AND r.status = :status")
    List<Request> findByEventIdsAndStatus(List<Long> eventIds, String status);

    @Query("SELECT r FROM Request r " +
            "WHERE r.requesterId = :userId")
    List<Request> findByEventInitiatorId(Long userId);

    boolean existsByEventIdAndRequesterIdAndStatus(Long eventId, Long userId, String status);

}