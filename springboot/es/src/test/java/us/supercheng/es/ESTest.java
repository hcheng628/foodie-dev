package us.supercheng.es;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;
import us.supercheng.es.pojo.Stu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ESTest {

    @Autowired
    ElasticsearchTemplate template;


    @Test
    public void createIdxStu() {
        Stu stu = new Stu();

        stu.setName("Moka");
        stu.setAge(2);
        stu.setStuId(1L);
        stu.setMoney(19.8f);
        stu.setSign("Moka Pot");
        stu.setDesc("I am a Moka Pot");

        IndexQuery query = new IndexQueryBuilder()
                .withObject(stu).build();
        this.template.index(query);

        stu.setName("Nespresso");
        stu.setAge(5);
        stu.setStuId(2L);
        stu.setMoney(29.8f);
        stu.setSign("Moka Pot Espresso");
        stu.setDesc("I am a Moka Pot Espresso");

        query = new IndexQueryBuilder()
                .withObject(stu).build();
        this.template.index(query);

        stu.setName("Illy Machine");
        stu.setAge(8);
        stu.setStuId(3L);
        stu.setMoney(39.8f);
        stu.setSign("illy Machine");
        stu.setDesc("I am an illy Machine");

        query = new IndexQueryBuilder()
                .withObject(stu).build();
        this.template.index(query);

        stu.setName("Lavazza");
        stu.setAge(12);
        stu.setStuId(4L);
        stu.setMoney(5.8f);
        stu.setSign("Lavazza Machine");
        stu.setDesc("I am a Lavazza Machine");

        query = new IndexQueryBuilder()
                .withObject(stu).build();
        this.template.index(query);
    }

    @Test
    public void deleteIdxStu() {
        this.template.deleteIndex(Stu.class);
    }

    @Test
    public void updateStuDoc() {
        Map<String, Object> map = new HashMap<>();

        map.put("sign", "Espresso is good");
        map.put("money", 66.6f);

        IndexRequest request = new IndexRequest();
        request.source(map);

        UpdateQuery updateQuery =
                new UpdateQueryBuilder()
                        .withId("1")
                        .withIndexRequest(request)
                        .withClass(Stu.class)
                        .build();
        this.template.update(updateQuery);
    }

    @Test
    public void getStuDocById() {
        GetQuery query = new GetQuery();
        query.setId("1");
        System.out.println(this.template.queryForObject(query, Stu.class));
    }

    @Test
    public void deleteStuDocById() {
        this.template.delete(Stu.class, "1");
    }

    @Test
    public void queryStuDoc() {
        int page = 0,
            pageSize = 2,
            lastPage = -1;

        Pageable pageable = PageRequest.of(page, pageSize);

        while (page != lastPage) {
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withPageable(pageable)
                    .withQuery(QueryBuilders.matchQuery("desc", "moka machine"))
                    .build();
            AggregatedPage<Stu> stuList = this.template.queryForPage(searchQuery, Stu.class);

            for (Stu stu : stuList.getContent())
                System.out.println(stu);

            if (lastPage == -1)
                lastPage = stuList.getTotalPages();

            pageable = PageRequest.of(++page, pageSize);
        }
    }

    @Test
    public void queryAndHighlightStuDoc() {
        String preTag = "<font color='red'>",
               postTag = "</font>",
               fld = "desc";

        int page = 0,
            pageSize = 10,
            lastPage = -1;

        SortBuilder sortMoney = new FieldSortBuilder("money").order(SortOrder.ASC),
                    sortAge = new FieldSortBuilder("age").order(SortOrder.DESC);

        Pageable pageable = PageRequest.of(page, pageSize);

        while (page != lastPage) {
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withHighlightFields(new HighlightBuilder.Field(fld).
                            preTags(preTag).
                            postTags(postTag))
                    .withPageable(pageable)
                    .withQuery(QueryBuilders.matchQuery(fld, "moka machine"))
                    .withSort(sortAge)
                    .withSort(sortMoney)
                    .build();
            AggregatedPage<Stu> stuList = this.template.queryForPage(searchQuery, Stu.class, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                    List<Stu> ret = new ArrayList<>();

                    for (SearchHit hit : searchResponse.getHits()) {
                        HighlightField highlightField = hit.getHighlightFields().get(fld);
                        Map<String, Object> map = hit.getSourceAsMap();
                        Stu stu = new Stu();
                        stu.setSign((String)map.get("sign"));
                        stu.setMoney(Float.valueOf(map.get("money").toString()));
                        stu.setAge((Integer)map.get("age"));
                        stu.setName((String)map.get("name"));
                        stu.setDesc(highlightField.getFragments()[0].toString());
                        stu.setStuId(Long.valueOf(map.get("stuId").toString()));
                        ret.add(stu);
                    }

                    return new AggregatedPageImpl<>((List<T>) ret);
                }
            });

            for (Stu stu : stuList.getContent())
                System.out.println(stu);

            if (lastPage == -1)
                lastPage = stuList.getTotalPages();

            pageable = PageRequest.of(++page, pageSize);
        }
    }
}
