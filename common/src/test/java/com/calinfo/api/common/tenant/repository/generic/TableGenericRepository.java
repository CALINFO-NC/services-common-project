package com.calinfo.api.common.tenant.repository.generic;

import com.calinfo.api.common.tenant.entity.generic.TableGenericEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TableGenericRepository extends JpaRepository<TableGenericEntity, Long> {
}
