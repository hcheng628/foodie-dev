package us.supercheng.service.center;

import us.supercheng.bo.center.CenterUserBO;
import us.supercheng.pojo.Users;

public interface CenterUsersService {
    Users getUserByUserId(String userId);
    Users update(String userId, CenterUserBO userBO);
    Users updateAvatarByUserId(String userId, String url);
}