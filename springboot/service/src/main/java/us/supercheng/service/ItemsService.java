package us.supercheng.service;

import us.supercheng.pojo.Items;
import us.supercheng.pojo.ItemsImg;
import us.supercheng.pojo.ItemsParam;
import us.supercheng.pojo.ItemsSpec;
import us.supercheng.utils.PagedResult;
import us.supercheng.vo.ItemCommentsSummaryVO;
import us.supercheng.vo.ShopcartItemVO;
import java.util.List;
import java.util.Map;

public interface ItemsService {
    Items queryItemById(String id);
    List<ItemsImg> queryItemImgListByItemId(String id);
    ItemsImg getItemsImgMainByItemId(String itemId);
    List<ItemsSpec> queryItemSpacListByItemId(String id);
    ItemsParam queryItemParamByItemId(String id);
    ItemCommentsSummaryVO getItemCommentsSummary(String id);
    Integer getItemCommentCountByLevel(String id, Integer level);
    PagedResult getComments(Map<String, Object> map, Integer pageNum, Integer pageSize);
    PagedResult doSearchByKeywordsAndCatId(Map<String, Object> map, Integer pageNum, Integer pageSize);
    List<ShopcartItemVO> getItemsBySpecIds(List<String> ids);
    ItemsSpec getItemsSpecByItemsSpecId(String id);
    Items getItemsByItemsId(String id);
    void updateItemsSpecCount(Map<String, Object> map);
}