package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

public interface AdminCategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    void delete(int catId);

    CategoryDto update(CategoryDto categoryDto, int catId);
}
