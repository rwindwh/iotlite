package com.dj.iotlite.api;

import com.dj.iotlite.api.dto.*;
import com.dj.iotlite.api.form.*;
import com.dj.iotlite.entity.device.Device;
import com.dj.iotlite.entity.product.Product;
import com.dj.iotlite.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class ProductController extends BaseController {

    @Autowired
    DeviceService deviceService;

    @GetMapping("/list")
    public ResDto<Page<ProductListDto>> list(ProductQueryForm query) {

        Page<ProductListDto> ret = new Page<ProductListDto>();
        org.springframework.data.domain.Page<Product> res = deviceService.getProductList(query);
        res.forEach(s -> {
            ProductListDto t = new ProductListDto();
            BeanUtils.copyProperties(s, t);
            ret.getList().add(t);
        });
        ret.setTotal(res.getTotalElements());
        return success(ret);
    }

    @GetMapping("/deviceList")
    public ResDto<Page<ProductListDto>> deviceList(DeviceQueryForm query) {

        Page<DeviceListDto> ret = new Page<DeviceListDto>();
        org.springframework.data.domain.Page<Device> res = deviceService.getDeviceList(query);
        res.forEach(s -> {
            DeviceListDto t = new DeviceListDto();
            BeanUtils.copyProperties(s, t);
            deviceService.getProduct(s.getProductId(), t.getProduct());
            ret.getList().add(t);
        });
        ret.setTotal(res.getTotalElements());
        return success(ret);
    }

    @GetMapping("/all")
    public ResDto<List<OptionDto>> all() {
        List<OptionDto> ret = deviceService.getAllProduct();
        return success(ret);
    }

    @PostMapping("/remove")
    public ResDto<Boolean> remove(@RequestParam("uuid") String uuid) {
        return success(deviceService.removeProduct(uuid));
    }

    @PostMapping("/save")
    public ResDto<Boolean> save(@RequestBody ProductForm productForm) {
        return success(deviceService.saveProduct(productForm));
    }

    @GetMapping("/info")
    public ResDto<ProductDto> query(@RequestParam("id") Long id) {
        return success(deviceService.queryProduct(id));
    }

    @PostMapping("/saveModel")
    public ResDto<Boolean> saveModel(@RequestBody ProductSaveModelForm form) {
        return success(deviceService.saveModel(form));
    }


    @PostMapping("/refreshProductKey")
    public ResDto<Boolean> refreshProductKey(@RequestBody ProductRefreshProductKeyForm form) {
        return success(deviceService.refreshProductKey(form));
    }

    @PostMapping("/changeTags")
    public ResDto<Boolean> changeTags(@RequestBody ChangeTagsForm form) {
        return success(deviceService.changeTags(form));
    }
}
