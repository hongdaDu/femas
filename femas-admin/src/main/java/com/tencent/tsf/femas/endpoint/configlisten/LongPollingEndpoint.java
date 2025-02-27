package com.tencent.tsf.femas.endpoint.configlisten;

import com.tencent.tsf.femas.constant.IgnorePrefix;
import com.tencent.tsf.femas.endpoint.adaptor.AbstractBaseEndpoint;
import com.tencent.tsf.femas.entity.registry.ServiceApiRequest;
import com.tencent.tsf.femas.enums.ServiceInvokeEnum;
import com.tencent.tsf.femas.service.registry.ServiceManagerService;
import com.tencent.tsf.femas.service.rule.ConvertService;
import com.tencent.tsf.femas.storage.StorageResult;
import com.tencent.tsf.femas.storage.rocksdb.StringRawKVStoreManager;

import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author leoziltong、cody
 * @Description //TODO
 * @Date: 2021/4/16 14:39
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/atom/v1/sdk")
@IgnorePrefix
public class LongPollingEndpoint extends AbstractBaseEndpoint {

//    private final StringRawKVStoreManager kvStoreManager;

    private final ConvertService convertService;

    private final ServiceManagerService serviceManagerService;

    public LongPollingEndpoint(ConvertService convertService,
                               ServiceManagerService serviceManagerService) {
//        this.kvStoreManager = kvStoreManager;
        this.convertService = convertService;
        this.serviceManagerService = serviceManagerService;
    }

    @GetMapping("fetchData")
    public String fetchBreakerRule(String key) {
        return convertService.convert(key);
    }

//    @GetMapping("showData")
//    public HashMap<String, String> get() {
//        StorageResult<List<String>> result = kvStoreManager.scanPrefix("");
//        HashMap<String, String> stringStringHashMap = new HashMap<>();
//        if (result.getData() != null && result.getData().size() != 0) {
//            result.getData().forEach(s -> {
//                StorageResult<String> stringStorageResult = kvStoreManager.get(s);
//                stringStringHashMap.put(s, stringStorageResult.getData());
//            });
//        }
//        return stringStringHashMap;
//    }

    @PostMapping("/reportServiceApi")
    public void reportServiceApi(@RequestBody ServiceApiRequest serviceApiRequest) {
        if (log.isDebugEnabled()) {
            log.debug("ServiceApiRequest: {}", serviceApiRequest);
        }
        serviceManagerService.reportServiceApi(serviceApiRequest.getNamespaceId(),
                serviceApiRequest.getServiceName(), serviceApiRequest.getApplicationVersion(),
                serviceApiRequest.getData());
    }

    @PostMapping("/reportServiceEvent")
    public void reportServiceEvent(String namespaceId, String serviceName, String eventId, String data) {
        serviceManagerService.reportServiceEvent(namespaceId, serviceName, eventId, data);
    }


    /**
     * 服务不依赖控制台自动创建命名空间
     *
     * @param registryAddress
     * @param namespaceId
     */
    @PostMapping("initNamespace")
    public void initNamespace(String registryAddress, String namespaceId) {
        executor.invoke(ServiceInvokeEnum.ApiInvokeEnum.NAMESPACE_MANGER_INIT, registryAddress, namespaceId);
    }
}
