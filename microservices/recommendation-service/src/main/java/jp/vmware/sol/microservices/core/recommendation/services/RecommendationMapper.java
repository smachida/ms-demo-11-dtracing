package jp.vmware.sol.microservices.core.recommendation.services;

import jp.vmware.sol.api.core.recommendation.Recommendation;
import jp.vmware.sol.microservices.core.recommendation.persistence.RecommendationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mappings({
            @Mapping(target  = "rate", source = "entity.rating"),
            @Mapping(target  = "serviceAddress", ignore = true)
    })
    Recommendation entityToApi(RecommendationEntity entity);

    @Mappings({
            @Mapping(target  = "rating", source = "recommendation.rate"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    RecommendationEntity apiToEntity(Recommendation recommendation);

    List<Recommendation> entityListToApiList(List<RecommendationEntity> entities);
    List<RecommendationEntity> apiListToEntityList(List<Recommendation> recommendations);
}
