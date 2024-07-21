package com.moment.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class ResultDTO<D> {

    private final int code;
    private final String message;
    private final D data;
}