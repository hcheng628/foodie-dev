package us.supercheng.service.center.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import us.supercheng.bo.center.CenterUserBO;
import us.supercheng.mapper.UsersMapper;
import us.supercheng.pojo.Users;
import us.supercheng.service.center.CenterUsersService;

import java.util.Date;

@Service
public class CenterUsersServiceImpl implements CenterUsersService {

    @Autowired
    private UsersMapper usersMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users getUserByUserId(String userId) {
        Users users = new Users();
        users.setId(userId);
        return this.usersMapper.selectByPrimaryKey(users);
    }

    @Transactional
    @Override
    public Users update(String userId, CenterUserBO userBO) {
        Users u = new Users();

        BeanUtils.copyProperties(userBO, u);
        u.setId(userId);
        u.setUpdatedTime(new Date());

        int res = this.usersMapper.updateByPrimaryKeySelective(u);

        if (res != 1)
            throw new RuntimeException("No such user exist.");

        return this.getUserByUserId(userId);
    }

    @Transactional
    @Override
    public Users updateAvatarByUserId(String userId, String url) {
        Users u = new Users();

        u.setId(userId);
        u.setFace(url);
        u.setUpdatedTime(new Date());

        int res = this.usersMapper.updateByPrimaryKeySelective(u);

        if (res != 1)
            throw new RuntimeException("No such user exist.");

        return this.getUserByUserId(userId);
    }
}
