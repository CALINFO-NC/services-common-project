package com.calinfo.api.common.mocks;

import com.calinfo.api.common.dto.Dto;
import com.calinfo.api.common.dto.MessageInfoAndWarningInterface;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalexis on 20/11/2017.
 */
public class MockResponseResource implements Dto, MessageInfoAndWarningInterface {

    public static final String MESSAGE_INFO_TEST = "Ceci est un message d'information";

    public static final String MESSAGE_WARNING_TEST = "Ceci est un message d'alert";

    @Getter
    @Setter
    @Size(min = 0, max = 0)
    private List<String> listInfoMessages = new ArrayList<>();

    @Getter
    @Setter
    @Size(min = 0, max = 0)
    private List<String> listWarningMessages = new ArrayList<>();

}
