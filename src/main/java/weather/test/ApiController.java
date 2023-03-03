package weather.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    @Value("${resources.location}")
    private String resourceLocation;

    private final EntityManager em;

    @PostMapping("/region")
    @Transactional
    public ResponseEntity<String> resetRegionList() {
        String fileLocation = resourceLocation + "/init/regionList.csv"; // 설정파일에 설정된 경로 뒤에 붙인다
        Path path = Paths.get(fileLocation);
        URI uri = path.toUri();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new UrlResource(uri).getInputStream()))
        ) {
            String line = br.readLine(); // head 떼기
            while ((line = br.readLine()) != null) {
                String[] splits = line.split(",");
                em.persist(new Region(Long.parseLong(splits[0]), splits[1], splits[2],
                        Integer.parseInt(splits[3]), Integer.parseInt(splits[4])));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("뭔가 오류가 생겼는데요");
        }
        return ResponseEntity.ok("초기화에 성공했습니다");
    }
}
