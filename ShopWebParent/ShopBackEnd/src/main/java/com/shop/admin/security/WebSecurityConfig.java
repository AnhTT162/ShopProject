package com.shop.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public UserDetailsService userDetailsService() {
		return new ShopUserDetailsService();
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		
		return authProvider;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/users/**").hasAuthority("Quản lý")
		.antMatchers("/categories/**").hasAnyAuthority("Quản lý","Nhân viên kho")
		.antMatchers("/brands/**").hasAnyAuthority("Quản lý","Nhân viên kho")
		.antMatchers("/products/**").hasAnyAuthority("Quản lý","Nhân viên kho","Nhân viên bán hàng","Nhân viên giao hàng")
		.antMatchers("/customers/**").hasAnyAuthority("Quản lý","Nhân viên bán hàng")
		.antMatchers("/shipping/**").hasAnyAuthority("Quản lý","Nhân viên bán hàng")
		.antMatchers("/report/**").hasAnyAuthority("Quản lý","Nhân viên bán hàng")
		.antMatchers("/orders/**").hasAnyAuthority("Quản lý","Nhân viên bán hàng","Nhân viên giao hàng")
		.antMatchers("/articles/**").hasAnyAuthority("Quản lý","Nhân viên kho")
		.antMatchers("/menus/**").hasAnyAuthority("Quản lý","Nhân viên kho")
		.antMatchers("/settings/**").hasAuthority("Quản lý")
		.anyRequest().authenticated()
		.and()
		.formLogin()
			.loginPage("/login")
			.usernameParameter("email")
			.permitAll()
		.and().logout().permitAll()
		.and().rememberMe().key("AbcDefgHijKlmnOpqrs_1234567890");
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}
	
	

}
