package com.calinfo.api.common.tenant.controller;

import com.calinfo.api.common.tenant.service.TableDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by dalexis on 29/05/2018.
 */
@RequestMapping("/mock/tenant/domain")
@RestController
public class TableDomainController {

    @Autowired
    private TableDomainService tableDomainService;

    @PostMapping(value = "/{val}", produces = MediaType.APPLICATION_JSON_VALUE)
    public long create(@PathVariable("val") String name){
        return tableDomainService.create(name);
    }

    @GetMapping(value = "/{val}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Long> read(@PathVariable("val") String name){
        return tableDomainService.read(name);
    }

}
