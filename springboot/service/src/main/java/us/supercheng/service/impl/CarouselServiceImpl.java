package us.supercheng.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import us.supercheng.enums.YesOrNo;
import us.supercheng.mapper.CarouselMapper;
import us.supercheng.pojo.Carousel;
import us.supercheng.service.CarouselService;
import us.supercheng.utils.JsonUtils;
import us.supercheng.utils.RedisOperator;

import java.util.List;

@Service
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    private CarouselMapper carouselMapper;

    @Autowired
    private RedisOperator redisOperator;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Carousel> getActiveCarousel() {
        List<Carousel> ret = null;
        String res = this.redisOperator.get("index_carousel");

        if (StringUtils.isBlank(res)) {
            Example carouselExp = new Example(Carousel.class);
            Example.Criteria userCriteria = carouselExp.createCriteria();
            userCriteria.andEqualTo("isShow", YesOrNo.Yes.type);
            ret = this.carouselMapper.selectByExample(carouselExp);
            this.redisOperator.set("index_carousel", JsonUtils.objectToJson(ret));
        } else
            ret = JsonUtils.jsonToList(res, Carousel.class);

        return ret;
    }
}
