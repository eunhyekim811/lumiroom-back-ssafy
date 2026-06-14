package com.ssafy.lumiroom.domain.model.service;

import com.ssafy.lumiroom.domain.model.dto.InfraResponse;
import com.ssafy.lumiroom.domain.model.mapper.InfraMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfraServiceImpl implements InfraService {

    private final InfraMapper infraMapper;

    @Override
    public List<InfraResponse> findInfra(double lat, double lng, double radius, String types) {
        Set<InfraType> requestedTypes = parseTypes(types);
        List<InfraResponse> result = new ArrayList<>();

        if (requestedTypes.contains(InfraType.CCTV)) {
            result.addAll(infraMapper.selectCctvs(lat, lng, radius));
        }
        if (requestedTypes.contains(InfraType.SECURITY_LIGHT)) {
            result.addAll(infraMapper.selectSecurityLights(lat, lng, radius));
        }
        if (requestedTypes.contains(InfraType.STREET_LIGHT)) {
            result.addAll(infraMapper.selectStreetLights(lat, lng, radius));
        }
        if (requestedTypes.contains(InfraType.POLICE)) {
            result.addAll(infraMapper.selectPoliceFacilities(lat, lng, radius));
        }

        return result;
    }

    private Set<InfraType> parseTypes(String types) {
        if (!StringUtils.hasText(types)) {
            return EnumSet.allOf(InfraType.class);
        }

        Set<String> typeNames = Arrays.stream(types.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        return Arrays.stream(InfraType.values())
                .filter(type -> typeNames.contains(type.frontendKey))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(InfraType.class)));
    }

    private enum InfraType {
        CCTV("cctv"),
        SECURITY_LIGHT("securityLight"),
        STREET_LIGHT("streetLight"),
        POLICE("police");

        private final String frontendKey;

        InfraType(String frontendKey) {
            this.frontendKey = frontendKey;
        }
    }
}
