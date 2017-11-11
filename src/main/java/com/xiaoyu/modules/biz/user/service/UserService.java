/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.user.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiaoyu.common.base.BaseService;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.base.ResponseCode;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.common.utils.Md5Utils;
import com.xiaoyu.common.utils.StringUtils;
import com.xiaoyu.common.utils.UserUtils;
import com.xiaoyu.modules.biz.message.entity.Message;
import com.xiaoyu.modules.biz.message.service.MessageHandler;
import com.xiaoyu.modules.biz.user.dao.FollowDao;
import com.xiaoyu.modules.biz.user.dao.LoginRecordDao;
import com.xiaoyu.modules.biz.user.dao.UserAttrDao;
import com.xiaoyu.modules.biz.user.dao.UserDao;
import com.xiaoyu.modules.biz.user.entity.Follow;
import com.xiaoyu.modules.biz.user.entity.LoginRecord;
import com.xiaoyu.modules.biz.user.entity.User;
import com.xiaoyu.modules.biz.user.entity.UserAttr;
import com.xiaoyu.modules.biz.user.service.api.IUserService;
import com.xiaoyu.modules.biz.user.vo.FollowVo;
import com.xiaoyu.modules.biz.user.vo.UserVo;
import com.xiaoyu.modules.sys.constant.NumCountType;

/**
 * @author xiaoyu 2016年3月16日
 */
