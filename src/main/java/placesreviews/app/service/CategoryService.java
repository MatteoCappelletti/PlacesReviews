package placesreviews.app.service;

import jakarta.enterprise.context.ApplicationScoped;
import placesreviews.app.persistence.entity.Category;
import placesreviews.app.persistence.repository.CategoryRepository;

import java.util.List;
import java.util.Set;

@ApplicationScoped
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.listAll();
    }

    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    public Set<Category> findByNamesList(List<String> name) {
        return categoryRepository.findByNamesList(name);
    }
}
