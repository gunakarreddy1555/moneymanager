package Gunachattu.moneymanager.controller;

import Gunachattu.moneymanager.dto.ExpenseDTO;
import Gunachattu.moneymanager.dto.IncomeDTO;
import Gunachattu.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    public ResponseEntity<IncomeDTO> addExpense(@RequestBody IncomeDTO dto) {
        IncomeDTO saved = incomeService.addIncome(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
