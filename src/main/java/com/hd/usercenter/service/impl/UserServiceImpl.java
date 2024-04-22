package com.hd.usercenter.service.impl;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hd.usercenter.Utils.AlgorithmUtils;
import com.hd.usercenter.common.ErrorCode;
import com.hd.usercenter.exception.BusinessException;
import com.hd.usercenter.model.domain.User;
import com.hd.usercenter.service.UserService;
import com.hd.usercenter.mapper.UserMapper;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.hd.usercenter.contant.UserConstant.ADMIN_ROLE;
import static com.hd.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值
     */
    private static final String SALT = "hd";



    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
//        1.校验（工具类）
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }

//        校验账户不能包含特殊字符
//        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
//        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
//        if (matcher.find()){
//            return -1;
//        }
        //        账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
//        密码和校验密码(字符串要用equals)
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
//      2.加密密码
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
//      3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }

        return user.getId();
    }

    @Override
    public Boolean isAdmin(HttpServletRequest request) {
            Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
            User user = (User) userObj;
            if (user == null || user.getUserRole() != ADMIN_ROLE){
                return false;
            }
            return true;
    }



    /**
     * 支持User
     * @param loginUser
     * @return
     */
    @Override
    public Boolean isAdmin(User loginUser) {
            if (loginUser == null || loginUser.getUserRole() != ADMIN_ROLE){
                return false;
            }
            return true;
    }

    /**
     * 更新信息
     * @param user
     * @param loginUser
     * @return
     */
    @Override
    public Integer updateUser(User user, User loginUser) {
//        必须要传id
        Long userId = user.getId();
        if (userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        如果管理员， 可以更改任意用户
//        非管理员，只能更改自己的信息
        if (userId != loginUser.getId() && !isAdmin(loginUser) ) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User originUser = userMapper.selectById(userId);
        if (originUser == null ) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null ){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }

    /**
     * 推荐匹配用户
     * @param num
     * @param loginUser
     * @return
     */
    @Override
    public List<User> matchUsers(long num, User loginUser) {

//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.last("limit 50000");
//        List<User> userList = this.list(queryWrapper);

//         或者用page分页查询，自己输入或默认数值，但这样匹配就有限制了
//        List<User> userList = this.page(new Page<>(pageNum,pageSize),queryWrapper);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("tags");
        queryWrapper.select("id","tags");
        List<User> userList = this.list(queryWrapper);

        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下表 => 相似度'
        List<Pair<User,Long>> list = new ArrayList<>();
        // 依次计算当前用户和所有用户的相似度
        for (int i = 0; i <userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            //无标签的 或当前用户为自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()){
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            //计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user,distance));
        }
        //按编辑距离有小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        //有顺序的userID列表
        List<Long> userListVo = topUserPairList.stream().map(pari -> pari.getKey().getId()).collect(Collectors.toList());

        //根据id查询user完整信息
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id",userListVo);
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper).stream()
                .map(user -> getSafetyUser(user))
                .collect(Collectors.groupingBy(User::getId));

        // 因为上面查询打乱了顺序，这里根据上面有序的userID列表赋值
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userListVo){
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }


    @Override
    public User doLogin(String userAccount, String userPassword, HttpServletRequest request) {
//        1.校验（工具类）
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
//      查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
//        用户不存在
        if (user == null){
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不存在");
        }
        //        用户脱敏(返回给前端数据)
        User safetyUser = getSafetyUser(user);

//      提交登录态（向session设置数据）
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;

    }

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    @Override
    public User getSafetyUser(User user){
        if (user == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(0);
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUpdateTime(new Date());
        safetyUser.setIsDelete(0);
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setTags(user.getTags());
        return safetyUser;
    }

    /**
     * 用户注销 ，移除登录态
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 根据标签查用户
     *
     * @param tagNameList 用户所拥有的标签
     * @return
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if(CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
////        拼接and查询
//        for (String tagName : tagNameList) {
//            queryWrapper = queryWrapper.like("tags", tagName);
//        }
//        List<User> userList = userMapper.selectList(queryWrapper);
//        上面注释掉的是直接模糊查询，下面用的是内存查

        // 查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 在内存中判断是否包含符合要求的标签
        return userList.stream().filter(user -> {
            String tagStr = user.getTags();
            if (StringUtils.isBlank(tagStr)) {
                return false;
            }
            Set<String> tempTagNameSet = gson.fromJson(tagStr, new TypeToken<Set<String>>(){}.getType());
//            java8 Optional判空
            tempTagNameSet = Optional.ofNullable(tempTagNameSet ).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }
}

