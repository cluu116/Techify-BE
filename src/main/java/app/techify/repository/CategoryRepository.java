package app.techify.repository;

import app.techify.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByParentCategoryId(Integer parentCategoryId);
    List<Category> findAllByIsDeletedFalse();
    Optional<Category> findByIdAndIsDeletedFalse(Integer id);
}
