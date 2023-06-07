package com.luv2code.springboot.demosecurity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import javax.sql.DataSource;
import java.security.Security;

@Configuration
public class DemoSecurityConfig {
    //ADD SUPPORT FOR JDBC... no more hardcoded users
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource){
        //return  new JdbcUserDetailsManager(dataSource);
        // this code in line 17 tells spring security to use jdbc authentication with our datasource
        // spring security knows it's using a predefined table schemas
        //  spring security will look at two tables users and roles
        // it knows the column to use as all the info is stored on the database
        /*.......................................................................*/

        //using a custom table we use a custom query to tell spring security the table name
        // and column names it should use

        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        //define query to retrieve user by username
        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select user_id, pw, active from members where user_id=?"
        );
        //define query to retrieve the authorities/roles by username
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select user_id, role from roles where user_id=?"
        );

        return jdbcUserDetailsManager;


    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception{
     http.authorizeHttpRequests(configurer ->
             configurer
                     .requestMatchers("/").hasAnyRole("CEO","EMPLOYEE","MANAGER")
                     .requestMatchers("/leaders/**").hasRole("MANAGER")
                     .requestMatchers("/systems/**").hasRole("ADMIN")
                     .anyRequest().authenticated()
     )
             .formLogin(form ->
                     form
                             .loginPage("/showMyLoginPage")
                             .loginProcessingUrl("/authenticateTheUser")
                             .permitAll()
                     )
             .logout(logout ->logout.permitAll()
             )
             .exceptionHandling(configurer ->
                                 configurer.accessDeniedPage("/access-denied"));
         return http.build();
    }


//    @Bean
//    public InMemoryUserDetailsManager userDetailsManager(){
//
//        UserDetails john = User.builder()
//                .username("john")
//                .password("{noop}test123")
//                .roles("CEO")
//                .build();
//
//        UserDetails miya = User.builder()
//                .username("miya")
//                .password("{noop}test123")
//                .roles("EMPLOYEE")
//                .build();
//
//        UserDetails mary = User.builder()
//                .username("mary")
//                .password("{noop}test123")
//                .roles("ADMIN", "MANAGER", "EMPLOYEE")
//                .build();
//
//        UserDetails stephen = User.builder()
//                .username("stephen")
//                .password("{noop}test123")
//                .roles("MANAGER")
//                .build();
//
//        return new InMemoryUserDetailsManager(john, miya, mary, stephen);
//
//    }
}
