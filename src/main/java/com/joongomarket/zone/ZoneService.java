package com.joongomarket.zone;

import com.joongomarket.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    @PostConstruct
    public void initZoneData() throws IOException {
        if(zoneRepository.count() == 0){
            Resource resource = new ClassPathResource("zones_kr.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            List<Zone> zoneList = new ArrayList<>();
            while(true){
                String line = br.readLine();
                if(line == null){
                    break;
                }
                String[] split = line.split(",");
                zoneList.add(Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build());
            }
            zoneRepository.saveAll(zoneList);
        }
    }
}
