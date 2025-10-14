package bg.logicsoft.pos_connector;

import bg.logicsoft.pos_connector.services.ERPNextInitDataLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {

    final ERPNextInitDataLoaderService dataLoader;

    @Override
    public void run(String... args) throws Exception {
        dataLoader.loadInitData();
    }
}
