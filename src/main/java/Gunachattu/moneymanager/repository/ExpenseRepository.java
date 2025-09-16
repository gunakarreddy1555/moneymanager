package Gunachattu.moneymanager.repository;

import Gunachattu.moneymanager.entity.ExpenseEntity;
import Gunachattu.moneymanager.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

//  // select * from tbl_expenses where profile_id=? order by date desc
//  List<IncomeEntity> findByProfileIdOrderByIncomeDateDesc(Long profileId);
//
//
//  // select * from tbl_expenses where profile_id=? order by date desc limit 5
//  List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);
//
//  @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id = :profileId")
//  BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);
//
//  // select * from tbl_expenses where profile_id = ?1 and date between ?2 and ?3 and name like %?4% order by date desc
//  List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
//          Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort);
//
//  List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);
}

