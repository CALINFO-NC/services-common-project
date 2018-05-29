package com.calinfo.api.common.tenant.repository.domain;

import com.calinfo.api.common.tenant.entity.domain.TableDomainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TableDomainRepositoryDomain extends JpaRepository<TableDomainEntity, Long>{

    List<TableDomainEntity> findAllByPropDomain(String propDomain);
}
