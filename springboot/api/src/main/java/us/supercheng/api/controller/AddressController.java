package us.supercheng.api.controller;

import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.supercheng.bo.AddressBO;
import us.supercheng.pojo.UserAddress;
import us.supercheng.service.AddressService;
import us.supercheng.utils.APIResponse;
import java.util.Date;

@RestController
@RequestMapping("address")
public class AddressController {

    @Autowired
    private Sid sid;

    @Autowired
    private AddressService addressService;


    @PostMapping("setDefalut")
    public APIResponse setUserDefaultShippingAddr(@RequestParam String userId,
                                                  @RequestParam String addressId) {
        if (userId == null || addressId == null)
            return APIResponse.errorMsg("Missing User ID and/or Address ID");

        if (this.addressService.setUserDefaultShippingAddr(userId, addressId)) {
            return APIResponse.ok();
        } else {
            return APIResponse.errorMsg("Delete Address Failed");
        }
    }

    @PostMapping("list")
    public APIResponse getUserAddresses(@RequestParam String userId) {
        if (StringUtils.isBlank(userId))
            return APIResponse.errorMsg("Missing User ID in Request");

        return APIResponse.ok(this.addressService.getUserAddresses(userId));
    }

    @PostMapping("add")
    @ResponseBody
    public APIResponse createAddr(@RequestBody AddressBO addressBO) {
        if (addressBO == null)
            return APIResponse.errorMsg("Missing required request Address content");

        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, userAddress);
        userAddress.setId(this.sid.nextShort());
        Date now = new Date();
        userAddress.setCreatedTime(now);
        userAddress.setUpdatedTime(now);

        if (this.addressService.createAddr(userAddress)) {
            return APIResponse.ok(userAddress);
        } else {
            return APIResponse.ok("Create Address Failed");
        }
    }

    @PostMapping("update")
    public APIResponse updateAddr(@RequestBody AddressBO addressBO) {
        if (addressBO == null)
            return APIResponse.errorMsg("Missing required request Address content");

        String addrId = addressBO.getAddressId();
        if (StringUtils.isBlank(addrId))
            return APIResponse.errorMsg("Missing required attribute Address ID");

        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, userAddress);
        userAddress.setId(addrId);
        userAddress.setUpdatedTime(new Date());

        if (this.addressService.updateAddr(userAddress)) {
            return APIResponse.ok(userAddress);
        } else {
            return APIResponse.errorMsg("Update Address Failed");
        }
    }


    @PostMapping("delete")
    public APIResponse deleteUserAddr(@RequestParam String userId,
                                      @RequestParam String addressId) {
        if (userId == null || addressId == null)
            return APIResponse.errorMsg("Missing User ID and/or Address ID");

        if (this.addressService.deleteUserAddr(userId, addressId)) {
            return APIResponse.ok();
        } else {
            return APIResponse.errorMsg("Delete Address Failed");
        }
    }
}