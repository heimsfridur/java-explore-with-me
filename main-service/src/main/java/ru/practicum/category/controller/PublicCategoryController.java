package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.PublicCategoryService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/categories")
public class PublicCategoryController {

    public final PublicCategoryService publicCategoryService;

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        return publicCategoryService.getAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getAll(@PathVariable int catId) {
        return publicCategoryService.getById(catId);
    }
}
