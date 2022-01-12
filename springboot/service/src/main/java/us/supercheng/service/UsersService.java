package us.supercheng.service;

import us.supercheng.bo.UserBO;
import us.supercheng.pojo.Users;
import us.supercheng.vo.UsersVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UsersService {
    boolean isUsernameExist(String username);
    UsersVO createUser(UserBO user, HttpServletRequest req, HttpServletResponse resp);
    UsersVO login(UserBO user, HttpServletRequest req, HttpServletResponse resp);
    Users validateLogin(String username, String password);
    String createUserSession(Users users);
    boolean hasUserSession(String userId, String userToken);
    void delUserSession(String userId);
    void syncShoppingCart(String userId, HttpServletRequest req, HttpServletResponse resp);
    void removeUsersPII(Users users);
    UsersVO convertUsersToUsersVOWithToken(Users users, String token);
}