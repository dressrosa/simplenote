/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.user.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiaoyu.common.base.BaseService;
import com.xiaoyu.common.base.ResponseCode;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.common.utils.Md5Utils;
import com.xiaoyu.common.utils.StringUtil;
import com.xiaoyu.common.utils.UserUtils;
import com.xiaoyu.maple.core.MapleUtil;
import com.xiaoyu.modules.biz.message.entity.Message;
import com.xiaoyu.modules.biz.message.service.api.IMessageService;
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
import com.xiaoyu.modules.constant.BizAction;
import com.xiaoyu.modules.constant.BizType;
import com.xiaoyu.modules.constant.MsgType;
import com.xiaoyu.modules.constant.NumCountType;

/**
 * @author xiaoyu 2016年3月16日
 */
@Service
@Primary
public class UserServiceImpl extends BaseService<UserDao, User> implements IUserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginRecordDao userRecordDao;

    @Autowired
    private UserAttrDao userAttrDao;

    @Autowired
    private FollowDao followDao;

    @Autowired
    private IMessageService messageService;

    private Map<String, Object> user2Map(User u) {
        return MapleUtil.wrap(u)
                .rename("uuid", "userId")
                .skip("password")
                .skip("sex")
                .skip("loginName")
                .map();
    }

    private Map<String, Object> user2Map(UserVo u) {
        return MapleUtil.wrap(u)
                .map();
    }

    private User getForLogin(User user) {
        final User u = this.userDao.getForLogin(user);
        if (u == null) {
            return user;
        }
        return u;
    }

    @Override
    public String login(HttpServletRequest request, String loginName, String password) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (StringUtil.isAnyBlank(password, loginName)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                    .message("姓名和密码不能为空")
                    .resultJson();
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) session.getAttribute(request.getHeader("token"));
            // 3hours
            session.setMaxInactiveInterval(3600 * 3);
            if (map != null) {
                return mapper.data(map).resultJson();
            }
        }

        User user = new User();
        user.setLoginName(loginName.trim());
        user = this.getForLogin(user);
        if (user.getId() == null || !Md5Utils.md5(password.trim()).equalsIgnoreCase(user.getPassword())) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                    .message("用户名或密码不正确")
                    .resultJson();
        }
        // 消去密码
        user.setPassword(null);
        // 登录名存入session
        HttpSession tsession = request.getSession(true);
        // 3h
        tsession.setMaxInactiveInterval(3600 * 3);
        // 用户id和密码和当前时间生成的md5用于token
        String token = Md5Utils.md5(user.getId() + password + System.currentTimeMillis());
        tsession.setAttribute(token, user);
        // 不管怎么设置过期时间 都没用 暂不知道为撒
        // tsession.setMaxInactiveInterval(2060);
        Map<String, Object> result = this.user2Map(user);
        result.put("token", token);
        return mapper.data(result).resultJson();
    }

    @Override
    public String loginRecord(HttpServletRequest request, String userId, String device) {
        final String loginIp = request.getRemoteHost();
        LoginRecord record = new LoginRecord();
        record.setUserId(userId)
                .setLoginIp(loginIp)
                .setDevice(device)
                .setUuid(IdGenerator.uuid());
        this.userRecordDao.insert(record);
        return null;
    }

    @Override
    public String userDetail(HttpServletRequest request, String userId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        User user = new User();
        user.setUuid(userId);
        UserVo u = this.userDao.getVo(user);
        if (u == null) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        return mapper.data(this.user2Map(u)).resultJson();
    }

    @Transactional(readOnly = false, rollbackFor = RuntimeException.class)
    @Override
    public String register(HttpServletRequest request, String loginName, String password) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        User user = new User();
        user.setLoginName(loginName.trim())
                .setPassword(Md5Utils.md5(password.trim()))
                .setNickname(loginName.trim());

        if (this.userDao.isExist(user) > 0) {
            return mapper.code(ResponseCode.EXIST.statusCode())
                    .message("此账号已存在")
                    .resultJson();
        }
        user.setUuid(IdGenerator.uuid());
        try {
            if (this.userDao.insert(user) > 0) {
                //用户初始化数据
                UserAttr attr = new UserAttr();
                attr.setUserId(user.getUuid());
                this.userAttrDao.insert(attr);
                // 消息推送
                this.messageService.sendMsgEvent(new Message()
                        .setSenderId(user.getUuid())
                        .setReceiverId(user.getUuid())
                        .setType(MsgType.NOTICE.statusCode())
                        .setBizId(user.getUuid())
                        .setBizType(BizType.USER.statusCode())
                        .setBizAction(BizAction.NONE.statusCode())
                        .setContent("很高兴与您相识,希望以后的日子见字如面")
                        .setReply(null));
                return mapper.resultJson();
            }
        } catch (RuntimeException e) {
            LOG.error(e.toString());
            throw e;
        }
        return mapper.code(ResponseCode.FAILED.statusCode())
                .message("抱歉,注册没成功")
                .resultJson();
    }

    @Override
    public String editUser(HttpServletRequest request, String userId, String content, Integer flag) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        User u = UserUtils.checkLoginDead(request);
        if (u == null) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("未登录或登录失效,请重新登录")
                    .resultJson();
        }
        User temp = new User();
        temp.setUuid(u.getUuid());
        switch (flag) {
        // 修改头像
        case 0:
            if (StringUtil.isBlank(content)) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                        .message("请上传头像")
                        .resultJson();
            }
            temp.setAvatar(content.substring(content.lastIndexOf("/")));
            break;
        // 修改签名
        case 1:
            if (StringUtil.isNotBlank(content) && content.length() > 15) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                        .message("签名最多15个字")
                        .resultJson();
            }
            temp.setSignature(content);
            break;
        // 修改简介
        case 2:
            if (StringUtil.isNotBlank(content) && content.length() > 100) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                        .message("简介最多100个字")
                        .resultJson();
            }
            temp.setDescription(content);
            break;
        // 修改昵称
        case 3:
            if (StringUtil.isBlank(content)) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                        .message("昵称不能为空")
                        .resultJson();
            }
            if (content.length() > 10) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                        .message("昵称最多10个字")
                        .resultJson();
            }
            temp.setNickname(content);
            break;
        // 修改背景图片
        case 4:
            if (StringUtil.isBlank(content)) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                        .message("请上传背景图片")
                        .resultJson();
            }
            temp.setBackground(content.substring(content.lastIndexOf("/")));
            break;
        default:
            break;
        }
        if (this.userDao.update(temp) > 0) {
            return mapper.code(ResponseCode.SUCCESS.statusCode())
                    .message("资料修改成功")
                    .resultJson();
        }
        return mapper.code(ResponseCode.FAILED.statusCode())
                .message("资料修改失败")
                .resultJson();

    }

    @Transactional(readOnly = false, rollbackFor = RuntimeException.class)
    @Override
    public String followUser(HttpServletRequest request, String userId, String followTo) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        User u = UserUtils.checkLoginDead(request);
        if (u == null) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("您未登录或登录失效,请重新登录")
                    .resultJson();
        }
        if (!u.getUuid().equals(userId)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        if (userId.equals(followTo)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                    .message("无法关注自己")
                    .resultJson();
        }
        User t = new User();
        t.setUuid(followTo);
        if (this.userDao.isExist(t) < 1) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                    .message("所关注用户不存在")
                    .resultJson();
        }
        if (this.followDao.isFollow(userId, followTo) == 1) {
            return mapper.code(ResponseCode.EXIST.statusCode())
                    .message("您已关注过该用户")
                    .resultJson();
        }
        Follow f = new Follow();
        f.setUserId(followTo)
                .setFollowerId(userId)
                .setUuid(IdGenerator.uuid());
        boolean isSendMsg = false;
        try {
            if (this.followDao.isExist(f) > 0) {
                this.followDao.update(f);
            } else {
                this.followDao.insert(f);
            }
            this.userAttrDao.addNum(NumCountType.FollowerNum.ordinal(), 1, f.getUserId());
            isSendMsg = true;
        } catch (RuntimeException e) {
            LOG.error(e.toString());
            throw e;
        }
        Map<String, String> map = new HashMap<>(2);
        map.put("isFollow", "1");

        if (isSendMsg) {
            this.messageService.sendMsgEvent(new Message()
                    .setSenderId(userId)
                    .setReceiverId(followTo)
                    .setType(MsgType.NEWS.statusCode())
                    .setBizId(f.getUuid())
                    .setBizType(BizType.USER.statusCode())
                    .setBizAction(BizAction.FOLLOW.statusCode())
                    .setContent(null)
                    .setReply(null));
        }
        return mapper.data(map).resultJson();
    }

    @Transactional(readOnly = false, rollbackFor = RuntimeException.class)
    @Override
    public String cancelFollow(HttpServletRequest request, String userId, String followTo) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        User u = UserUtils.checkLoginDead(request);
        if (u == null) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("未登录或登录失效,请重新登录")
                    .resultJson();
        }
        if (!u.getUuid().equals(userId)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                    .resultJson();
        }
        User t = new User();
        t.setUuid(followTo);
        if (this.userDao.isExist(t) < 1) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                    .message("所关注用户不存在")
                    .resultJson();
        }

        try {
            this.followDao.cancelFollow(followTo, userId);
            this.userAttrDao.addNum(NumCountType.FollowerNum.ordinal(), -1, followTo);
        } catch (RuntimeException e) {
            LOG.error(e.toString());
            throw e;
        }

        Map<String, String> map = new HashMap<>(2);
        map.put("isFollow", "0");
        return mapper.data(map).resultJson();
    }

    @Override
    public String follower(HttpServletRequest request, String userId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        String pageNum = request.getHeader("pageNum");
        Follow f = new Follow();
        f.setUserId(userId);
        PageHelper.startPage(Integer.valueOf(pageNum), 10);
        Page<FollowVo> page = (Page<FollowVo>) this.followDao.findList(f);
        List<FollowVo> list = page.getResult();

        if (list == null || list.isEmpty()) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        return mapper.data(list).resultJson();
    }

    @Override
    public String following(HttpServletRequest request, String userId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        String pageNum = request.getHeader("pageNum");
        Follow f = new Follow();
        f.setFollowerId(userId);

        PageHelper.startPage(Integer.valueOf(pageNum), 10);
        Page<FollowVo> page = (Page<FollowVo>) this.followDao.findList(f);
        List<FollowVo> list = page.getResult();

        if (list == null || list.isEmpty()) {
            return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
        }
        return mapper.data(list).resultJson();
    }

    @Override
    public String isFollowed(HttpServletRequest request, String userId, String followTo) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        int num = this.followDao.isFollow(userId, followTo);
        Map<String, String> map = new HashMap<>(2);
        map.put("isFollow", "" + num);
        return mapper.data(map).resultJson();
    }

    @Override
    public String commonNums(HttpServletRequest request, String userId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        UserAttr attr = this.userAttrDao.get(new UserAttr().setUserId(userId));
        Map<String, Object> map = new HashMap<>(4);
        if (attr != null) {
            map.put("articleNum", attr.getArticleNum());
            map.put("collectNum", attr.getCollectNum());
            map.put("followerNum", attr.getFollowerNum());
            return mapper.data(map).resultJson();
        }
        return mapper.code(ResponseCode.NO_DATA.statusCode()).resultJson();
    }

}
