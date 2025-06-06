package ru.practicum.categories.service;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.categories.model.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto getCategory(Long catId);

    List<CategoryDto> getCategories(int from, int size);

    void deleteCategory(Long catId);

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(NewCategoryDto newCategoryDto, Long catId);

    Category getCategoryByIdNotMapping(Long id);
}
