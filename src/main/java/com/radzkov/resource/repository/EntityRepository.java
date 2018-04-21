package com.radzkov.resource.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author Radzkov Andrey
 */
@NoRepositoryBean
public interface EntityRepository<T> extends CrudRepository<T, Long>, JpaSpecificationExecutor<T> {

}
