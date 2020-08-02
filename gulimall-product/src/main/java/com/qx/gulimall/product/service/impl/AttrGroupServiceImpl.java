package com.qx.gulimall.product.service.impl;

import com.qx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.qx.gulimall.product.entity.AttrEntity;
import com.qx.gulimall.product.entity.vo.AttGroupWithAttrVo;
import com.qx.gulimall.product.service.AttrAttrgroupRelationService;
import com.qx.gulimall.product.service.AttrService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.product.dao.AttrGroupDao;
import com.qx.gulimall.product.entity.AttrGroupEntity;
import com.qx.gulimall.product.service.AttrGroupService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;

import javax.swing.*;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catlogId) {

        // 定义包装类对象
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        // 当id为0时，查询所有
        if(catlogId != 0){
            wrapper.eq("catelog_id",catlogId);
        }
        // 初始化page对象
        IPage<AttrGroupEntity> page = null;
        // 自定义query对象，辅助查询
        Query<AttrGroupEntity> entityQuery = new Query<>();
        if(!CollectionUtils.isEmpty(params)){
            Object key =  params.get("key");
            // 判断key的有效性
            if(key != null && !StringUtils.isEmpty(key)){
                wrapper.and((obj) -> {
                    obj.like("attr_group_name",key)
                            .or()
                            .like("attr_group_id",key);
                });
            }
        }
        page = this.page(entityQuery.getPage(params),wrapper);

        return new PageUtils(page);

    }

    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        // 设置查询条件
        QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId);
        // 查出关联关系
        List<AttrAttrgroupRelationEntity> relationList = attrAttrgroupRelationService.list(queryWrapper);
        // 创建集合接收id
        List<Long> attrIdList = null;
        if(!CollectionUtils.isEmpty(relationList)){
            attrIdList = new ArrayList<>();
            for (AttrAttrgroupRelationEntity relationEntity : relationList) {
                Long attrId = relationEntity.getAttrId();
                attrIdList.add(attrId);
            }
            // 通过attrIdList查询返回
            return attrService.listByIds(attrIdList);
        }
        return null;
    }

    @Override
    public PageUtils getNoattrRelation(Map<String, Object> params, Long attrgroupId) {
        // 1、该属性未被其他属性关联，2、类型为当前属性类型 0-销售属性，1-基本属性
        // 获取属性分组里面还没有关联的本分类里面的其他基本属性，方便添加新的关联
        QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
        // 不为0时，设置id
        if(attrgroupId != 0){
//            queryWrapper.ne("attr_group_id", attrgroupId);
            queryWrapper.eq("attr_group_id", attrgroupId);
        }

        // 通过在分组表查询当前分组下的分类ID
        AttrGroupEntity groupEntity = attrGroupService.getById(attrgroupId);
        Long catelogId = groupEntity.getCatelogId();

        // 申明属性表查询包装类封装条件
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        // 关联属性必须为当前分类下的数据
        wrapper.eq("catelog_id",catelogId);
        if(params != null && params.size() > 0){
            // 获取id为attr_group_id的关系表数据
            List<AttrAttrgroupRelationEntity> relationList = attrAttrgroupRelationService.list(queryWrapper);
            // 遍历关系表获取属性attrId，并将其放入wrapper中
            relationList.forEach(relation -> {
                // 不包含关系表中attrId的数据
                wrapper.ne("attr_id",relation.getAttrId());
            });
            // 拼接条件key
            Object key = params.get("key");
            if(!StringUtils.isEmpty(key)){
                wrapper.like("attr_name",key);
            }

        }
        // 查询attrId不为attrIdList中的数据
        Query<AttrEntity> attrEntityQuery = new Query<>();
        IPage<AttrEntity> page = attrService.page(attrEntityQuery.getPage(params), wrapper);
        //

        PageUtils pageUtils = new PageUtils(page);

        /*IPage<AttrAttrgroupRelationEntity> page = null;
        if(params != null && params.size() > 0){
            // 使用自定义query对象
            Query<AttrAttrgroupRelationEntity> query = new Query<>();
            page = attrAttrgroupRelationService.page(query.getPage(params), queryWrapper);
        }*/
        // 创建pageUtils封装对象
//        PageUtils pageUtils = new PageUtils(page);
        //Variable used in lambda expression should be final or effectively final
        // 为什么 Lambda 表达式(匿名类) 不能访问非 final 的局部变量呢？
        // 因为实例变量存在堆中，而局部变量是在栈上分配，Lambda 表达(匿名类) 会在另一个线程中执行。
        // 如果在线程中要直接访问一个局部变量，可能线程执行时该局部变量已经被销毁了，
        // 而 final 类型的局部变量在 Lambda 表达式(匿名类) 中其实是局部变量的一个拷贝。
        /*if(page != null){
            List<List<AttrEntity>> records = page.getRecords().stream().map(attrAttrgroupRelationEntity -> {
                // 查询
                Long attrId = attrAttrgroupRelationEntity.getAttrId();
                // 查询attr_id不为已经使用的attrID--》ne
                QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().ne("attr_id",attrId);
                // key不能设置为key = "";这样使用like条件会查出全部，也不能将key设置在集合外面，不在同一个线程，当访问key变量时，key可能已经销毁了
                String key = null;
                if(!StringUtils.isEmpty(params.get("key"))){
                    key = (String) params.get("key");
                    wrapper.or().like("attr_name", key);
                }
                // 直接将查询出来的当前集合返回，由于查询的是不等于该attrId的，故可能不止一个结果
                List<AttrEntity> attrEntityList = attrService.list(wrapper);
                if(attrEntityList != null){
                    return attrEntityList;
                } else {
                    return null;
                }

            }).collect(Collectors.toList());

            // 创建集合组装数据
            ArrayList<AttrEntity> attrReturnList = new ArrayList<>();
            // 取出所有数据
            for (List<AttrEntity> record : records) {
                attrReturnList.addAll(record);
            }
            // 设置结果集
            pageUtils.setList(attrReturnList);
        }*/
        return pageUtils;
    }

    @Override
    public List<AttGroupWithAttrVo> getGroupWithAttr(Long catelogId) {

        // 0、创建AttGroupWithAttrVo接收并组装数据
        List<AttGroupWithAttrVo> attGroupWithAttrVoList = new ArrayList<>();

        // 1、通过catelogId获取当前分类ID的分组，进而获得分组ID
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        List<AttrGroupEntity> groupList = this.list(wrapper.eq("catelog_id",catelogId));

        // 封装数据
        List<AttGroupWithAttrVo> groupWithAttrVoList = groupList.stream().map(group -> {
            // 创建wrapper封装分组id
            QueryWrapper<AttrAttrgroupRelationEntity> relationQueryWrapper = new QueryWrapper<>();
            Long attrGroupId = group.getAttrGroupId();
            // 定义AttGroupWithAttrVo
            AttGroupWithAttrVo attGroupWithAttrVo = new AttGroupWithAttrVo();
            /*relationQueryWrapper.eq("attr_group_id", attrGroupId);
            // 通过分组ID获得属性和分组关联表，进而获得属性ID
            List<AttrAttrgroupRelationEntity> relationEntityList = attrAttrgroupRelationService.list(relationQueryWrapper);

            if(relationEntityList != null){
                // ①：遍历获取属性id
                List<Long> attrIdList = relationEntityList.stream().map(relationEntity -> {
                    Long attrId = relationEntity.getAttrId();
                    return attrId;
                }).collect(Collectors.toList());
//                attrService.
                // 3、通过属性

                // id查询属性
                List<AttrEntity> attrEntityList = attrService.listByIds(attrIdList);*/

            // 使用写过的方法替换
            List<AttrEntity> attrEntityList = getRelationAttr(attrGroupId);
            // 遍历组装group
            // 复制属性分组
            BeanUtils.copyProperties(group, attGroupWithAttrVo);
            // 设置属性
            attGroupWithAttrVo.setAttrs(attrEntityList);
            return attGroupWithAttrVo;
        }).collect(Collectors.toList());

        return groupWithAttrVoList;
    }

}