package com.calinfo.api.common.tenant.service;

import com.calinfo.api.common.tenant.entity.generic.TableGenericEntity;
import com.calinfo.api.common.tenant.repository.generic.TableGenericRepositoryGeneric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


/**
 * Created by dalexis on 11/05/2018.
 */
@Service
@Transactional
public class TableGenericServiceImpl implements TableGenericService{

    @Autowired
    private TableGenericRepositoryGeneric tableGenericRepository;

    public long create(String val){

        TableGenericEntity tableGenericEntity = new TableGenericEntity();
        tableGenericEntity.setPropGeneric(val);
        tableGenericEntity = tableGenericRepository.save(tableGenericEntity);

        return tableGenericEntity.getId();
    }

    public String read(Long id){

        Optional<TableGenericEntity> optionalTableGenericEntity = tableGenericRepository.findById(id);
        if (!optionalTableGenericEntity.isPresent())
            return null;

        return optionalTableGenericEntity.get().getPropGeneric();
    }


    
}
