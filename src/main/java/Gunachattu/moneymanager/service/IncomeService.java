package Gunachattu.moneymanager.service;


import Gunachattu.moneymanager.dto.IncomeDTO;
import Gunachattu.moneymanager.entity.CategoryEntity;

import Gunachattu.moneymanager.entity.IncomeEntity;
import Gunachattu.moneymanager.entity.ProfileEntity;
import Gunachattu.moneymanager.repository.CategoryRepository;
import Gunachattu.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    //add a new Expense to database
    public IncomeDTO addIncome(IncomeDTO dto) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        IncomeEntity newExpense = toEntity(dto, profile, category);
        newExpense = incomeRepository.save(newExpense);
        return toDTO(newExpense);

    }
//    helper Methods

    private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category){
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIocn())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();

    }

    private IncomeDTO toDTO(IncomeEntity entity){
        return IncomeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .iocn(entity.getIcon())
                .categoryId(entity.getCategory()!=null ? entity.getCategory().getId():null)
                .categoryName(entity.getCategory() !=null ? entity.getCategory().getName():"N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

    }
}
