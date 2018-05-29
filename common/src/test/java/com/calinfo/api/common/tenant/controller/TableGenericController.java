package com.calinfo.api.common.tenant.controller;

import com.calinfo.api.common.tenant.service.TableGenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dalexis on 29/05/2018.
 */
@RequestMapping("/mock/tenant/generic")
@RestController
public class TableGenericController {

    @Autowired
    private TableGenericService tableGenericService;

    @PostMapping(value = "/{val}", produces = MediaType.APPLICATION_JSON_VALUE)
    public long create(@PathVariable("val") String name){
        return tableGenericService.create(name);
    }

    @GetMapping(value = "/{val}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String read(@PathVariable("val") Long name){
        return tableGenericService.read(name);
    }

}
