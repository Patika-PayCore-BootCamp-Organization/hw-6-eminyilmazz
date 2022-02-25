package com.loanapp.loanapplication.config;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.loanapp.loanapplication.controller.CustomerController;
import com.loanapp.loanapplication.model.dto.CustomerDto;
import com.loanapp.loanapplication.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {
    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket customerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
//                .additionalModels(typeResolver.resolve(CustomerDto.class))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.loanapp.loanapplication.controller"))
                .paths(PathSelectors.regex("/customer/.*"))
                .build().apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Loan Service",
                "Loan Application Service of a Bank",
                "v1",
                "",
                "",
                "",
                "https://swagger.io/docs/");
    }
}
