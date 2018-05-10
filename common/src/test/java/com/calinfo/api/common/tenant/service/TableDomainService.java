package com.calinfo.api.common.tenant.service;

import java.util.List;

/**
 * Created by dalexis on 11/05/2018.
 */
public interface TableDomainService  {

    long create(String val);

    List<Long> read(String val);
}
