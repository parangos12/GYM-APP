package com.epam.gym.services.impl;

import java.security.Principal;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.epam.gym.config.JwtService;
import com.epam.gym.config.LoginAttemptService;
import com.epam.gym.dto.AuthenticationResponse;
import com.epam.gym.dto.ChangePasswordRequest;
import com.epam.gym.dto.AuthenticationDTO;
import com.epam.gym.repositories.TokenRepository;
import com.epam.gym.repositories.UserRepository;
import com.epam.gym.services.AuthenticationService;
import com.epam.gym.entities.Token;
import com.epam.gym.entities.TokenType;
import com.epam.gym.entities.User;
import com.epam.gym.exceptions.LogginDeniedException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{
	
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepo;
	private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
	private final TokenRepository tokenRepository;
    private final LoginAttemptService attemptService;
	
//    @Override
//    public AuthenticationResponse authenticate(@Valid AuthenticationDTO authDto) {
//        
//        String username = authDto.getUsername();
//        
//        if(attemptService.isBlocked(username)) {
//            throw new LogginDeniedException(username,"msg"); 
//        }
//        
//        authenticateUser(authDto);
//        
//        attemptService.loginSucceeded(username);
//            
//        var user = userRepo.findByUsername(username).orElseThrow();
//        var jwtToken = jwtService.generateToken(user);
//            
//        revokeAllUserTokens(user); 
//        saveUserToken(user, jwtToken);
//            
//        return AuthenticationResponse.builder().token(jwtToken).build();
//    }
  @Override
  public AuthenticationResponse authenticate(@Valid AuthenticationDTO authDto) {
      
      String username = authDto.getUsername();
      
      if(attemptService.isBlocked(username)) {
          throw new LogginDeniedException(username,"has been blocked. Try later after 1 min"); 
      }
      
      authenticateUser(authDto);
      
      attemptService.loginSucceeded(username);
          
      var user = userRepo.findByUsername(username).orElseThrow();
      var jwtToken = jwtService.generateToken(user);
          
      revokeAllUserTokens(user); 
      saveUserToken(user, jwtToken);
          
      return AuthenticationResponse.builder().token(jwtToken).build();
  }

    private void authenticateUser(AuthenticationDTO authDto) {
       try {
           authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authDto.getUsername(),
                    authDto.getPassword()
               )
           );
       } catch (Exception e) {
           attemptService.loginFailed(authDto.getUsername());
           throw new LogginDeniedException(authDto.getUsername()," has entered wrong credentials"); 
       }
    }

	  private void revokeAllUserTokens(User user) {
		    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
		    if (validUserTokens.isEmpty())
		      return;
		    validUserTokens.forEach(token -> {
		      token.setExpired(true);
		      token.setRevoked(true);
		    });
		    tokenRepository.saveAll(validUserTokens);
		  }
	  private void saveUserToken(User user, String jwtToken) {
		    var token = Token.builder()
		        .user(user)
		        .token(jwtToken)
		        .tokenType(TokenType.BEARER)
		        .expired(false)
		        .revoked(false)
		        .build();
		    tokenRepository.save(token);
		  }

	@Override
	public boolean hasPermission(String username, String role) {
		Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
		UserDetails user= (UserDetails) authentication.getPrincipal();
		return username.equals(authentication.getName()) && user.getAuthorities().stream().anyMatch(authority->authority.getAuthority().equals(role));
	}
	@Override
	public String actualUsername() {
		Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
		UserDetails user= (UserDetails) authentication.getPrincipal();
		return user.getUsername();
	}

	@Override
	public void changePassword(String username,ChangePasswordRequest request, Principal connectedUser) {
		var user= (User)((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
	        throw new IllegalStateException("Wrong password");
		}
		if(!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
		}
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepo.save(user);
	}

}
