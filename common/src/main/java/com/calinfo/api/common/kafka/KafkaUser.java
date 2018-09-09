package com.calinfo.api.common.kafka;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class KafkaUser {

    private String login;

    private List<String> roles = new ArrayList<>();

    private boolean systemUser;

    private boolean anonymousUser;
}
