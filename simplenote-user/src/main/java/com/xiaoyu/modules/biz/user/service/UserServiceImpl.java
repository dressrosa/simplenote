/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.biz.user.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.xiaoyu.beacon.autoconfigure.anno.BeaconExporter;
import com.xiaoyu.common.base.ResponseCode;
import com.xiaoyu.common.base.ResponseMapper;
import com.xiaoyu.common.request.TraceRequest;
import com.xiaoyu.common.utils.IdGenerator;
import com.xiaoyu.common.utils.Md5Utils;
import com.xiaoyu.common.utils.RedisLock;
import com.xiaoyu.common.utils.SpringBeanUtils;
import com.xiaoyu.common.utils.StringUtil;
import com.xiaoyu.core.constant.Type;
import com.xiaoyu.core.template.BaseProducer;
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
import com.xiaoyu.modules.common.MailBuilder;
import com.xiaoyu.modules.constant.BizAction;
import com.xiaoyu.modules.constant.BizType;
import com.xiaoyu.modules.constant.MqContant;
import com.xiaoyu.modules.constant.MsgType;
import com.xiaoyu.modules.constant.NumCountType;

/**
 * @author xiaoyu 2016年3月16日
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Primary
@BeaconExporter(interfaceName = "com.xiaoyu.modules.biz.user.service.api.IUserService", group = "dev")
public class UserServiceImpl implements IUserService {

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

    @Override
    public User login(String loginName, String password) {
        User user = new User();
        user.setLoginName(loginName.trim());
        user = this.userDao.getForLogin(user);
        if (user == null || user.getId() == null
                || !Md5Utils.md5(password.trim()).equalsIgnoreCase(user.getPassword())) {
            return null;
        }
        return user;
    }

    @Override
    public ResponseMapper loginRecord(TraceRequest request, String userId, String device) {
        final String loginIp = request.getHeader().getRemoteHost();
        LoginRecord record = new LoginRecord();
        record.setUserId(userId)
                .setLoginIp(loginIp)
                .setDevice(device)
                .setUuid(IdGenerator.uuid());
        this.userRecordDao.insert(record);
        return null;
    }

    @Override
    public ResponseMapper userDetail(TraceRequest request, String userId) {
        User user = new User();
        user.setUuid(userId);
        UserVo u = this.userDao.getVo(user);
        if (u == null) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.NO_DATA.statusCode());
        }
        return ResponseMapper.createMapper().data(MapleUtil.wrap(u).map());
    }

    @Override
    public ResponseMapper register(TraceRequest request, String loginName, String password) {
        User user = new User();
        user.setLoginName(loginName.trim())
                .setPassword(Md5Utils.md5(password.trim()))
                .setNickname(loginName.length() > 8 ? loginName.substring(0, 8) : loginName);

        user.setUuid(IdGenerator.uuid());
        RedisLock lock = RedisLock.getRedisLock("loginName:" + loginName);
        Integer ret = lock.lock(() -> {
            if (this.userDao.isExist(user) > 0) {
                return -1;
            }
            return SpringBeanUtils.getBean(UserServiceImpl.class).doRegister(user);
        });
        if (ret != null) {
            if (ret > 0) {
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
                // 通知我有人注册了,哈哈
                MailBuilder builder = new MailBuilder();
                builder.sender("1546428286@qq.com", "小往")
                        .receiver("1546428286@qq.com", "Mr xiaoyu")
                        .title("往往:注册通知.")
                        .content("有新人注册了!<br/>名称:" + user.getLoginName() + "<br/>来源:"
                                + request.getHeader().getRemoteHost()
                                + "<br/>速速去了解一下拉"
                                + "<br/><a href='http://47.93.235.211/user/" + user.getUuid() + "'>这是个神奇的链接...</a>");
                try {
                    BaseProducer.produce(Type.TOPIC, MqContant.EMAIL, JSON.toJSONString(builder));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ResponseMapper.createMapper();
            }
            if (ret == -1) {
                return ResponseMapper.createMapper()
                        .code(ResponseCode.FAILED.statusCode())
                        .message("用户已存在");
            }
        }
        return ResponseMapper.createMapper()
                .code(ResponseCode.FAILED.statusCode())
                .message("抱歉,注册没成功");
    }

    @Transactional(readOnly = false, rollbackFor = RuntimeException.class)
    public int doRegister(User user) {
        if (this.userDao.insert(user) > 0) {
            // 用户初始化数据
            UserAttr attr = new UserAttr();
            attr.setUserId(user.getUuid());
            this.userAttrDao.insert(attr);
            return 1;
        }
        return 0;
    }

    @Override
    public ResponseMapper editUser(TraceRequest request, String content, Integer flag) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!request.isLogin()) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("未登录或登录失效,请重新登录");
        }
        User u = request.getUser();
        User temp = new User();
        temp.setUuid(u.getUuid());
        switch (flag) {
        // 修改头像
        case 0:
            if (StringUtil.isEmpty(content)) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                        .message("请上传头像");
            }
            temp.setAvatar(content.substring(content.lastIndexOf("/")));
            break;
        // 修改签名
        case 1:
            if (StringUtil.isNotBlank(content) && content.length() > 15) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                        .message("签名最多15个字");
            }
            temp.setSignature(content);
            break;
        // 修改简介
        case 2:
            if (StringUtil.isNotBlank(content) && content.length() > 100) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                        .message("简介最多100个字");
            }
            temp.setDescription(content);
            break;
        // 修改昵称
        case 3:
            if (StringUtil.isEmpty(content)) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                        .message("昵称不能为空");
            }
            if (content.length() > 8) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                        .message("昵称最多8个字");
            }
            temp.setNickname(content);
            break;
        // 修改背景图片
        case 4:
            if (StringUtil.isEmpty(content)) {
                return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                        .message("请上传背景图片");
            }
            temp.setBackground(content.substring(content.lastIndexOf("/")));
            break;
        default:
            break;
        }
        if (this.userDao.update(temp) > 0) {
            return mapper.code(ResponseCode.SUCCESS.statusCode())
                    .message("资料修改成功");
        }
        return mapper.code(ResponseCode.FAILED.statusCode())
                .message("资料修改失败");

    }

    @Override
    public ResponseMapper followUser(TraceRequest request, String userId, String followTo) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!request.isLogin()) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("您未登录或登录失效,请重新登录");
        }
        User u = request.getUser();
        if (!u.getUuid().equals(userId)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode());
        }
        if (userId.equals(followTo)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                    .message("无法关注自己");
        }
        User t = new User();
        t.setUuid(followTo);
        if (this.userDao.isExist(t) < 1) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                    .message("所关注用户不存在");
        }
        if (this.followDao.isFollow(userId, followTo) == 1) {
            return mapper.code(ResponseCode.EXIST.statusCode())
                    .message("您已关注过该用户");
        }
        Follow f = new Follow();
        f.setUserId(followTo)
                .setFollowerId(userId)
                .setUuid(IdGenerator.uuid());
        boolean isSendMsg = false;
        RedisLock lock = RedisLock.getRedisLock("followUser:" + userId + ":" + followTo);
        Integer ret = lock.lock(() -> {
            SpringBeanUtils.getBean(UserServiceImpl.class).doFollowUser(f);
            return 1;
        });
        if (ret == null) {
            return mapper.code(ResponseCode.FAILED.statusCode())
                    .message("关注用户失败");
        }
        isSendMsg = true;
        Map<String, Object> map = new HashMap<>(2);
        map.put("isFollow", 1);

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
        return mapper.data(map);
    }

    @Transactional(readOnly = false, rollbackFor = RuntimeException.class)
    public void doFollowUser(Follow f) {
        if (this.followDao.isExist(f) > 0) {
            this.followDao.update(f);
        } else {
            this.followDao.insert(f);
        }
        this.userAttrDao.addNum(NumCountType.FollowerNum.ordinal(), 1, f.getUserId());
    }

    @Override
    public ResponseMapper cancelFollow(TraceRequest request, String userId, String followTo) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        if (!request.isLogin()) {
            return mapper.code(ResponseCode.LOGIN_INVALIDATE.statusCode())
                    .message("未登录或登录失效,请重新登录");
        }
        User u = request.getUser();
        if (!u.getUuid().equals(userId)) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode());
        }
        User t = new User();
        t.setUuid(followTo);
        if (this.userDao.isExist(t) < 1) {
            return mapper.code(ResponseCode.ARGS_ERROR.statusCode())
                    .message("所关注用户不存在");
        }
        SpringBeanUtils.getBean(UserServiceImpl.class).doCancelFollow(followTo, userId);
        Map<String, Object> map = new HashMap<>(2);
        map.put("isFollow", 0);
        return mapper.data(map);
    }

    @Transactional(readOnly = false, rollbackFor = RuntimeException.class)
    public void doCancelFollow(String followTo, String userId) {
        this.followDao.cancelFollow(followTo, userId);
        this.userAttrDao.addNum(NumCountType.FollowerNum.ordinal(), -1, followTo);
    }

    @Override
    public ResponseMapper follower(TraceRequest request, String userId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        Follow f = new Follow();
        f.setUserId(userId);

        PageHelper.startPage(request.getHeader().getPageNum(), 12);
        List<FollowVo> list = this.followDao.findList(f);

        if (list.isEmpty()) {
            return mapper.code(ResponseCode.NO_DATA.statusCode());
        }
        List<Map<String, Object>> total = new ArrayList<>(list.size());
        list.forEach(a -> {
            total.add(MapleUtil.wrap(a).map());
        });
        return mapper.data(total);
    }

    @Override
    public ResponseMapper following(TraceRequest request, String userId) {
        Follow f = new Follow();
        f.setFollowerId(userId);

        PageHelper.startPage(request.getHeader().getPageNum(), 12);
        List<FollowVo> list = this.followDao.findList(f);

        if (list.isEmpty()) {
            return ResponseMapper.createMapper()
                    .code(ResponseCode.NO_DATA.statusCode());
        }
        List<Map<String, Object>> total = new ArrayList<>(list.size());
        list.forEach(a -> {
            total.add(MapleUtil.wrap(a).map());
        });
        return ResponseMapper.createMapper().data(total);
    }

    @Override
    public ResponseMapper isFollowed(TraceRequest request, String userId, String followTo) {
        int num = this.followDao.isFollow(userId, followTo);
        Map<String, Object> map = new HashMap<>(2);
        map.put("isFollow", num);
        return ResponseMapper.createMapper().data(map);
    }

    @Override
    public ResponseMapper commonNums(TraceRequest request, String userId) {
        ResponseMapper mapper = ResponseMapper.createMapper();
        UserAttr attr = this.userAttrDao.get(new UserAttr().setUserId(userId));
        Map<String, Object> map = new HashMap<>(4);
        if (attr != null) {
            map.put("articleNum", attr.getArticleNum());
            map.put("collectNum", attr.getCollectNum());
            map.put("followerNum", attr.getFollowerNum());
            return mapper.data(map);
        }
        return mapper.code(ResponseCode.NO_DATA.statusCode());
    }

    @Override
    public User getByUuid(String uuid) {
        return this.userDao.getByUuid(uuid);
    }

    @Override
    public List<UserVo> findVoByUuid(List<String> userIdList) {
        return this.userDao.findVoByUuid(userIdList);
    }

    @Override
    public int addNum(int type, int num, String userId) {
        return this.userAttrDao.addNum(type, num, userId);
    }

    @Override
    public UserVo getVoByUuid(String userId) {
        return this.userDao.getVoByUuid(userId);
    }

    @Override
    public Follow getFollow(String followId) {
        return this.followDao.getByUuid(followId);
    }

}
