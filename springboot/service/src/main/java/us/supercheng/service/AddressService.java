package us.supercheng.service;

import us.supercheng.pojo.UserAddress;
import java.util.List;

public interface AddressService {
    boolean setUserDefaultShippingAddr(String userId, String addressId);
    List<UserAddress> getUserAddresses(String userId);
    boolean createAddr(UserAddress addr);
    boolean updateAddr(UserAddress addr);
    boolean deleteUserAddr(String userId, String addressId);
    UserAddress getUserAddressByUserIdAndAddressId(String userId, String addressId);
}
