package us.supercheng.es.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import us.supercheng.es.pojo.Item;
import us.supercheng.es.service.IItemsService;
import us.supercheng.utils.PagedResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ItemsService implements IItemsService {

    @Autowired
    ElasticsearchTemplate template;

    @Override
    public PagedResult doSearchByKeywordsAndCatId(Map<String, Object> map, Integer pageNum, Integer pageSize) {
        String fld = "itemName",
               keyword = map.get("keywords").toString(),
               sort = map.get("sort").toString().trim();

        if (StringUtils.isBlank(sort) || (!sort.equals("price") && !sort.equals("sellCounts")))
            sort = fld;

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withHighlightFields(new HighlightBuilder.Field(fld))
                .withPageable(PageRequest.of(pageNum, pageSize))
                .withQuery(QueryBuilders.matchQuery(fld, keyword))
                .withSort(new FieldSortBuilder(sort).order(SortOrder.ASC));

        AggregatedPage<Item> itemList = this.template.queryForPage(queryBuilder.build(), Item.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<Item> ret = new ArrayList<>();

                for (SearchHit hit : searchResponse.getHits()) {
                    HighlightField highlightField = hit.getHighlightFields().get(fld);
                    Map<String, Object> map = hit.getSourceAsMap();
                    Item item = new Item();
                    item.setSellCounts((Integer)map.get("sellCounts"));
                    item.setItemId((String)map.get("itemId"));
                    item.setImgUrl((String)map.get("imgUrl"));
                    item.setUpdatedTime((String)map.get("updated_time"));
                    item.setPrice(Integer.valueOf(map.get("price").toString()));
                    item.setItemName(highlightField.getFragments()[0].toString());
                    ret.add(item);
                }

                return new AggregatedPageImpl<>((List<T>) ret, pageable, searchResponse.getHits().getTotalHits());
            }
        });

        PagedResult ret = new PagedResult();
        ret.setTotal(itemList.getTotalPages());
        ret.setPage(pageNum+1);
        ret.setRecords(itemList.getTotalElements());
        ret.setRows(itemList.getContent());

        return ret;
    }
}
