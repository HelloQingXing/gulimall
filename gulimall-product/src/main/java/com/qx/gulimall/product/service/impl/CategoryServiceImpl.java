package com.qx.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qx.gulimall.product.entity.BrandEntity;
import com.qx.gulimall.product.entity.CategoryBrandRelationEntity;
import com.qx.gulimall.product.entity.vo.Catelog2Vo;
import com.qx.gulimall.product.service.BrandService;
import com.qx.gulimall.product.service.CategoryBrandRelationService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.product.dao.CategoryDao;
import com.qx.gulimall.product.entity.CategoryEntity;
import com.qx.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity>
    implements CategoryService {

  @Autowired private CategoryBrandRelationService categoryBrandRelationService;
  @Autowired private StringRedisTemplate stringRedisTemplate;
  @Autowired
  private RedissonClient redissonClient;

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<CategoryEntity> page =
        this.page(new Query<CategoryEntity>().getPage(params), new QueryWrapper<CategoryEntity>());

    return new PageUtils(page);
  }

      /*@Override
      public List<CategoryEntity> listTree() {

          // 查询所有数据
          List<CategoryEntity> categoryList = baseMapper.selectList(null);

          // 返回数据
          List<CategoryEntity> categoryReturnList = new ArrayList<>();

          // 组装数据
          for (int i = 0; i < categoryList.size(); i++) {
              // 获取当前节点
              CategoryEntity category = categoryList.get(i);
              // 判断当前节点ID是否为父ID，即ID=0
              if(category.getParentCid() == 0){

                  // 创建List接收children数据
                  List<CategoryEntity> categoryChildrenList = new ArrayList<>();

//                  categoryChildrenList = assembleCategoryTree(categoryChildrenList,category,categoryList);
                  // 设置子元素
//                  category.setChildren(categoryChildrenList);

                  assembleCategoryTree(category,categoryList);
                  // 将其放入返回集合中
                  categoryReturnList.add(category);
              }

          }


          return categoryReturnList;
      }*/
  @Cacheable(value = "categoryTree",sync = true)
  @Override
  public List<CategoryEntity> listTree() {

    // 查找所有数据
    List<CategoryEntity> entityList = baseMapper.selectList(null);

    //
    List<CategoryEntity> returnList =
        entityList
            // 转换为流的形式
            .stream()
            // 过滤掉父节点不是0的数据
            .filter(
                categoryEntity -> {
                  return categoryEntity.getParentCid() == 0;
                })
            // 组装数据
            .map(
                categoryEntity -> {
                  // 查找子节点
                  List<CategoryEntity> childrens = getChildrens(categoryEntity, entityList);
                  // 设置子节点
                  categoryEntity.setChildren(childrens);
                  return categoryEntity;
                })
            // 排序
            .sorted(
                (menu1, menu2) -> {
                  return (menu1.getSort() == null ? 0 : menu1.getSort())
                      - (menu2.getSort() == null ? 0 : menu2.getSort());
                })
            .collect(Collectors.toList());

    return returnList;
  }

  @Override
  public void removeMenuByIdList(Long... catIds) {

    // TODO 1、检查删除的菜单，是否被其他地方引用

    // 逻辑删除
    baseMapper.deleteBatchIds(Arrays.asList(catIds));
  }

  /**
   * 查找当前分类id的三级id
   *
   * @param catelogId
   * @return
   */
  @Override
  public Long[] getCatlogPath(Long catelogId) {

    List<Long> longList = new ArrayList<>();
    //        List<Long> longListResult = recursionSearchPidByCatId(catelogId, longList);
    recursionSearchPidByCatId(catelogId, longList);
    longList.add(catelogId);

    /*Long [] ids = new Long[3];
    ids[0] = catelogId;
    for (int i = 1; i < longListResult.size(); i++) {
        //
        ids[i] = longListResult.get(i-1);
    }
    // 以上for方法可以使用toArray转为Long数组
    // longList.toArray(new Long[longList.size()])
    // 如果在递归方法插入之前，必须使用倒序，因为前段数据需要这样排列
    Collections.reverse(longList);
    */
    // java.lang.ClassCastException: [Ljava.lang.Object; cannot be cast to [Ljava.lang.Long; at
    // com.qx.gulimall.product.service.impl.CategoryServiceImpl.getCatlogPath(CategoryServiceImpl.java:130) ~[classes/:na]
    // 直接强转，List会直接转为Object类型，不能转为Long类型
    return longList.toArray(new Long[longList.size()]);
  }

  @CacheEvict(value = "category")
  @Transactional(rollbackFor = {Exception.class})
  @Override
  public void updateCascade(CategoryEntity category) {

    Long catId = category.getCatId();
    String name = category.getName();

    CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
    categoryBrandRelationEntity.setCatelogId(catId);
    categoryBrandRelationEntity.setCatelogName(name);
    // 更改冗余字段信息
    categoryBrandRelationService.update(
        categoryBrandRelationEntity,
        new UpdateWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));

    this.updateById(category);

    // 更新缓存信息
      RLock rLock = redissonClient.getReadWriteLock("category_redisson_lock").writeLock();
      try {
          // 加锁
          rLock.lock();
          // 删除缓存中数据
          stringRedisTemplate.delete("cateGoryList_redis");
      } catch (Exception e) {
          e.printStackTrace();
      } finally{
          // 解锁
          rLock.unlock();
      }

  }

  @Cacheable(value = "categoryLevel1",sync = true)
  @Override
  public List<CategoryEntity> getLevel1Catlog() {

    QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<CategoryEntity>().eq("parent_cid", 0);
    List<CategoryEntity> list = this.list(wrapper);

    return list;
  }

  public List<CategoryEntity> getCategoryByParentCid(
      List<CategoryEntity> categoryList, Long catId) {

    // 从category中筛选出数据
    List<CategoryEntity> collect =
        categoryList.stream()
            .filter(category -> category.getParentCid().equals(catId))
            .collect(Collectors.toList());

    return collect;
  }


    /**
     * @Cacheable value：缓存名 sync：是否同步，同步则一个容器一次只允许一个线程执行（因为加的是synchronized关键字，不能保证分布式同步）
     * @return
     */
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogWithOrder(){
      return getCategoryOrderFromDb();
    }

    @Override
    @CacheEvict(value = "category",allEntries = true)
    public void updateBatch(CategoryEntity... categoryEntities) {
        this.updateBatchById(Arrays.asList(categoryEntities));
    }

    public Map<String, List<Catelog2Vo>> getCatelogWithOrderByTradition() {
    /*// 获取ops操作对象
    ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
    // 获取数据
    String cateGoryListJson = ops.get("cateGoryList_redis");
    // 初始化map
    Map<String, List<Catelog2Vo>> cateGoryList = null;
    // 判断redis中是否存有数据
    if (StringUtils.isEmpty(cateGoryListJson)) {
      // 从数据库中获取数据
      cateGoryList = getCategoryOrderFromDb();
      // 将数据转为json
      cateGoryListJson = JSON.toJSONString(cateGoryList);
      // 将数据保存到redis中
      ops.set("cateGoryList_redis", cateGoryListJson, 1, TimeUnit.DAYS);
      // 返回数据
      return cateGoryList;
    }
    // 将数据转为map
    cateGoryList =
        JSON.parseObject(cateGoryListJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {});
    return cateGoryList;*/
      // 获取ops操作对象
      ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
      // 获取数据
      String cateGoryListJson = ops.get("cateGoryList_redis");
      Map<String, List<Catelog2Vo>> cateGoryList = null;
      if(cateGoryListJson == null){
          return getCatelogOrderByredisonLock();
      } else {
          cateGoryList = JSON.parseObject(cateGoryListJson,new TypeReference<Map<String, List<Catelog2Vo>>>(){});
      }
      return cateGoryList;
  }

    /**
     * redisson分布式锁
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatelogOrderByredisonLock() {

        // 获取锁
        RLock lock = redissonClient.getLock("category_redisson_lock");
        // 上锁：阻塞式
        lock.lock();
        Map<String, List<Catelog2Vo>> categoryOrder = null;
        try {
            // 判断是否取到数据
            ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
            String cateGoryList_redis = ops.get("cateGoryList_redis");
            if (!StringUtils.isEmpty(cateGoryList_redis)) {
                // 直接返回
                return JSON.parseObject(cateGoryList_redis, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
            }
            // 从数据库中获取数据
            categoryOrder = getCategoryOrderFromDb();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            // 不管有没有异常都需要解锁
            lock.unlock();
        }

        // 返回数据
        return categoryOrder;
    }


    /**
     * redis分布式锁
     * @return
     */
  public Map<String, List<Catelog2Vo>> getCatelogOrderByredisLock() {

      // 获取redis操作对象
      ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
      // 获取category
      String goryListRedis = ops.get("cateGoryList_redis");
      if(goryListRedis == null ){
          // 先执行占位操作
          // ①：创建UUID赋值给v
          String uuid = UUID.randomUUID().toString();
          Boolean lock = ops.setIfAbsent("lock", uuid, 10, TimeUnit.SECONDS);
          // 判断占位是否成功
          if(lock){
              // 判断redis中是否有数据
              goryListRedis = ops.get("cateGoryList_redis");
              if(goryListRedis != null){
                  return JSON.parseObject(goryListRedis,new TypeReference<Map<String, List<Catelog2Vo>>>(){});
              }
              // 调用数据库
              Map<String, List<Catelog2Vo>> categoryMap = getCategoryOrderFromDb();
              // 在删除之前将数据保存到redis中
              String jsonMap = JSON.toJSONString(categoryMap);
              ops.setIfAbsent("cateGoryList_redis",jsonMap,1,TimeUnit.DAYS);
              // 原子删除lock锁 只有key存在并且值必须和我存取的值一样才能删除
              // 使用lua脚本执行本操作
              String lua = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then return redis.call(\"del\",KEYS[1]) else return 0 end";
              DefaultRedisScript<Long> redisScript = new DefaultRedisScript<Long>(lua,Long.class);
              Long execute = stringRedisTemplate.execute(redisScript, Arrays.asList("lock"), uuid);
              if(execute != null && execute == 1){
                  System.out.println("删除lock锁成功！");
              }
              return categoryMap;
          } else {
              // 如果未拿到锁，执行回旋
              getCatelogOrderByredisLock();
          }
      }
      return JSON.parseObject(goryListRedis,new TypeReference<Map<String, List<Catelog2Vo>>>(){});

  }

    /**
     * 使用synchronized限定一个tomcat中同时只能有一个线程能得到锁
     * @return
     */
  public synchronized Map<String, List<Catelog2Vo>> getCatelogOrderFromDBByLocalLock() {

      // 获取ops操作对象
      ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
      // 获取数据
      String cateGoryListJson = ops.get("cateGoryList_redis");
      // 初始化map
      Map<String, List<Catelog2Vo>> cateGoryList;
      // 判断redis中是否存有数据
      if (StringUtils.isEmpty(cateGoryListJson)) {
        // 从数据库中获取数据
        cateGoryList = getCategoryOrderFromDb();
        System.out.println("访问数据库……");
        // 将数据转为json
        cateGoryListJson = JSON.toJSONString(cateGoryList);
        // 将数据保存到redis中
        ops.set("cateGoryList_redis", cateGoryListJson, 1, TimeUnit.DAYS);
        // 返回数据
        return cateGoryList;
      }
      // 将数据转为map
      cateGoryList =
          JSON.parseObject(cateGoryListJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {});
      return cateGoryList;

  }

  @Cacheable(value = "category",sync = true)
  public Map<String, List<Catelog2Vo>> getCategoryOrderFromDb() {
    System.out.println("from db");
    // 获取一级分类，只需要获取一级分类及其后代们的分类
    List<CategoryEntity> level1CatList = getLevel1Catlog();
    // 获取所有分类，将下面代码中每遍历一次查询一次数据库变为只查询一次
    List<CategoryEntity> categoryList = this.list();
    // 在collect中组装，不在map()方法中组装是因为在map()收集结束后不能获取根元素的catId,
    // 而在收集时组装可以获取到stream流中所有遍历的每个元素，包括catId
    // 通过一级分类组装二级分类、三级分类
    Map<String, List<Catelog2Vo>> collect =
        level1CatList.stream()
            .collect(
                Collectors.toMap(
                    k -> k.getCatId().toString(),
                    l1 -> {
                      // 新建二级分类
                      //            Catelog2Vo catelog2Vo = new Catelog2Vo();
                      // 查找二级分类，将在数据库中查找分类变为在所有分类中查找，降低了查询数据库的频率
                      //            QueryWrapper<CategoryEntity> wrapper = new
                      // QueryWrapper<CategoryEntity>().eq("parent_cid", l1.getCatId());
                      List<CategoryEntity> catLevel2List =
                          getCategoryByParentCid(categoryList, l1.getCatId());
                      // 设置数据
                      //            catelog2Vo.setCatalog1Id(l1.getCatId().toString());
                      List<Catelog2Vo> catelog2Vos = null;
                      if (catLevel2List != null && catLevel2List.size() > 0) {
                        // 查找三级分类
                        catelog2Vos =
                            catLevel2List.stream()
                                .map(
                                    l2 -> {
                                      /*QueryWrapper<CategoryEntity> wrapper1 = new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId());
                                      List<CategoryEntity> catLevel3List = this.list(wrapper1);*/
                                      // 新建二级分类，不能将二级分类新建在获取List<Catelog2Vo>的外面，这样在遍历内部循环时，每个二级Catelog2Vo地址都是同一个地址（外面二级Catelog2Vo的地址）
                                      Catelog2Vo catelog2Vo = new Catelog2Vo();
                                      catelog2Vo.setCatalog1Id(l1.getCatId().toString());
                                      List<CategoryEntity> catLevel3List =
                                          getCategoryByParentCid(categoryList, l2.getCatId());
                                      // 设置数据
                                      catelog2Vo.setId(l2.getCatId().toString());
                                      catelog2Vo.setName(l2.getName());

                                      List<Catelog2Vo.Catelog3Vo> catLevel3s = null;
                                      // 避免子类数据空空遍历设置错误数据
                                      if (catLevel3List != null && catLevel3List.size() > 0) {
                                        // 设置三级分类数据
                                        catLevel3s =
                                            catLevel3List.stream()
                                                .map(
                                                    l3 -> {
                                                      Catelog2Vo.Catelog3Vo catelog3Vo =
                                                          new Catelog2Vo.Catelog3Vo();
                                                      catelog3Vo.setCatalog2Id(
                                                          l2.getCatId().toString());
                                                      catelog3Vo.setId(l3.getCatId().toString());
                                                      catelog3Vo.setName(l3.getName());
                                                      return catelog3Vo;
                                                    })
                                                .collect(Collectors.toList());
                                      }
                                      catelog2Vo.setCatalog3List(catLevel3s);

                                      return catelog2Vo;
                                    })
                                .collect(Collectors.toList());
                      }

                      return catelog2Vos;
                    }));
//    System.out.println(collect);
    return collect;
  }

  /**
   * 递归查找
   *
   * @param catelogId
   * @return
   */
  private List<Long> recursionSearchPidByCatId(Long catelogId, List<Long> idList) {
    // 设置查询条件
    QueryWrapper<CategoryEntity> wrapper =
        new QueryWrapper<CategoryEntity>().select("parent_cid").eq("cat_id", catelogId);
    // 查询
    List<Object> objects = baseMapper.selectObjs(wrapper);
    // 类型转换
    long id = Long.parseLong(objects.get(0).toString());
    // 当父id不为0时，表示还未遍历到根节点
    if (id != 0) {

      recursionSearchPidByCatId(id, idList);
      // 先递归了再将id存入集合，避免再次将集合倒序排列
      idList.add(id);
    } /*else{
          // 遍历到根节点时，将当前id添加进集合，如果将添加方法置为递归方法之前可以执行下面操作，否则会造成一次重复添加
          idList.add(catelogId);
      }*/
    // 这样会每个造成重复添加
    //        idList.add(catelogId);
    return idList;
  }

  // 递归查找所有菜单的子菜单
  private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

    // 生成children
    List<CategoryEntity> entities =
        all.stream()
            .filter(
                categoryEntity -> {
                  // 遍历的该节点ID与根节点ID是否相同
                  return categoryEntity.getParentCid().equals(root.getCatId());
                })
            .map(
                categoryEntity -> {
                  // 递归，找到当前菜单子菜单
                  //                    root.setChildren(getChildrens(categoryEntity, all));
                  // 这是查找父节点子菜单，不能查到当前节点子菜单
                  categoryEntity.setChildren(getChildrens(categoryEntity, all));
                  return categoryEntity;
                })
            .sorted(
                (menu1, menu2) -> {
                  return (menu1.getSort() == null ? 0 : menu1.getSort())
                      - (menu2.getSort() == null ? 0 : menu2.getSort());
                })
            .collect(Collectors.toList());

    return entities;
  }

  /**
   * 组装数据
   *
   * @param category 当前节点
   * @param categoryList 所有节点
   * @return
   */
  /*
      private CategoryEntity assembleCategoryTree(CategoryEntity category , List<CategoryEntity> categoryList){

          // 为父ID时，遍历该节点下所有子ID
          for (int j = 0; j < categoryList.size(); j++) {
              // 当前子遍历节点
              CategoryEntity categoryT = categoryList.get(j);
              // 当前节点父ID是否为其上面ID
              if(category.getCatId().equals(categoryT.getParentCid())){
                  // 从category中获取children，保证每次添加数据地址正确
                  List<CategoryEntity> children = category.getChildren();
                  // 判断children是否为null
                  if(children == null){
                      children = new ArrayList<>();
                      // 地址变化，重新设置children
                      category.setChildren(children);
                  }
                  // 添加子节点
                  children.add(categoryT);
                  // 递归子子节点
                  assembleCategoryTree(categoryT,categoryList);
              }
          }

          return category;
      }
  */

}
