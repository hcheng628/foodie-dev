package us.supercheng.api.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.supercheng.utils.APIResponse;
import us.supercheng.utils.RedisOperator;

@RestController
@RequestMapping("redis_test")
public class RedisTestController {

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("get")
    public APIResponse getValue(String key) {
        if (StringUtils.isBlank(key))
            return APIResponse.ok();

        return APIResponse.ok(redisOperator.get(key));
    }

    @PostMapping("set")
    public APIResponse setValue(String key, String value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value))
            return APIResponse.errorMsg("Empty key and/or value");

        this.redisOperator.set(key, value);
        return APIResponse.ok();
    }

    @DeleteMapping("del")
    public APIResponse delValue(String key) {
        if (StringUtils.isBlank(key))
            return APIResponse.ok();

        if (!this.redisOperator.del(key))
            return APIResponse.errorMsg("Could not delete key: " + key);
        return APIResponse.ok();
    }
}