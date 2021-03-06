package com.dj.iotlite.service;

import com.dj.iotlite.RedisKey;
import com.dj.iotlite.entity.device.Device;
import com.dj.iotlite.entity.repo.DeviceRepository;
import com.dj.iotlite.entity.product.Product;
import com.dj.iotlite.entity.product.ProductRepository;
import com.dj.iotlite.enums.DirectionEnum;
import com.dj.iotlite.exception.BusinessException;
import com.dj.iotlite.push.PushService;
import com.dj.iotlite.utils.JsonUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class DeviceInstance implements DeviceModel {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    PushService pushService;

    @Autowired
    DeviceLogService deviceLogService;

    @Override
    public void setProductSn(String productSn) {

    }

    @Autowired
    MqttClient mqttClient;

    @Override
    public void setProperty(String productSn, String deviceSn, String property, Object value, String desc) {
        Map<String, Object> propertys = new HashMap<>();
        propertys.put(property, value);
        setPropertys(productSn, deviceSn, propertys, desc);
    }

    @Override
    public void setPropertys(String productSn, String deviceSn, Map<String, Object> propertys, String desc) {

        Product product = productRepository.findFirstBySn(productSn).orElseThrow(() -> {
            throw new BusinessException("产品序号不存在");
        });

        Device device = deviceRepository.findFirstBySnAndProductSn(deviceSn, productSn).orElseThrow(() -> {
            throw new BusinessException("设备序号不存在");
        });

        Gson gson = new Gson();
        String topic = String.format(RedisKey.DeviceProperty, "default", productSn, deviceSn);
        propertys.put("v", device.getVersion());
        String data = gson.toJson(propertys);

        device.setVersion(device.getVersion() + 1);
        deviceRepository.save(device);
        //写入下发日志
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(data.getBytes(StandardCharsets.UTF_8));
        deviceLogService.Log(deviceSn, productSn, DirectionEnum.Down, "admin", topic, desc, JsonUtils.toJson(propertys));
        try {
            log.info("topic {}  data: {}", topic, data);
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
            throw new BusinessException("下发设备信息异常");
        }
    }


    public void deviceResponse(String topic, MqttMessage msg) throws Exception {
        System.out.println(new String(msg.getPayload(), "UTF-8"));
        var seg = topic.split("/");
        var deviceSn = seg[3];
        var productSn = seg[2];
        var data = JsonUtils.toMap(new String(msg.getPayload(), "UTF-8"));
        //TODO 并发问题处理
        deviceLogService.Log(deviceSn, productSn, DirectionEnum.UP, "device", topic, "response", new String(msg.getPayload(), "UTF-8"));
        deviceRepository.findFirstBySnAndProductSn(deviceSn, productSn).ifPresent((d) -> {
            d.setVersion(((Double) data.get("v")).intValue());
            deviceRepository.save(d);
            //TODO 下发给所有的设备订阅者
            try {
                //找到设备对应的组 推送给设备组
                pushService.push("/device/"+productSn+"/"+deviceSn+"/"+"response",new String(msg.getPayload(), "UTF-8"));
            }catch (Exception e){
                log.info("推送给前端报错");
                e.printStackTrace();
            }
        });
    }
}
