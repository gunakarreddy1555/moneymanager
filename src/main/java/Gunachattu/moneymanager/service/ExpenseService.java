package Gunachattu.moneymanager.service;

import Gunachattu.moneymanager.dto.ExpenseDTO;
import Gunachattu.moneymanager.entity.CategoryEntity;
import Gunachattu.moneymanager.entity.ExpenseEntity;
import Gunachattu.moneymanager.entity.ProfileEntity;
import Gunachattu.moneymanager.repository.CategoryRepository;
import Gunachattu.moneymanager.repository.ExpenseRepository;
import Gunachattu.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryService categoryService;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;
    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;

    //add a new Expense to database
    public ExpenseDTO addExpense(ExpenseDTO dto) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        ExpenseEntity newExpense = toEntity(dto, profile, category);
        newExpense = expenseRepository.save(newExpense);
        return toDTO(newExpense);

    }


    //    helper Methods
    private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile, CategoryEntity category) {
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIocn())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();

    }

    private ExpenseDTO toDTO(ExpenseEntity entity) {
        return ExpenseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .iocn(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

    }

//give a method to get total expenses of current month
}
