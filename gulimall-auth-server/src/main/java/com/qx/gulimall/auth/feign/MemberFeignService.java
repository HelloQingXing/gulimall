package com.qx.gulimall.auth.feign;

import com.qx.common.utils.R;
import com.qx.gulimall.auth.entity.vo.MemberRegisterVo;
import com.qx.gulimall.auth.entity.vo.MemberloginVo;
import com.qx.gulimall.auth.entity.vo.WebInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Classname MemberFeignService
 * @Description
 * @Date 2020/8/19 22:14
 * @Author 卿星
 */
@Service
@FeignClient("gulimall-member")
public interface MemberFeignService {

    @PostMapping("member/member/regist")
    R saveByRegisterVo(@RequestBody MemberRegisterVo vo);

    @PostMapping("member/member/login")
    R login(@RequestBody MemberloginVo vo);

    @PostMapping("member/member/oauth2/login")
    R authLogin(@RequestBody WebInfoVo webInfoVo);
}
