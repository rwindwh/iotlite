package com.dj.iotlite.api.form;

import com.dj.iotlite.api.dto.DeviceListDto;
import lombok.Data;

@Data
public class DeviceGroupSaveForm extends DeviceListDto {
    Long id;
    String name;
    String description;
}
