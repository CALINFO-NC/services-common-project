package com.calinfo.api.common.tenant.service;

import com.calinfo.api.common.tenant.entity.domain.TableDomainEntity;
import com.calinfo.api.common.tenant.repository.domain.TableDomainRepositoryDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalexis on 11/05/2018.
 */
@Service
@Transactional
public class TableDomainServiceImpl implements TableDomainService{

    @Autowired
    private TableDomainRepositoryDomain tableDomainRepository;

    public long create(String val){

        TableDomainEntity entityVal = new TableDomainEntity();
        entityVal.setPropDomain(val);
        entityVal = tableDomainRepository.save(entityVal);

        return entityVal.getId();
    }

    public List<Long> read(String val){
        List<TableDomainEntity> lstEntityVal = tableDomainRepository.findAllByPropDomain(val);

        List<Long> result = new ArrayList<>();
        for (TableDomainEntity item : lstEntityVal){
            result.add(item.getId());
        }

        return result;
    }
    
}
