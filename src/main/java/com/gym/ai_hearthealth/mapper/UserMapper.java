package com.gym.ai_hearthealth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.ai_hearthealth.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
