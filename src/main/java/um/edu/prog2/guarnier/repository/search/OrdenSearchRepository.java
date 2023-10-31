package um.edu.prog2.guarnier.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import um.edu.prog2.guarnier.domain.Orden;
import um.edu.prog2.guarnier.repository.OrdenRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Orden} entity.
 */
public interface OrdenSearchRepository extends ElasticsearchRepository<Orden, Long>, OrdenSearchRepositoryInternal {}

interface OrdenSearchRepositoryInternal {
    Stream<Orden> search(String query);

    Stream<Orden> search(Query query);

    void index(Orden entity);
}

class OrdenSearchRepositoryInternalImpl implements OrdenSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final OrdenRepository repository;

    OrdenSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, OrdenRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Orden> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Orden> search(Query query) {
        return elasticsearchTemplate.search(query, Orden.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Orden entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
