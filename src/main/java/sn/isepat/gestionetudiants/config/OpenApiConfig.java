package sn.isepat.gestionetudiants.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gestionEtudiantsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gestion des Étudiants - ISEP-AT")
                        .description("API REST CRUD pour la gestion des étudiants de l'ISEP-AT.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Dr Samba SIDIBE")
                                .email("ssidibe@ept.edu.sn")));
    }
}
