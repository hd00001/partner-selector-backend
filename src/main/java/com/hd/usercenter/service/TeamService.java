package com.hd.usercenter.service;

import com.hd.usercenter.model.Dto.TeamQuery;
import com.hd.usercenter.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hd.usercenter.model.domain.User;
import com.hd.usercenter.model.request.TeamJoinRequest;
import com.hd.usercenter.model.request.TeamQuitRequest;
import com.hd.usercenter.model.request.TeamUpdateRequest;
import com.hd.usercenter.model.vo.TeamUserVO;

import java.util.List;

/**
* @author 29077
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-04-01 18:48:40
*/
public interface TeamService extends IService<Team> {


    long addTeam(Team team, User loginUser);

    /**
     * 查询队伍
     * @param teamQuery
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 修改队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除队伍
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id, User loginUser);

}
