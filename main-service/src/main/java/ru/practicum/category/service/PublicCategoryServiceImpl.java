package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exceptions.NotFoundException;

import java.util.List;

@AllArgsConstructor
@Service
public class PublicCategoryServiceImpl implements PublicCategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getAll(int from, int size) {

        Pageable pageable = PageRequest.of(from / size, size);

        List<Category> categories = categoryRepository.findAll(pageable).getContent();

        return categories.stream().map(CategoryMapper::toCategoryDto).toList();
    }

    @Override
    public CategoryDto getById(int catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Category with id %d is not found.", catId));
        }

        return CategoryMapper.toCategoryDto(categoryRepository.findById(catId).get());
    }
}
