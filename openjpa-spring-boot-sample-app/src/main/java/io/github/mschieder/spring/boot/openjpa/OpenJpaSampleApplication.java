package io.github.mschieder.spring.boot.openjpa;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class OpenJpaSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenJpaSampleApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(PersonRepository repository, ApplicationContext context) {
        return (args) -> {
            // save a few customers
            repository.save(new Person("Jack", "Bauer"));
            repository.save(new Person("Chloe", "O'Brian"));
            repository.save(new Person("Kim", "Bauer"));
            repository.save(new Person("David", "Palmer"));
            repository.save(new Person("Michelle", "Dessler"));

            System.out.println(repository.findById(1L).get());
            System.out.println(context.getBeansOfType(DataSource.class));


        };
    }


}
