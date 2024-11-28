package com.hieptran.smarthome_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetaData {
    private int totalPage;

    private int currentPage;

    private int pageSize;

    private int totalItems;
}
