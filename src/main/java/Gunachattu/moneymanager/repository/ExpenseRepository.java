package Gunachattu.moneymanager.repository;

import Gunachattu.moneymanager.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    // ✅ Get all expenses for a profile, ordered by date desc
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    // ✅ Get last 5 expenses
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    // ✅ Sum of all expenses
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    // ✅ Search expenses by profileId, date range & keyword
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort);

    // ✅ Expenses by profile and date range
    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);
}
