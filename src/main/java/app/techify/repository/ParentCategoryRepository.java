package app.techify.repository;

import app.techify.entity.ParentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentCategoryRepository extends JpaRepository<ParentCategory, Integer> {
        List<ParentCategory> findAllByIsDeletedFalse();
        Optional<ParentCategory> findByIdAndIsDeletedFalse(Integer id);
}
