package us.supercheng.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import us.supercheng.bo.ShopcartItemBO;
import us.supercheng.bo.UserBO;
import us.supercheng.enums.Sex;
import us.supercheng.mapper.UsersMapper;
import us.supercheng.pojo.Users;
import us.supercheng.service.UsersService;
import us.supercheng.utils.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private RedisOperator redisOperator;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUsernameExist(String username) {
        Example userExp = new Example(Users.class);
        Example.Criteria userCriteria = userExp.createCriteria();
        userCriteria.andEqualTo("username", username);
        Users users = this.usersMapper.selectOneByExample(userExp);
        return users != null;
    }

    @Transactional
    @Override
    public Users createUser(UserBO user) {
        Users ret = new Users();

        ret.setId(this.sid.nextShort());
        ret.setUsername(user.getUsername());
        ret.setNickname(user.getUsername());
        ret.setSex(Sex.Secret.type);
        ret.setFace("PLACEHOLDER");
        try {
            ret.setPassword(MD5Utils.getMD5Str(user.getPassword()));
        } catch (Exception ex) {}
        ret.setBirthday(DateUtil.stringToDate("1900-1-1"));
        Date now = new Date();
        ret.setCreatedTime(now);
        ret.setUpdatedTime(now);

        this.usersMapper.insert(ret);

        return ret;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users login(UserBO user) {
        Example userExp = new Example(Users.class);
        Example.Criteria criteria = userExp.createCriteria();

        criteria.andEqualTo("username", user.getUsername());
        try {
            criteria.andEqualTo("password", MD5Utils.getMD5Str(user.getPassword()));
        } catch (Exception ex) {
            throw new RuntimeException("MD5 String Conversion Exception");
        }

        return this.usersMapper.selectOneByExample(userExp);
    }

    @Transactional
    @Override
    public void syncShoppingCart(Users user, HttpServletRequest req, HttpServletResponse resp) {
        String key = "shopping_cart:" + user.getId(),
                redisStr = this.redisOperator.get(key),
                cookieStr = CookieUtils.getCookieValue(req, CookieUtils.SHOPCART_COOKIE_KEY, true),
                jsonStr = null;

        if (StringUtils.isBlank(redisStr)) {
            if (!StringUtils.isBlank(cookieStr)) {
                this.redisOperator.set(key, cookieStr);
            }
        } else {
            if (StringUtils.isBlank(cookieStr)) {
                CookieUtils.setCookie(req, resp, CookieUtils.SHOPCART_COOKIE_KEY, redisStr, true);
            } else {
                List<ShopcartItemBO> redisList= JsonUtils.jsonToList(redisStr, ShopcartItemBO.class),
                                     cookieList = JsonUtils.jsonToList(cookieStr, ShopcartItemBO.class),
                                     list = new ArrayList<>();

                Map<String, ShopcartItemBO> map = new HashMap<>();

                for (ShopcartItemBO each : redisList) {
                    String specId = each.getSpecId();
                    if (map.containsKey(specId)) {
                        ShopcartItemBO curr = map.get(specId);
                        curr.setBuyCounts(Math.max(curr.getBuyCounts(), each.getBuyCounts()));
                    } else
                        map.put(specId, each);
                }

                for (ShopcartItemBO each : cookieList) {
                    String specId = each.getSpecId();
                    if (map.containsKey(specId)) {
                        ShopcartItemBO curr = map.get(specId);
                        curr.setBuyCounts(Math.max(curr.getBuyCounts(), each.getBuyCounts()));
                    } else
                        map.put(specId, each);
                }


                for (Map.Entry<String, ShopcartItemBO> entry : map.entrySet())
                    list.add(entry.getValue());

                if (list.size() > 0) {
                    jsonStr = JsonUtils.objectToJson(list);
                    this.redisOperator.set(key, jsonStr);
                    CookieUtils.setCookie(req, resp, CookieUtils.SHOPCART_COOKIE_KEY, jsonStr, true);
                }
            }
        }
    }
}