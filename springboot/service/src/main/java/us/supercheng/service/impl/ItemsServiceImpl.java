package us.supercheng.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import us.supercheng.enums.CommentLevel;
import us.supercheng.mapper.*;
import us.supercheng.mymapper.MyMapper;
import us.supercheng.pojo.*;
import us.supercheng.service.ItemsService;
import us.supercheng.utils.PIIUtils;
import us.supercheng.utils.PagedResult;
import us.supercheng.vo.ItemCommentVO;
import us.supercheng.vo.ItemCommentsSummaryVO;
import us.supercheng.vo.ItemSearchResVO;
import us.supercheng.vo.ShopcartItemVO;
import java.util.List;
import java.util.Map;

@Service
public class ItemsServiceImpl implements ItemsService {

    @Autowired
    private ItemsMapper itemsMapper;

    @Autowired
    private ItemsImgMapper itemsImgMapper;

    @Autowired
    private ItemsSpecMapper itemsSpecMapper;

    @Autowired
    private ItemsParamMapper itemsParamMapper;

    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;

    @Autowired
    private ItemsMapperCustom itemsMapperCustom;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items queryItemById(String id) {
        return this.queryOneHelper(Items.class, this.itemsMapper, "id", id);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsImg> queryItemImgListByItemId(String id) {
        return this.queryManyHelper(ItemsImg.class, this.itemsImgMapper, "itemId", id);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsImg getItemsImgMainByItemId(String id) {
        List<ItemsImg> imgs = this.itemsMapperCustom.getItemsImgMainByItemId(id);

        for (ItemsImg img : imgs)
            if (!StringUtils.isBlank(img.getUrl()))
                return img;

        return null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpacListByItemId(String id) {
        return this.queryManyHelper(ItemsSpec.class, this.itemsSpecMapper, "itemId", id);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemParamByItemId(String id) {
        return this.queryOneHelper(ItemsParam.class, this.itemsParamMapper, "itemId", id);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemCommentsSummaryVO getItemCommentsSummary(String id) {
        return new ItemCommentsSummaryVO(
                this.getItemCommentCountByLevel(id, CommentLevel.Good.type),
                this.getItemCommentCountByLevel(id, CommentLevel.Normal.type),
                this.getItemCommentCountByLevel(id, CommentLevel.Bad.type)
            );
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer getItemCommentCountByLevel(String id, Integer level) {
        Example exp = new Example(ItemsComments.class);
        Example.Criteria criteria = exp.createCriteria();
        criteria.andEqualTo("itemId", id);
        criteria.andEqualTo("commentLevel", level);
        return this.itemsCommentsMapper.selectCountByExample(exp);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getComments(Map<String, Object> map, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        List<ItemCommentVO> comments = this.itemsMapperCustom.getCommentsByLevel(map);

        for (ItemCommentVO c : comments)
            if (!StringUtils.isBlank(c.getNickname()))
                c.setNickname(PIIUtils.hashDisplay(c.getNickname()));

        PageInfo<?> pageInfo = new PageInfo<>(comments);

        return new PagedResult(pageNum, pageInfo.getPages(), pageInfo.getTotal(),comments);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult doSearchByKeywordsAndCatId(Map<String, Object> map, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        List<ItemSearchResVO> items = this.itemsMapperCustom.doSearchByKeywordsAndCatId(map);
        PageInfo<?> pageInfo = new PageInfo<>(items);

        return new PagedResult(pageNum, pageInfo.getPages(), pageInfo.getTotal(), items);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ShopcartItemVO> getItemsBySpecIds(List<String> ids) {
        return this.itemsMapperCustom.getItemsBySpecIds(ids);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsSpec getItemsSpecByItemsSpecId(String id) {
        ItemsSpec itemsSpec = new ItemsSpec();
        itemsSpec.setId(id);
        return this.itemsSpecMapper.selectOne(itemsSpec);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items getItemsByItemsId(String id) {
        Items items = new Items();
        items.setId(id);
        return this.itemsMapper.selectOne(items);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateItemsSpecCount(Map<String, Object> map) {
        if (this.itemsMapperCustom.updateItemsSpecCount(map) != 1) {
            throw new RuntimeException("Invalid transaction due to low stock inventory");
        }
    }

    private <T> T queryOneHelper(Class clz, MyMapper mapper, String col, String val) {
        Example exp = new Example(clz);
        Example.Criteria criteria = exp.createCriteria();
        criteria.andEqualTo(col, val);
        return (T) mapper.selectOneByExample(exp);
    }

    private <T> List<T> queryManyHelper(Class clz, MyMapper mapper, String col, String val) {
        Example exp = new Example(clz);
        Example.Criteria criteria = exp.createCriteria();
        criteria.andEqualTo(col, val);
        return (List<T>) mapper.selectByExample(exp);
    }
}