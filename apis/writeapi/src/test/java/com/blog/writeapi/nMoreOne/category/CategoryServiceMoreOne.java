package com.blog.writeapi.nMoreOne.category;

import com.blog.writeapi.configs.TestContainerConfig;
import com.blog.writeapi.modules.category.models.CategoryModel;
import com.blog.writeapi.modules.category.repository.CategoryRepository;
import com.blog.writeapi.modules.category.service.providers.CategoryService;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
public class CategoryServiceMoreOne {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    CategoryModel category = new CategoryModel().toBuilder()
            .id(1998780200074176609L)
            .name("TI")
            .description("Any Desc")
            .slug("ti")
            .isActive(true)
            .visible(true)
            .displayOrder(1)
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldQueryDatabaseOnceAndThenUseCache() {
        CategoryModel category = categoryRepository.save(this.category);

        Session session = entityManager.unwrap(Session.class);
        Statistics stats = session.getSessionFactory().getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        categoryService.getByIdSimple(category.getId());
        assertEquals(1, stats.getEntityLoadCount(), "Primeira chamada deve carregar do banco");

        categoryService.getByIdSimple(category.getId());
        assertEquals(1, stats.getEntityLoadCount(), "Segunda chamada não deve tocar no banco (Cache hit)");

        categoryService.getByIdSimple(category.getId());
        assertEquals(1, stats.getEntityLoadCount(), "Terceira chamada ainda deve vir do cache");
    }
}