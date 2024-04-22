package com.hd.usercenter.service;

import com.hd.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.hd.usercenter.contant.UserConstant.ADMIN_ROLE;
import static com.hd.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务
 */
public interface UserService extends IService<User> {


    /**
     * 用户注册
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword 校验密码
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userPassword
     * @return 返回脱敏后的账户信息
     */
    User doLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    User getSafetyUser(User user);

    /**
     * 请求用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签查用户
     *
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 用户更新
     *
     * @param user
     * @param loginUser
     * @return
     */
    Integer updateUser(User user, User loginUser);

    /**
     * 获取当前登录用户信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     *验证是否为管理员
     * @param loginUser
     * @return
     */
    Boolean isAdmin(User loginUser);

    /**
     * 验证是否为管理员
     * @param request
     * @return
     */
    Boolean isAdmin(HttpServletRequest request);

    List<User> matchUsers(long num, User user);

}
