package com.hd.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.usercenter.model.domain.UserTeam;
import com.hd.usercenter.service.UserTeamService;
import com.hd.usercenter.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 29077
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-04-01 18:50:23
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




