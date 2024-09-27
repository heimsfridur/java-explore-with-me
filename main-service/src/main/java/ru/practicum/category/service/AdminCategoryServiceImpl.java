package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.NotUniqueException;

@AllArgsConstructor
@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        validateCategoryName(newCategoryDto.getName());
        Category savedCategory = categoryRepository.save(CategoryMapper.toCategoryFromNewCategoryDto(newCategoryDto));
        return CategoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    public void delete(int catId) {
        validateById(catId);

        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException(String.format("Category with id %d contains events. Impossible to delete it", catId));
        }

        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto, int catId) {
        Category category = getCategoryById(catId);

        String newName = categoryDto.getName();
        if (!category.getName().equals(newName)) {
            validateCategoryName(newName);
            category.setName(categoryDto.getName());
        }

        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    private void validateById(int id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(String.format("Category with id %d is not found.", id));
        }
    }

    private Category getCategoryById(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id %d is not found.", id)));
    }

    private void validateCategoryName(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new NotUniqueException("Category name is not unique.");
        }
    }

}
