package us.supercheng.es.service;

import us.supercheng.utils.PagedResult;
import java.util.Map;

public interface IItemsService {
    PagedResult doSearchByKeywordsAndCatId(Map<String, Object> map, Integer pageNum, Integer pageSize);
}
