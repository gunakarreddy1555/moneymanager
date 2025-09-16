package Gunachattu.moneymanager.controller;

import Gunachattu.moneymanager.dto.CategoryDTO;
import Gunachattu.moneymanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO saveCategory= categoryService.saveCategory(categoryDTO);
        System.out.println("after save category");
        return ResponseEntity.status(HttpStatus.CREATED).body(saveCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        List<CategoryDTO> categories = categoryService.getCategoriesForCurrentProfile();
        return ResponseEntity.ok(categories);
    }
    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByTypeForCurrentUser(@PathVariable String type) {

        List<CategoryDTO> list = categoryService.getCategoriesByTypeForCurrentUser(type);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId,@RequestBody CategoryDTO categoryDTO) {

        CategoryDTO updateCategory = categoryService.updateCategory(categoryId, categoryDTO);

        return ResponseEntity.ok(updateCategory);
    }
}