@Service
@Transactional
public class UserService extends BaseService<UserDao, User> implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginRecordDao userRecordDao;

    private Map<String, Object> user2Map(User u) {
        final Map<String, Object> map = new HashMap<>();
        map.put("userId", u.getId());
        map.put("description", u.getDescription());
        map.put("background", u.getBackground());
        map.put("avatar", u.getAvatar());
        map.put("nickname", u.getNickname());
        map.put("signature", u.getSignature());
        return map;
    }

    private Map<String, Object> user2Map(UserVo u) {
        final Map<String, Object> map = new HashMap<>();
        map.put("userId", u.getUserId());
        map.put("description", u.getDescription());
        map.put("background", u.getBackground());
        map.put("avatar", u.getAvatar());
        map.put("nickname", u.getNickname());
        map.put("signature", u.getSignature());
        map.put("attr", u.getAttr());
        return map;
    }

    private User getForLogin(User user) {
        final User u = this.userDao.getForLogin(user);
        if (u == null) {
            return user;
        }
        return u;
    }

    @Autowired
    private MessageHandler msgHandler;

    // 发送消息
    private void sendMsg(String userId, int type, String bizId, int bizType, int bizAction, String content,
            String reply) {
        final Message msg = new Message();
        msg.setSenderId(userId).setType(type).setBizId(bizId).setBizType(bizType).setBizAction(bizAction);
        if (content != null) {
            msg.setContent(content);
        }
        if (reply != null) {
            msg.setReply(reply);
        }
        try {
            this.msgHandler.produce(JSON.toJSONString(msg));
        } catch (final Exception e) {
            logger.error(e.toString());
            // do nothing
        }

    }

    @Override
    public String login(HttpServletRequest request, String loginName, String password) {
        final HttpSession session = request.getSession(false);
        final ResponseMapper mapper = ResponseMapper.createMapper();
        if (session != null) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> map = (Map<String, Object>) session.getAttribute(request.getHeader("token"));
            // 3hours
            session.setMaxInactiveInterval(60 * 60 * 3);
            if (map != null) {
                return mapper.data(map).resultJson();
            }
        }
        if (StringUtils.isAnyBlank(password, loginName)) {
            mapper.code(ResponseCode.ARGS_ERROR.statusCode()).message("姓名和密码不能为空");
            return mapper.resultJson();
        }

        User user = new User();
        user.setLoginName(loginName.trim());
        user = this.getForLogin(user);
        if (StringUtils.isBlank(user.getId()) || !Md5Utils.MD5(password.trim()).equalsIgnoreCase(user.getPassword())) {
            mapper.code(ResponseCode.ARGS_ERROR.statusCode()).message("用户名或密码不正确");
            return mapper.resultJson();
        }
        user.setPassword(null);
        // 登录名存入session
        final HttpSession session1 = request.getSession(true);
        // 3h
        session1.setMaxInactiveInterval(60 * 60 * 3);
        // 用户id和密码和当前时间生成的md5用于token
        final String token = Md5Utils.MD5(user.getId() + password + new Date().getTime());
        session1.setAttribute(token, user);
        // 不管怎么设置过期时间 都没用 暂不知道为撒
        // session1.setMaxInactiveInterval(2060);
        final Map<String, Object> result = this.user2Map(user);
        result.put("token", token);
        return mapper.data(result).resultJson();
    }

    @Override
    public String loginRecord(HttpServletRequest request, String userId, String device) {
        final String loginIp = request.getRemoteHost();
        final LoginRecord record = new LoginRecord();
        record.setUserId(userId);
        record.setLoginIp(loginIp);
        record.setDevice(device);
        record.setId(IdGenerator.uuid());
        this.userRecordDao.insert(record);
        return null;
    }

    @Override
    public String userDetail(HttpServletRequest request, String userId) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final User user = new User();
        user.setId(userId);
        final UserVo u = this.userDao.getVo(user);
        if (u == null) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        return mapper.data(this.user2Map(u)).resultJson();
    }

    @Autowired
    private UserAttrDao userAttrDao;

    @Override
    public String register(HttpServletRequest request, String loginName, String password) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final User user = new User();
        user.setLoginName(loginName.trim()).setPassword(Md5Utils.MD5(password.trim())).setNickname(loginName.trim());

        if (this.userDao.isExist(user) > 0) {
            return mapper.code(ResponseCode.EXIST.statusCode()).message("此账号早已注册").resultJson();
        }
        user.setId(IdGenerator.uuid());
        try {
            if (this.userDao.insert(user) > 0) {
                final UserAttr attr = new UserAttr();
                attr.setUserId(user.getId());
                this.userAttrDao.insert(attr);
                // 消息推送
                this.sendMsg(user.getId(), 2, user.getId(), 1, 0, "很高兴与您相识,希望以后的日子见字如面", null);
                return mapper.resultJson();
            }
        } catch (final Exception e) {
            UserService.logger.error(e.toString());
            throw e;
        }

        return mapper.code(ResponseCode.FAILED.statusCode()).message("抱歉,注册没成功").resultJson();
    }

    @Override
    public String editUser(HttpServletRequest request, String userId, String content, Integer flag) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final User u = UserUtils.checkLoginDead(request);
        if (u == null) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).message("未登录或登录失效,请重新登录").resultJson();
        }
        final User temp = new User();
        temp.setId(userId);
        switch (flag) {
        case 0:// 修改头像
            if (StringUtils.isBlank(content)) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).message("请上传头像").resultJson();
            }
            temp.setAvatar(content.substring(content.lastIndexOf("/")));
            break;
        case 1:// 修改签名
            if (StringUtils.isNotBlank(content) && content.length() > 15) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).message("签名最多15个字").resultJson();
            }
            temp.setSignature(content);
            break;
        case 2:// 修改简介
            if (StringUtils.isNotBlank(content) && content.length() > 100) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).message("简介最多100个字").resultJson();
            }
            temp.setDescription(content);
            break;
        case 3:// 修改昵称
            if (StringUtils.isBlank(content)) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).message("昵称不能为空").resultJson();
            }
            if (content.length() > 10) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).message("昵称最多10个字").resultJson();
            }
            temp.setNickname(content);
            break;
        case 4:// 修改背景图片
            if (StringUtils.isBlank(content)) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).message("请上传背景图片").resultJson();
            }
            temp.setBackground(content.substring(content.lastIndexOf("/")));
            break;

        }
        if (this.userDao.update(temp) > 0) {
            return mapper.code(ResponseCode.SUCCESS.statusCode()).message("资料修改成功").resultJson();
        }
        return mapper.code(ResponseCode.FAILED.statusCode()).message("资料修改失败").resultJson();

    }

    @Autowired
    private FollowDao followDao;

    @Override
    public String followUser(HttpServletRequest request, String userId, String followTo) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final User u = UserUtils.checkLoginDead(request);
        if (u == null) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).message("您未登录或登录失效,请重新登录").resultJson();
        }
        if (!u.getId().equals(userId)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        if (userId.equals(followTo)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).message("无法关注自己").resultJson();
        }
        final User t = new User();
        t.setId(followTo);
        if (this.userDao.isExist(t) < 1) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).message("所关注用户不存在").resultJson();
        }
        if (this.followDao.isFollow(userId, followTo) == 1) {
            return mapper.code(ResponseCode.EXIST.statusCode()).message("您已关注过该用户").resultJson();
        }
        final Follow f = new Follow();
        f.setId(IdGenerator.uuid());
        f.setUserId(followTo);
        f.setFollowerId(userId);

        try {
            if (this.followDao.isExist(f) > 0) {
                this.followDao.update(f);
            } else {
                this.followDao.insert(f);
            }
            this.userAttrDao.addNum(NumCountType.FollowerNum.ordinal(), 1, f.getUserId());
            this.sendMsg(userId, 0, f.getId(), 1, 8, null, null);
        } catch (final RuntimeException e) {
            logger.error(e.toString());
            throw e;
        }
        final Map<String, String> map = new HashMap<>();
        map.put("isFollow", "1");
        return mapper.data(map).resultJson();
    }

    @Override
    public String cancelFollow(HttpServletRequest request, String userId, String followTo) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final User u = UserUtils.checkLoginDead(request);
        if (u == null) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode()).message("未登录或登录失效,请重新登录").resultJson();
        }
        if (!u.getId().equals(userId)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).resultJson();
        }
        final User t = new User();
        t.setId(followTo);
        if (this.userDao.isExist(t) < 1) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode()).message("所关注用户不存在").resultJson();
        }
        try {
            this.followDao.cancelLove(followTo, userId);
            this.userAttrDao.addNum(NumCountType.FollowerNum.ordinal(), -1, followTo);
        } catch (final RuntimeException e) {
            logger.error(e.toString());
            throw e;
        }

        final Map<String, String> map = new HashMap<>();
        map.put("isFollow", "0");
        return mapper.data(map).resultJson();
    }

    @Override
    public String follower(HttpServletRequest request, String userId) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final String pageNum = request.getHeader("pageNum");
        final Follow f = new Follow();
        f.setUserId(userId);
        List<FollowVo> result = new ArrayList<>();
        PageHelper.startPage(Integer.valueOf(pageNum), 10);
        final Page<FollowVo> page = (Page<FollowVo>) this.followDao.findList(f);
        if (page != null) {
            result = page.getResult();
            if (result != null && result.size() > 0) {
                mapper.data(result);
            }
        }
        return mapper.data(result).resultJson();
    }

    @Override
    public String following(HttpServletRequest request, String userId) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final String pageNum = request.getHeader("pageNum");
        final Follow f = new Follow();
        f.setFollowerId(userId);
        List<FollowVo> result = new ArrayList<>();
        PageHelper.startPage(Integer.valueOf(pageNum), 10);
        final Page<FollowVo> page = (Page<FollowVo>) this.followDao.findList(f);
        if (page != null) {
            result = page.getResult();
            if (result != null && result.size() > 0) {
                mapper.data(result);
            }
        }
        return mapper.data(result).resultJson();
    }

    @Override
    public String isFollowed(HttpServletRequest request, String userId, String followTo) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final int num = this.followDao.isFollow(userId, followTo);
        final Map<String, String> map = new HashMap<>();
        map.put("isFollow", "" + num);
        return mapper.data(map).resultJson();
    }

    @Override
    public String commonNums(HttpServletRequest request, String userId) {
        final ResponseMapper mapper = ResponseMapper.createMapper();
        final UserAttr t = new UserAttr();
        t.setUserId(userId);
        final UserAttr attr = this.userAttrDao.get(t);
        final Map<String, Object> map = new HashMap<>();
        if (attr != null) {
            map.put("articleNum", attr.getArticleNum());
            map.put("collectNum", attr.getCollectNum());
            map.put("followerNum", attr.getFollowerNum());
            return mapper.data(map).resultJson();
        }
        return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
    }

}
