package demo;

import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
@RestController
@Configuration
@EnableResourceServer
public class ResourceApplication extends ResourceServerConfigurerAdapter {

    private static Logger LOG = Logger.getLogger(ResourceApplication.class);
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    @Value("${security.oauth2.resource.tokenInfoUri:}")
    private String tokenInfoUri;

    public static void main(String[] args) {

        SpringApplication.run(ResourceApplication.class, args);
    }

    @Bean
    public RemoteTokenServices tokenService() {

        if (!StringUtils.isEmpty(tokenInfoUri)) {
            RemoteTokenServices tokenService = new RemoteTokenServices();
            tokenService.setCheckTokenEndpointUrl(tokenInfoUri);
            tokenService.setClientId("acme");
            tokenService.setClientSecret("acmesecret");
            return tokenService;
        } else {
            return null;
        }
    }

    @RequestMapping("/")
    public Message home(HttpServletRequest request, HttpServletResponse response) {

        LOG.info("Authorization: " + request.getHeader("Authorization"));
        return new Message("Hello World");
    }

    @GetMapping("/custom")
    public Message custom() {

        return new Message("Hello World");
    }

    @GetMapping("/server-side-event")
    public SseEmitter serverSideEvent() {
        SseEmitter emitter = new SseEmitter();
        this.emitters.add(emitter);

        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));

        return emitter;
    }

    @Scheduled(fixedDelay = 5000)
    public void scheduledMessage() {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(new Message("pushed message from server"));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });

        emitters.remove(deadEmitters);
    }

    //    const eventSource = new EventSource('/api/resource/server-side-event/?access_token=eyJhbGc');
    //    eventSource.onmessage = function(e){console.log(e)}
    //    https://www.npmjs.com/package/react-eventsource
    @GetMapping("/get-supplier/{id}")
    public Map<String, String> getSupplier(@PathVariable("id") int id) {
        Map<String, String> supplier = new HashMap<>();
        supplier.put("companyName", "name" + id);
        supplier.put("email", "email@user" + id + ".com");
        return supplier;
    }

    @GetMapping("/get-suppliers")
    public Map<String, Object> getSuppliers() {
        List<Map<String, String>> suppliers = IntStream.range(0, 3).mapToObj(id -> {
            Map<String, String> supplier = new HashMap<>();
            supplier.put("id", Integer.toString(id));
            supplier.put("companyName", "name" + id);
            supplier.put("email", "email@user" + id + ".com");
            return supplier;
        }).collect(Collectors.toList());
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("content", suppliers);
        return map;

    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .requestMatchers().antMatchers("/**")
                .and()
                .authorizeRequests().anyRequest().access("#oauth2.hasScope('resource-read')");
        // @formatter:on
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("resource-id1");
    }


}

